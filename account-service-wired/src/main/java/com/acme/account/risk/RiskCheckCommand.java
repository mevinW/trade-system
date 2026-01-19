package com.acme.account.risk;

import java.math.BigDecimal;

public record RiskCheckCommand(
    String accountId,
    String orderId,
    String symbol,
    String side,
    long qty,
    BigDecimal limitPrice
) {}
