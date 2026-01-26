package com.fintech.wallet.application.command;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command for withdrawing money from a wallet.
 * Immutable command object following CQRS pattern.
 */
public final class WithdrawMoneyCommand {

    private final String walletId;
    private final BigDecimal amount;
    private final String currency;
    private final String description;

    public WithdrawMoneyCommand(String walletId, BigDecimal amount, String currency, String description) {
        Objects.requireNonNull(walletId, "Wallet ID is required");
        Objects.requireNonNull(amount, "Amount is required");
        Objects.requireNonNull(currency, "Currency is required");

        if (walletId.isBlank()) {
            throw new IllegalArgumentException("Wallet ID cannot be blank");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be blank");
        }

        this.walletId = walletId.trim();
        this.amount = amount;
        this.currency = currency.trim().toUpperCase();
        this.description = description != null ? description.trim() : "Withdrawal";
    }

    public String getWalletId() {
        return walletId;
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
        return "WithdrawMoneyCommand{" +
                "walletId='" + walletId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
