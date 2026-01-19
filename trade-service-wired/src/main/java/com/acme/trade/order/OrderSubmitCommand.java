package com.acme.trade.order;

import java.math.BigDecimal;

public record OrderSubmitCommand(
    String accountId,
    String clientOrderId,
    String symbol,
    String side,
    long qty,
    BigDecimal limitPrice
) {}
