package com.fintech.wallet.domain.service;

import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.ExchangeRate;
import com.fintech.wallet.domain.valueobject.Money;

import java.util.Objects;

/**
 * Domain service for currency exchange operations.
 * Contains business logic for cross-currency transfers.
 */
public class CurrencyExchangeDomainService {

    private static final int MAX_RATE_AGE_MINUTES = 15;

    /**
     * Converts money from one currency to another using the provided exchange rate.
     * Validates that the exchange rate is not stale.
     *
     * @param amount the amount to convert
     * @param exchangeRate the exchange rate to use
     * @return the converted money in the target currency
     * @throws IllegalArgumentException if the exchange rate is stale or currencies don't match
     */
    public Money convert(Money amount, ExchangeRate exchangeRate) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(exchangeRate, "Exchange rate cannot be null");

        validateExchangeRateNotStale(exchangeRate);
        validateCurrencyMatch(amount.getCurrency(), exchangeRate.getSourceCurrency());

        return exchangeRate.convert(amount);
    }

    /**
     * Calculates the exchange fee based on the amount.
     * Standard fee is 0.5% of the transaction amount.
     */
    public Money calculateExchangeFee(Money amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        
        java.math.BigDecimal feeRate = new java.math.BigDecimal("0.005");
        java.math.BigDecimal feeAmount = amount.getAmount()
            .multiply(feeRate)
            .setScale(2, java.math.RoundingMode.HALF_UP);

        return Money.of(feeAmount, amount.getCurrency());
    }

    private void validateExchangeRateNotStale(ExchangeRate rate) {
        if (rate.isStale(MAX_RATE_AGE_MINUTES)) {
            throw new IllegalStateException(
                String.format("Exchange rate is stale. Rate timestamp: %s, max age: %d minutes",
                    rate.getTimestamp(), MAX_RATE_AGE_MINUTES));
        }
    }

    private void validateCurrencyMatch(Currency amountCurrency, Currency rateCurrency) {
        if (!amountCurrency.isSameAs(rateCurrency)) {
            throw new IllegalArgumentException(
                String.format("Amount currency %s does not match exchange rate source currency %s",
                    amountCurrency, rateCurrency));
        }
    }
}
