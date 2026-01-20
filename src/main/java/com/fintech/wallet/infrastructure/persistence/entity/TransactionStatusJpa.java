package com.fintech.wallet.infrastructure.persistence.entity;

/**
 * JPA enumeration for transaction statuses.
 */
public enum TransactionStatusJpa {
    PENDING,
    COMPLETED,
    FAILED,
    REVERSED
}
