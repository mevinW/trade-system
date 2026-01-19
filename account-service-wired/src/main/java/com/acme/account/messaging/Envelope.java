package com.acme.account.messaging;

import java.time.Instant;

public record Envelope<T>(
    String messageId,
    String type,
    Instant createdAt,
    T payload
) {}
