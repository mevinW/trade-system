package com.acme.match.consumer;

import com.acme.match.db.TradeOrderRepository;
import com.acme.match.messaging.Envelope;
import com.acme.match.model.OrderFilled;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RiskEventsConsumer {

  private final TradeOrderRepository orders;
  private final KafkaTemplate<String, Object> kafka;

  public RiskEventsConsumer(TradeOrderRepository orders, KafkaTemplate<String, Object> kafka) {
    this.orders = orders;
    this.kafka = kafka;
  }

  @KafkaListener(topics = "risk.events")
  public void onMessage(ConsumerRecord<String, Object> record) {
    Object raw = record.value();

    if (!(raw instanceof Map<?, ?> env)) return;

    String type = String.valueOf(env.get("type"));
    if (!"RiskApproved".equals(type)) return;

    Object payloadObj = env.get("payload");
    if (!(payloadObj instanceof Map<?, ?> p)) return;

    String accountId = String.valueOf(p.get("accountId"));
    String orderId = String.valueOf(p.get("orderId"));

    var snap = orders.approveAndLoad(orderId);
    if (snap == null) {
      System.out.println("MatchService: orderId not found in DB: " + orderId);
      return;
    }

    orders.markFilled(orderId);

    OrderFilled filled = new OrderFilled(
        accountId,
        orderId,
        snap.symbol(),
        snap.side(),
        snap.qty(),
        snap.limitPrice()
    );

    Envelope<OrderFilled> out = new Envelope<>(
        UUID.randomUUID().toString(),
        "OrderFilled",
        Instant.now(),
        filled
    );

    kafka.send("trade.events", accountId, out);
    System.out.println("MatchService: FILLED orderId=" + orderId + " published trade.events");
  }
}
