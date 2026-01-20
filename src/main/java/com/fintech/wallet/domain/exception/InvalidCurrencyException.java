package com.fintech.wallet.domain.exception;

/**
 * Thrown when an operation involves incompatible or invalid currencies.
 */
public class InvalidCurrencyException extends RuntimeException {

    public InvalidCurrencyException(String message) {
        super(message);
    }

    public InvalidCurrencyException(String sourceCurrency, String targetCurrency) {
        super(String.format(
                "Currency mismatch: cannot transfer between %s and %s",
                sourceCurrency, targetCurrency));
    }
}
