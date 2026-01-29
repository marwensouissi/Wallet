package com.fintech.wallet.domain.valueobject;

import java.time.LocalDate;
import java.util.List;

/**
 * Value object representing an account statement.
 */
public record AccountStatement(
    WalletId walletId,
    Currency currency,
    LocalDate startDate,
    LocalDate endDate,
    Money openingBalance,
    Money closingBalance,
    List<StatementEntry> entries,
    int totalTransactions
) {

    /**
     * Single entry in an account statement.
     */
    public record StatementEntry(
        java.time.Instant date,
        String type,
        String description,
        Money amount,
        Money runningBalance,
        String transactionId
    ) {}
}
