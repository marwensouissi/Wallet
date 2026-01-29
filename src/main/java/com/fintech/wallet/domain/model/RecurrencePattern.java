package com.fintech.wallet.domain.model;

/**
 * Enumeration of recurrence patterns for scheduled payments.
 */
public enum RecurrencePattern {
    ONCE,           // One-time future payment
    DAILY,          // Every day
    WEEKLY,         // Every week
    BIWEEKLY,       // Every two weeks
    MONTHLY,        // Every month
    QUARTERLY,      // Every 3 months
    YEARLY          // Every year
}
