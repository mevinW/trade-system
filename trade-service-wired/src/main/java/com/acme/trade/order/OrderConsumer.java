package com.acme.trade.order;

import com.acme.trade.messaging.Envelope;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {
  private final OrderRepository repo;
  private final KafkaTemplate<String,Object> kafka;

  public OrderConsumer(OrderRepository repo, KafkaTemplate<String,Object> kafka) {
    this.repo = repo;
    this.kafka = kafka;
  }

  @KafkaListener(topics = "order.commands")
  public void onMessage(java.util.Map<String, Object> env) {
    System.out.println("TradeService envelope keys = " + env.keySet());

    Object payloadObj = env.get("payload");
    if (!(payloadObj instanceof java.util.Map<?, ?> p)) {
      System.out.println("payload is not a map -> " + payloadObj);
      return;
    }

    OrderSubmitCommand cmd = new OrderSubmitCommand(
            p.get("accountId").toString(),
            p.get("clientOrderId").toString(),
            p.get("symbol").toString(),
            p.get("side").toString(),
            Long.parseLong(p.get("qty").toString()),
            new java.math.BigDecimal(p.get("limitPrice").toString())
    );

    String orderId = repo.create(cmd);

    RiskCheckCommand risk = new RiskCheckCommand(
            cmd.accountId(), orderId, cmd.symbol(), cmd.side(), cmd.qty(), cmd.limitPrice()
    );

    Envelope<RiskCheckCommand> out =
            new Envelope<>(java.util.UUID.randomUUID().toString(), "RiskCheckCommand", java.time.Instant.now(), risk);

    kafka.send("risk.commands", cmd.accountId(), out);

    System.out.println("Inserted orderId=" + orderId + " and published risk.commands");
  }
}
