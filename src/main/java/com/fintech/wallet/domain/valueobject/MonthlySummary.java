package com.fintech.wallet.domain.valueobject;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

/**
 * Value object representing a monthly spending summary.
 */
public record MonthlySummary(
    WalletId walletId,
    YearMonth month,
    Currency currency,
    BigDecimal totalDeposits,
    BigDecimal totalWithdrawals,
    BigDecimal totalTransfersIn,
    BigDecimal totalTransfersOut,
    BigDecimal netChange,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    int transactionCount,
    Map<String, BigDecimal> spendingByCategory
) {

    /**
     * Creates a summary with calculated net change.
     */
    public static MonthlySummary create(
            WalletId walletId,
            YearMonth month,
            Currency currency,
            BigDecimal totalDeposits,
            BigDecimal totalWithdrawals,
            BigDecimal totalTransfersIn,
            BigDecimal totalTransfersOut,
            BigDecimal openingBalance,
            BigDecimal closingBalance,
            int transactionCount,
            Map<String, BigDecimal> spendingByCategory) {

        BigDecimal netChange = totalDeposits.add(totalTransfersIn)
                .subtract(totalWithdrawals)
                .subtract(totalTransfersOut);

        return new MonthlySummary(
            walletId,
            month,
            currency,
            totalDeposits,
            totalWithdrawals,
            totalTransfersIn,
            totalTransfersOut,
            netChange,
            openingBalance,
            closingBalance,
            transactionCount,
            spendingByCategory
        );
    }
}
