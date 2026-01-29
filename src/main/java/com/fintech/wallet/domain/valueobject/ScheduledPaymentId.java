package com.fintech.wallet.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a scheduled payment ID.
 */
public final class ScheduledPaymentId {

    private final UUID value;

    private ScheduledPaymentId(UUID value) {
        this.value = value;
    }

    public static ScheduledPaymentId generate() {
        return new ScheduledPaymentId(UUID.randomUUID());
    }

    public static ScheduledPaymentId of(String value) {
        Objects.requireNonNull(value, "Scheduled payment ID cannot be null");
        try {
            return new ScheduledPaymentId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid scheduled payment ID format: " + value);
        }
    }

    public static ScheduledPaymentId of(UUID value) {
        Objects.requireNonNull(value, "Scheduled payment ID cannot be null");
        return new ScheduledPaymentId(value);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledPaymentId that = (ScheduledPaymentId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
