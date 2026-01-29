package com.fintech.wallet.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for domain events.
 * All domain events should extend this class.
 */
public abstract class DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    /**
     * Returns the type of the event for routing and logging.
     */
    public abstract String getEventType();
}
