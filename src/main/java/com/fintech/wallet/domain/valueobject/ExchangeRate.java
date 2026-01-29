package com.fintech.wallet.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

/**
 * Value object representing an exchange rate between two currencies.
 * Immutable and validated.
 */
public final class ExchangeRate {

    private static final int RATE_SCALE = 6;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final Currency sourceCurrency;
    private final Currency targetCurrency;
    private final BigDecimal rate;
    private final Instant timestamp;

    private ExchangeRate(Currency sourceCurrency, Currency targetCurrency, BigDecimal rate, Instant timestamp) {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate.setScale(RATE_SCALE, ROUNDING_MODE);
        this.timestamp = timestamp;
    }

    public static ExchangeRate of(Currency sourceCurrency, Currency targetCurrency, 
                                   BigDecimal rate, Instant timestamp) {
        Objects.requireNonNull(sourceCurrency, "Source currency cannot be null");
        Objects.requireNonNull(targetCurrency, "Target currency cannot be null");
        Objects.requireNonNull(rate, "Rate cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }

        return new ExchangeRate(sourceCurrency, targetCurrency, rate, timestamp);
    }

    public static ExchangeRate of(String sourceCurrencyCode, String targetCurrencyCode,
                                   BigDecimal rate, Instant timestamp) {
        return of(Currency.of(sourceCurrencyCode), Currency.of(targetCurrencyCode), rate, timestamp);
    }

    /**
     * Converts the given amount from source currency to target currency.
     */
    public Money convert(Money amount) {
        if (!amount.getCurrency().isSameAs(sourceCurrency)) {
            throw new IllegalArgumentException(
                String.format("Amount currency %s does not match exchange rate source currency %s",
                    amount.getCurrency(), sourceCurrency));
        }

        BigDecimal convertedAmount = amount.getAmount()
            .multiply(rate)
            .setScale(2, ROUNDING_MODE);

        return Money.of(convertedAmount, targetCurrency);
    }

    /**
     * Returns the inverse exchange rate (target to source).
     */
    public ExchangeRate invert() {
        BigDecimal invertedRate = BigDecimal.ONE.divide(rate, RATE_SCALE, ROUNDING_MODE);
        return new ExchangeRate(targetCurrency, sourceCurrency, invertedRate, timestamp);
    }

    public Currency getSourceCurrency() {
        return sourceCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Checks if this exchange rate is stale (older than specified minutes).
     */
    public boolean isStale(int maxAgeMinutes) {
        return timestamp.plusSeconds(maxAgeMinutes * 60L).isBefore(Instant.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(sourceCurrency, that.sourceCurrency) &&
               Objects.equals(targetCurrency, that.targetCurrency) &&
               rate.compareTo(that.rate) == 0 &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceCurrency, targetCurrency, rate, timestamp);
    }

    @Override
    public String toString() {
        return String.format("1 %s = %s %s (as of %s)",
            sourceCurrency, rate.toPlainString(), targetCurrency, timestamp);
    }
}
