package com.fintech.wallet.application.command;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command for cross-currency transfer between wallets.
 * Supports transfers where source and destination wallets have different currencies.
 */
public final class CrossCurrencyTransferCommand {

    private final String sourceWalletId;
    private final String destinationWalletId;
    private final BigDecimal sourceAmount;
    private final String sourceCurrency;
    private final String targetCurrency;
    private final String description;

    public CrossCurrencyTransferCommand(String sourceWalletId, String destinationWalletId,
            BigDecimal sourceAmount, String sourceCurrency, String targetCurrency, String description) {
        Objects.requireNonNull(sourceWalletId, "Source wallet ID is required");
        Objects.requireNonNull(destinationWalletId, "Destination wallet ID is required");
        Objects.requireNonNull(sourceAmount, "Source amount is required");
        Objects.requireNonNull(sourceCurrency, "Source currency is required");
        Objects.requireNonNull(targetCurrency, "Target currency is required");

        if (sourceWalletId.isBlank()) {
            throw new IllegalArgumentException("Source wallet ID cannot be blank");
        }
        if (destinationWalletId.isBlank()) {
            throw new IllegalArgumentException("Destination wallet ID cannot be blank");
        }
        if (sourceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (sourceCurrency.isBlank()) {
            throw new IllegalArgumentException("Source currency cannot be blank");
        }
        if (targetCurrency.isBlank()) {
            throw new IllegalArgumentException("Target currency cannot be blank");
        }
        if (sourceWalletId.equals(destinationWalletId)) {
            throw new IllegalArgumentException("Source and destination wallets must be different");
        }

        this.sourceWalletId = sourceWalletId.trim();
        this.destinationWalletId = destinationWalletId.trim();
        this.sourceAmount = sourceAmount;
        this.sourceCurrency = sourceCurrency.trim().toUpperCase();
        this.targetCurrency = targetCurrency.trim().toUpperCase();
        this.description = description != null ? description.trim() : "Cross-currency transfer";
    }

    public String getSourceWalletId() {
        return sourceWalletId;
    }

    public String getDestinationWalletId() {
        return destinationWalletId;
    }

    public BigDecimal getSourceAmount() {
        return sourceAmount;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCrossCurrency() {
        return !sourceCurrency.equals(targetCurrency);
    }

    @Override
    public String toString() {
        return String.format(
                "CrossCurrencyTransferCommand{from='%s', to='%s', amount=%s %s -> %s}",
                sourceWalletId, destinationWalletId, sourceAmount, sourceCurrency, targetCurrency);
    }
}
