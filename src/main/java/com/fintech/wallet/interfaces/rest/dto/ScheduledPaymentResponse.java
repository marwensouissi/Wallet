package com.fintech.wallet.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Response DTO for scheduled payment.
 */
public class ScheduledPaymentResponse {

    private String id;
    private String sourceWalletId;
    private String destinationWalletId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String recurrencePattern;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextExecutionDate;
    private int executionCount;
    private int maxExecutions;
    private String status;
    private Instant createdAt;
    private Instant lastModifiedAt;

    private ScheduledPaymentResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getId() { return id; }
    public String getSourceWalletId() { return sourceWalletId; }
    public String getDestinationWalletId() { return destinationWalletId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public String getRecurrencePattern() { return recurrencePattern; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getNextExecutionDate() { return nextExecutionDate; }
    public int getExecutionCount() { return executionCount; }
    public int getMaxExecutions() { return maxExecutions; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastModifiedAt() { return lastModifiedAt; }

    public static class Builder {
        private final ScheduledPaymentResponse response = new ScheduledPaymentResponse();

        public Builder id(String id) { response.id = id; return this; }
        public Builder sourceWalletId(String sourceWalletId) { response.sourceWalletId = sourceWalletId; return this; }
        public Builder destinationWalletId(String destinationWalletId) { response.destinationWalletId = destinationWalletId; return this; }
        public Builder amount(BigDecimal amount) { response.amount = amount; return this; }
        public Builder currency(String currency) { response.currency = currency; return this; }
        public Builder description(String description) { response.description = description; return this; }
        public Builder recurrencePattern(String recurrencePattern) { response.recurrencePattern = recurrencePattern; return this; }
        public Builder startDate(LocalDate startDate) { response.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { response.endDate = endDate; return this; }
        public Builder nextExecutionDate(LocalDate nextExecutionDate) { response.nextExecutionDate = nextExecutionDate; return this; }
        public Builder executionCount(int executionCount) { response.executionCount = executionCount; return this; }
        public Builder maxExecutions(int maxExecutions) { response.maxExecutions = maxExecutions; return this; }
        public Builder status(String status) { response.status = status; return this; }
        public Builder createdAt(Instant createdAt) { response.createdAt = createdAt; return this; }
        public Builder lastModifiedAt(Instant lastModifiedAt) { response.lastModifiedAt = lastModifiedAt; return this; }

        public ScheduledPaymentResponse build() {
            return response;
        }
    }
}
