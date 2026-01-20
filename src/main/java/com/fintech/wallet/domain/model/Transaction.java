package com.fintech.wallet.domain.model;

import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.time.Instant;
import java.util.Objects;

/**
 * Transaction entity representing a money transfer between wallets.
 * Immutable once created.
 */
public final class Transaction {

    private final TransactionId id;
    private final WalletId sourceWalletId;
    private final WalletId destinationWalletId;
    private final Money amount;
    private final String description;
    private final TransactionStatus status;
    private final Instant createdAt;

    private Transaction(Builder builder) {
        this.id = builder.id;
        this.sourceWalletId = builder.sourceWalletId;
        this.destinationWalletId = builder.destinationWalletId;
        this.amount = builder.amount;
        this.description = builder.description;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Transaction createTransfer(WalletId sourceWalletId, WalletId destinationWalletId,
            Money amount, String description) {
        return builder()
                .id(TransactionId.generate())
                .sourceWalletId(sourceWalletId)
                .destinationWalletId(destinationWalletId)
                .amount(amount)
                .description(description)
                .status(TransactionStatus.COMPLETED)
                .createdAt(Instant.now())
                .build();
    }

    public TransactionId getId() {
        return id;
    }

    public WalletId getSourceWalletId() {
        return sourceWalletId;
    }

    public WalletId getDestinationWalletId() {
        return destinationWalletId;
    }

    public Money getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Transaction{id=%s, from=%s, to=%s, amount=%s, status=%s}",
                id, sourceWalletId, destinationWalletId, amount, status);
    }

    public static final class Builder {
        private TransactionId id;
        private WalletId sourceWalletId;
        private WalletId destinationWalletId;
        private Money amount;
        private String description;
        private TransactionStatus status;
        private Instant createdAt;

        private Builder() {
        }

        public Builder id(TransactionId id) {
            this.id = id;
            return this;
        }

        public Builder sourceWalletId(WalletId sourceWalletId) {
            this.sourceWalletId = sourceWalletId;
            return this;
        }

        public Builder destinationWalletId(WalletId destinationWalletId) {
            this.destinationWalletId = destinationWalletId;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Transaction build() {
            Objects.requireNonNull(id, "Transaction ID is required");
            Objects.requireNonNull(sourceWalletId, "Source wallet ID is required");
            Objects.requireNonNull(destinationWalletId, "Destination wallet ID is required");
            Objects.requireNonNull(amount, "Amount is required");
            Objects.requireNonNull(status, "Status is required");
            Objects.requireNonNull(createdAt, "Created timestamp is required");

            if (sourceWalletId.equals(destinationWalletId)) {
                throw new IllegalArgumentException("Source and destination wallets must be different");
            }

            if (description == null || description.isBlank()) {
                this.description = "Transfer";
            }

            return new Transaction(this);
        }
    }
}
