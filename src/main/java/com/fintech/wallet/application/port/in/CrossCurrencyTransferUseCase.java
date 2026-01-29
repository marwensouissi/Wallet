package com.fintech.wallet.application.port.in;

import com.fintech.wallet.application.command.CrossCurrencyTransferCommand;

/**
 * Input port for cross-currency transfer use case.
 * Handles transfers between wallets with different currencies.
 */
public interface CrossCurrencyTransferUseCase {

    /**
     * Executes a cross-currency transfer between wallets.
     *
     * @param command the transfer command with source/destination wallets and amounts
     * @return the result of the transfer including exchange rate used
     */
    CrossCurrencyTransferResult execute(CrossCurrencyTransferCommand command);

    /**
     * Result of a cross-currency transfer operation.
     */
    record CrossCurrencyTransferResult(
        String transactionId,
        java.math.BigDecimal sourceAmount,
        String sourceCurrency,
        java.math.BigDecimal targetAmount,
        String targetCurrency,
        java.math.BigDecimal exchangeRate,
        java.math.BigDecimal feeAmount,
        java.time.Instant timestamp
    ) {}
}
