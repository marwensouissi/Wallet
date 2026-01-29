package com.fintech.wallet.infrastructure.persistence.entity;

/**
 * JPA enumeration for scheduled payment status.
 */
public enum ScheduledPaymentStatusJpa {
    ACTIVE,
    PAUSED,
    COMPLETED,
    CANCELLED,
    FAILED
}
