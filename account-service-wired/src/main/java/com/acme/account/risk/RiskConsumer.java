package com.acme.account.risk;

import com.acme.account.db.BalanceRepository;
import com.acme.account.messaging.Envelope;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RiskConsumer {

  private final BalanceRepository balances;
  private final KafkaTemplate<String, Object> kafka;

  public RiskConsumer(BalanceRepository balances, KafkaTemplate<String, Object> kafka) {
    this.balances = balances;
    this.kafka = kafka;
  }

  @KafkaListener(topics = "risk.commands")
  public void onMessage(ConsumerRecord<String, Object> record) {
    Object raw = record.value();

    System.out.println("AccountService received record key=" + record.key()
            + " valueClass=" + (raw == null ? "null" : raw.getClass().getName())
            + " value=" + raw);

    if (!(raw instanceof Map<?, ?> map)) return;

    String type = String.valueOf(map.get("type"));
    if (!"RiskCheckCommand".equals(type)) return;

    Object payloadObj = map.get("payload");
    if (!(payloadObj instanceof Map<?, ?> p)) return;

    String accountId = String.valueOf(p.get("accountId"));
    String orderId = String.valueOf(p.get("orderId"));
    String side = String.valueOf(p.get("side"));
    BigDecimal limitPrice = new BigDecimal(String.valueOf(p.get("limitPrice")));
    long qty = Long.parseLong(String.valueOf(p.get("qty")));

    // MVP: cash-only, limit-only, BUY only
    if (!"BUY".equalsIgnoreCase(side)) {
      publishRejected(accountId, orderId, "SELL_NOT_SUPPORTED_MVP");
      return;
    }

    BigDecimal notional = limitPrice.multiply(BigDecimal.valueOf(qty));
    boolean ok = balances.reserveIfEnough(accountId, notional);

    if (ok) {
      publishApproved(accountId, orderId);
    } else {
      publishRejected(accountId, orderId, "INSUFFICIENT_FUNDS");
    }
  }

  private void publishApproved(String accountId, String orderId) {
    var env = new Envelope<>(
            UUID.randomUUID().toString(),
            "RiskApproved",
            Instant.now(),
            new RiskApproved(accountId, orderId)
    );
    kafka.send("risk.events", accountId, env);
    System.out.println("Published RiskApproved for orderId=" + orderId);
  }

  private void publishRejected(String accountId, String orderId, String reason) {
    var env = new Envelope<>(
            UUID.randomUUID().toString(),
            "RiskRejected",
            Instant.now(),
            new RiskRejected(accountId, orderId, reason)
    );
    kafka.send("risk.events", accountId, env);
    System.out.println("Published RiskRejected for orderId=" + orderId + " reason=" + reason);
  }
}
