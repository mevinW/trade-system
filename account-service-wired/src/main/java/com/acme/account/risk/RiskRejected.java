package com.acme.account.risk;

public record RiskRejected(String accountId, String orderId, String reason) {}
