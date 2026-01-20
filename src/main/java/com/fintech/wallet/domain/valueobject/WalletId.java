package com.fintech.wallet.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique wallet identifier.
 */
public final class WalletId {

    private final UUID value;

    private WalletId(UUID value) {
        this.value = value;
    }

    public static WalletId generate() {
        return new WalletId(UUID.randomUUID());
    }

    public static WalletId of(UUID value) {
        Objects.requireNonNull(value, "Wallet ID cannot be null");
        return new WalletId(value);
    }

    public static WalletId of(String value) {
        Objects.requireNonNull(value, "Wallet ID cannot be null");
        return new WalletId(UUID.fromString(value));
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
        WalletId walletId = (WalletId) o;
        return Objects.equals(value, walletId.value);
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
