package com.fintech.wallet.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique ledger entry identifier.
 */
public final class LedgerEntryId {

    private final UUID value;

    private LedgerEntryId(UUID value) {
        this.value = value;
    }

    public static LedgerEntryId generate() {
        return new LedgerEntryId(UUID.randomUUID());
    }

    public static LedgerEntryId of(UUID value) {
        Objects.requireNonNull(value, "Ledger Entry ID cannot be null");
        return new LedgerEntryId(value);
    }

    public static LedgerEntryId of(String value) {
        Objects.requireNonNull(value, "Ledger Entry ID cannot be null");
        return new LedgerEntryId(UUID.fromString(value));
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
        LedgerEntryId that = (LedgerEntryId) o;
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
