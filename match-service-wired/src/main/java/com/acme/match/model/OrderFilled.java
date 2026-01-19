package com.acme.match.model;

import java.math.BigDecimal;

public record OrderFilled(
    String accountId,
    String orderId,
    String symbol,
    String side,
    long qty,
    BigDecimal fillPrice
) {}
