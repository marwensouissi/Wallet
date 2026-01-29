package com.fintech.wallet.domain.model;

import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.ScheduledPaymentId;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing a scheduled payment.
 * Supports both one-time future payments and recurring transfers.
 */
public final class ScheduledPayment {

    private final ScheduledPaymentId id;
    private final WalletId sourceWalletId;
    private final WalletId destinationWalletId;
    private final Money amount;
    private final String description;
    private final RecurrencePattern recurrencePattern;
    private final LocalDate startDate;
    private final LocalDate endDate; // null for indefinite recurring payments
    private final LocalDate nextExecutionDate;
    private final int executionCount;
    private final int maxExecutions; // 0 for unlimited
    private ScheduledPaymentStatus status;
    private final Instant createdAt;
    private Instant lastModifiedAt;

    private ScheduledPayment(Builder builder) {
        this.id = builder.id;
        this.sourceWalletId = builder.sourceWalletId;
        this.destinationWalletId = builder.destinationWalletId;
        this.amount = builder.amount;
        this.description = builder.description;
        this.recurrencePattern = builder.recurrencePattern;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.nextExecutionDate = builder.nextExecutionDate;
        this.executionCount = builder.executionCount;
        this.maxExecutions = builder.maxExecutions;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a one-time scheduled payment for a future date.
     */
    public static ScheduledPayment createOneTime(
            WalletId sourceWalletId,
            WalletId destinationWalletId,
            Money amount,
            String description,
            LocalDate executionDate) {

        if (executionDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Execution date must be in the future");
        }

        return builder()
                .id(ScheduledPaymentId.generate())
                .sourceWalletId(sourceWalletId)
                .destinationWalletId(destinationWalletId)
                .amount(amount)
                .description(description)
                .recurrencePattern(RecurrencePattern.ONCE)
                .startDate(executionDate)
                .nextExecutionDate(executionDate)
                .maxExecutions(1)
                .status(ScheduledPaymentStatus.ACTIVE)
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .build();
    }

    /**
     * Creates a recurring scheduled payment.
     */
    public static ScheduledPayment createRecurring(
            WalletId sourceWalletId,
            WalletId destinationWalletId,
            Money amount,
            String description,
            RecurrencePattern pattern,
            LocalDate startDate,
            LocalDate endDate,
            int maxExecutions) {

        if (pattern == RecurrencePattern.ONCE) {
            throw new IllegalArgumentException("Use createOneTime for one-time payments");
        }

        return builder()
                .id(ScheduledPaymentId.generate())
                .sourceWalletId(sourceWalletId)
                .destinationWalletId(destinationWalletId)
                .amount(amount)
                .description(description)
                .recurrencePattern(pattern)
                .startDate(startDate)
                .endDate(endDate)
                .nextExecutionDate(startDate)
                .maxExecutions(maxExecutions)
                .status(ScheduledPaymentStatus.ACTIVE)
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .build();
    }

    /**
     * Calculates the next execution date based on the recurrence pattern.
     */
    public LocalDate calculateNextExecutionDate(LocalDate currentDate) {
        return switch (recurrencePattern) {
            case ONCE -> null; // No next date for one-time payments
            case DAILY -> currentDate.plusDays(1);
            case WEEKLY -> currentDate.plusWeeks(1);
            case BIWEEKLY -> currentDate.plusWeeks(2);
            case MONTHLY -> currentDate.plusMonths(1);
            case QUARTERLY -> currentDate.plusMonths(3);
            case YEARLY -> currentDate.plusYears(1);
        };
    }

    /**
     * Checks if this payment is due for execution.
     */
    public boolean isDue() {
        if (status != ScheduledPaymentStatus.ACTIVE) {
            return false;
        }
        return !nextExecutionDate.isAfter(LocalDate.now());
    }

    /**
     * Checks if this payment has reached its end condition.
     */
    public boolean isCompleted() {
        if (recurrencePattern == RecurrencePattern.ONCE && executionCount >= 1) {
            return true;
        }
        if (maxExecutions > 0 && executionCount >= maxExecutions) {
            return true;
        }
        if (endDate != null && LocalDate.now().isAfter(endDate)) {
            return true;
        }
        return false;
    }

    /**
     * Creates a copy with updated execution information after payment is processed.
     */
    public ScheduledPayment withExecution() {
        int newExecutionCount = this.executionCount + 1;
        LocalDate newNextDate = calculateNextExecutionDate(this.nextExecutionDate);
        ScheduledPaymentStatus newStatus = this.status;

        if (isCompleted() || (recurrencePattern == RecurrencePattern.ONCE)) {
            newStatus = ScheduledPaymentStatus.COMPLETED;
            newNextDate = null;
        } else if (endDate != null && newNextDate != null && newNextDate.isAfter(endDate)) {
            newStatus = ScheduledPaymentStatus.COMPLETED;
            newNextDate = null;
        }

        return builder()
                .id(this.id)
                .sourceWalletId(this.sourceWalletId)
                .destinationWalletId(this.destinationWalletId)
                .amount(this.amount)
                .description(this.description)
                .recurrencePattern(this.recurrencePattern)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .nextExecutionDate(newNextDate)
                .executionCount(newExecutionCount)
                .maxExecutions(this.maxExecutions)
                .status(newStatus)
                .createdAt(this.createdAt)
                .lastModifiedAt(Instant.now())
                .build();
    }

    public ScheduledPayment pause() {
        if (status != ScheduledPaymentStatus.ACTIVE) {
            throw new IllegalStateException("Can only pause active payments");
        }
        return builder()
                .id(this.id)
                .sourceWalletId(this.sourceWalletId)
                .destinationWalletId(this.destinationWalletId)
                .amount(this.amount)
                .description(this.description)
                .recurrencePattern(this.recurrencePattern)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .nextExecutionDate(this.nextExecutionDate)
                .executionCount(this.executionCount)
                .maxExecutions(this.maxExecutions)
                .status(ScheduledPaymentStatus.PAUSED)
                .createdAt(this.createdAt)
                .lastModifiedAt(Instant.now())
                .build();
    }

    public ScheduledPayment resume() {
        if (status != ScheduledPaymentStatus.PAUSED) {
            throw new IllegalStateException("Can only resume paused payments");
        }
        return builder()
                .id(this.id)
                .sourceWalletId(this.sourceWalletId)
                .destinationWalletId(this.destinationWalletId)
                .amount(this.amount)
                .description(this.description)
                .recurrencePattern(this.recurrencePattern)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .nextExecutionDate(this.nextExecutionDate)
                .executionCount(this.executionCount)
                .maxExecutions(this.maxExecutions)
                .status(ScheduledPaymentStatus.ACTIVE)
                .createdAt(this.createdAt)
                .lastModifiedAt(Instant.now())
                .build();
    }

    public ScheduledPayment cancel() {
        if (status == ScheduledPaymentStatus.COMPLETED || status == ScheduledPaymentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a completed or already cancelled payment");
        }
        return builder()
                .id(this.id)
                .sourceWalletId(this.sourceWalletId)
                .destinationWalletId(this.destinationWalletId)
                .amount(this.amount)
                .description(this.description)
                .recurrencePattern(this.recurrencePattern)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .nextExecutionDate(null)
                .executionCount(this.executionCount)
                .maxExecutions(this.maxExecutions)
                .status(ScheduledPaymentStatus.CANCELLED)
                .createdAt(this.createdAt)
                .lastModifiedAt(Instant.now())
                .build();
    }

    // Getters
    public ScheduledPaymentId getId() { return id; }
    public WalletId getSourceWalletId() { return sourceWalletId; }
    public WalletId getDestinationWalletId() { return destinationWalletId; }
    public Money getAmount() { return amount; }
    public String getDescription() { return description; }
    public RecurrencePattern getRecurrencePattern() { return recurrencePattern; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getNextExecutionDate() { return nextExecutionDate; }
    public int getExecutionCount() { return executionCount; }
    public int getMaxExecutions() { return maxExecutions; }
    public ScheduledPaymentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastModifiedAt() { return lastModifiedAt; }

    public static class Builder {
        private ScheduledPaymentId id;
        private WalletId sourceWalletId;
        private WalletId destinationWalletId;
        private Money amount;
        private String description;
        private RecurrencePattern recurrencePattern;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate nextExecutionDate;
        private int executionCount = 0;
        private int maxExecutions = 0;
        private ScheduledPaymentStatus status;
        private Instant createdAt;
        private Instant lastModifiedAt;

        public Builder id(ScheduledPaymentId id) { this.id = id; return this; }
        public Builder sourceWalletId(WalletId sourceWalletId) { this.sourceWalletId = sourceWalletId; return this; }
        public Builder destinationWalletId(WalletId destinationWalletId) { this.destinationWalletId = destinationWalletId; return this; }
        public Builder amount(Money amount) { this.amount = amount; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder recurrencePattern(RecurrencePattern pattern) { this.recurrencePattern = pattern; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder nextExecutionDate(LocalDate nextExecutionDate) { this.nextExecutionDate = nextExecutionDate; return this; }
        public Builder executionCount(int executionCount) { this.executionCount = executionCount; return this; }
        public Builder maxExecutions(int maxExecutions) { this.maxExecutions = maxExecutions; return this; }
        public Builder status(ScheduledPaymentStatus status) { this.status = status; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder lastModifiedAt(Instant lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; return this; }

        public ScheduledPayment build() {
            Objects.requireNonNull(id, "ID is required");
            Objects.requireNonNull(sourceWalletId, "Source wallet ID is required");
            Objects.requireNonNull(destinationWalletId, "Destination wallet ID is required");
            Objects.requireNonNull(amount, "Amount is required");
            Objects.requireNonNull(recurrencePattern, "Recurrence pattern is required");
            Objects.requireNonNull(startDate, "Start date is required");
            Objects.requireNonNull(status, "Status is required");
            return new ScheduledPayment(this);
        }
    }
}
