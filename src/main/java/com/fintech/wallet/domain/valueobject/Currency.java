package com.fintech.wallet.domain.valueobject;

import java.util.Objects;
import java.util.Set;

/**
 * Value object representing a currency.
 * Immutable and validated against supported currencies.
 */
public final class Currency {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of(
            "USD", "EUR", "GBP", "CHF", "JPY", "CAD", "AUD", "NZD", "SGD", "HKD");

    private final String code;

    private Currency(String code) {
        this.code = code;
    }

    public static Currency of(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Currency code cannot be null or blank");
        }

        String normalizedCode = code.trim().toUpperCase();

        if (!SUPPORTED_CURRENCIES.contains(normalizedCode)) {
            throw new IllegalArgumentException("Unsupported currency: " + normalizedCode);
        }

        return new Currency(normalizedCode);
    }

    public String getCode() {
        return code;
    }

    public boolean isSameAs(Currency other) {
        if (other == null) {
            return false;
        }
        return this.code.equals(other.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Currency currency = (Currency) o;
        return Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
