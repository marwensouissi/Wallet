package com.fintech.wallet.domain.exception;

/**
 * Exception thrown when a scheduled payment is not found.
 */
public class ScheduledPaymentNotFoundException extends RuntimeException {

    private final String paymentId;

    public ScheduledPaymentNotFoundException(String paymentId) {
        super("Scheduled payment not found: " + paymentId);
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
