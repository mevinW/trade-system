package com.acme.gateway.order;

import java.math.BigDecimal;

public record OrderSubmitCommand(
    String accountId,
    String clientOrderId,
    String symbol,
    Side side,
    long qty,
    BigDecimal limitPrice
) {
  public enum Side { BUY, SELL }
}
