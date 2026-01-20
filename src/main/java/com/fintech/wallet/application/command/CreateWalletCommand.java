package com.fintech.wallet.application.command;

import java.util.Objects;

/**
 * Command for creating a new wallet.
 * Immutable command object following CQRS pattern.
 */
public final class CreateWalletCommand {

    private final String currency;

    public CreateWalletCommand(String currency) {
        Objects.requireNonNull(currency, "Currency is required");
        if (currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be blank");
        }
        this.currency = currency.trim().toUpperCase();
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return String.format("CreateWalletCommand{currency='%s'}", currency);
    }
}
