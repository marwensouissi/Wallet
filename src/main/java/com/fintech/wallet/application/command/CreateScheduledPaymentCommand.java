package com.fintech.wallet.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Command for creating a scheduled payment.
 */
public final class CreateScheduledPaymentCommand {

    private final String sourceWalletId;
    private final String destinationWalletId;
    private final BigDecimal amount;
    private final String currency;
    private final String description;
    private final String recurrencePattern;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int maxExecutions;

    public CreateScheduledPaymentCommand(
            String sourceWalletId,
            String destinationWalletId,
            BigDecimal amount,
            String currency,
            String description,
            String recurrencePattern,
            LocalDate startDate,
            LocalDate endDate,
            int maxExecutions) {

        Objects.requireNonNull(sourceWalletId, "Source wallet ID is required");
        Objects.requireNonNull(destinationWalletId, "Destination wallet ID is required");
        Objects.requireNonNull(amount, "Amount is required");
        Objects.requireNonNull(currency, "Currency is required");
        Objects.requireNonNull(recurrencePattern, "Recurrence pattern is required");
        Objects.requireNonNull(startDate, "Start date is required");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.amount = amount;
        this.currency = currency.toUpperCase();
        this.description = description != null ? description : "Scheduled payment";
        this.recurrencePattern = recurrencePattern.toUpperCase();
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxExecutions = maxExecutions;
    }

    public String getSourceWalletId() { return sourceWalletId; }
    public String getDestinationWalletId() { return destinationWalletId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public String getRecurrencePattern() { return recurrencePattern; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getMaxExecutions() { return maxExecutions; }
}
