package com.fintech.wallet.application.command;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command for transferring money between wallets.
 * Immutable command object following CQRS pattern.
 */
public final class TransferMoneyCommand {

    private final String sourceWalletId;
    private final String destinationWalletId;
    private final BigDecimal amount;
    private final String currency;
    private final String description;

    public TransferMoneyCommand(String sourceWalletId, String destinationWalletId,
            BigDecimal amount, String currency, String description) {
        Objects.requireNonNull(sourceWalletId, "Source wallet ID is required");
        Objects.requireNonNull(destinationWalletId, "Destination wallet ID is required");
        Objects.requireNonNull(amount, "Amount is required");
        Objects.requireNonNull(currency, "Currency is required");

        if (sourceWalletId.isBlank()) {
            throw new IllegalArgumentException("Source wallet ID cannot be blank");
        }
        if (destinationWalletId.isBlank()) {
            throw new IllegalArgumentException("Destination wallet ID cannot be blank");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be blank");
        }
        if (sourceWalletId.equals(destinationWalletId)) {
            throw new IllegalArgumentException("Source and destination wallets must be different");
        }

        this.sourceWalletId = sourceWalletId.trim();
        this.destinationWalletId = destinationWalletId.trim();
        this.amount = amount;
        this.currency = currency.trim().toUpperCase();
        this.description = description != null ? description.trim() : "Transfer";
    }

    public String getSourceWalletId() {
        return sourceWalletId;
    }

    public String getDestinationWalletId() {
        return destinationWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format(
                "TransferMoneyCommand{from='%s', to='%s', amount=%s %s}",
                sourceWalletId, destinationWalletId, amount, currency);
    }
}
