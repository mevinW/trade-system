package com.acme.trade.messaging;

import java.time.Instant;

public record Envelope<T>(
    String messageId,
    String type,
    Instant createdAt,
    T payload
) {}
