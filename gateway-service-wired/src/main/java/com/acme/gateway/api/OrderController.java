package com.acme.gateway.api;

import com.acme.gateway.account.AccountClient;
import com.acme.gateway.messaging.Envelope;
import com.acme.gateway.order.OrderSubmitCommand;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final AccountClient accountClient;
  private final KafkaTemplate<String, Object> kafka;

  public OrderController(AccountClient accountClient, KafkaTemplate<String, Object> kafka) {
    this.accountClient = accountClient;
    this.kafka = kafka;
  }

  @PostMapping
  public ResponseEntity<Void> submitOrder(
      @RequestHeader("X-API-Key") String apiKey,
      @RequestBody SubmitOrderRequest req
  ) {
    String accountId = accountClient.validateApiKeyAndGetAccountId(apiKey);

    var cmd = new OrderSubmitCommand(
        accountId,
        req.clientOrderId(),
        req.symbol(),
        req.side(),
        req.qty(),
        req.limitPrice()
    );

    var env = new Envelope<>(UUID.randomUUID().toString(), "OrderSubmitCommand", Instant.now(), cmd);

    kafka.send("order.commands", accountId, env);

    return ResponseEntity.accepted().build();
  }

  public record SubmitOrderRequest(
      String clientOrderId,
      String symbol,
      OrderSubmitCommand.Side side,
      long qty,
      BigDecimal limitPrice
  ) {}
}
