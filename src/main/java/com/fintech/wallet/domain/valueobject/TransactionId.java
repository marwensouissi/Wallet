package com.fintech.wallet.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique transaction identifier.
 */
public final class TransactionId {

    private final UUID value;

    private TransactionId(UUID value) {
        this.value = value;
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(UUID value) {
        Objects.requireNonNull(value, "Transaction ID cannot be null");
        return new TransactionId(value);
    }

    public static TransactionId of(String value) {
        Objects.requireNonNull(value, "Transaction ID cannot be null");
        return new TransactionId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TransactionId that = (TransactionId) o;
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
