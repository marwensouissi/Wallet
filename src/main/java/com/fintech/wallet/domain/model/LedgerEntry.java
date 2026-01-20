package com.fintech.wallet.domain.model;

import com.fintech.wallet.domain.valueobject.LedgerEntryId;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable ledger entry representing a credit or debit to a wallet.
 * Each entry is linked to a transaction for audit purposes.
 */
public final class LedgerEntry {

    private final LedgerEntryId id;
    private final WalletId walletId;
    private final TransactionId transactionId;
    private final LedgerEntryType type;
    private final Money amount;
    private final String description;
    private final Instant createdAt;

    private LedgerEntry(Builder builder) {
        this.id = builder.id;
        this.walletId = builder.walletId;
        this.transactionId = builder.transactionId;
        this.type = builder.type;
        this.amount = builder.amount;
        this.description = builder.description;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static LedgerEntry createCredit(WalletId walletId, TransactionId transactionId,
            Money amount, String description) {
        return builder()
                .id(LedgerEntryId.generate())
                .walletId(walletId)
                .transactionId(transactionId)
                .type(LedgerEntryType.CREDIT)
                .amount(amount)
                .description(description)
                .createdAt(Instant.now())
                .build();
    }

    public static LedgerEntry createDebit(WalletId walletId, TransactionId transactionId,
            Money amount, String description) {
        return builder()
                .id(LedgerEntryId.generate())
                .walletId(walletId)
                .transactionId(transactionId)
                .type(LedgerEntryType.DEBIT)
                .amount(amount)
                .description(description)
                .createdAt(Instant.now())
                .build();
    }

    public boolean isCredit() {
        return type == LedgerEntryType.CREDIT;
    }

    public boolean isDebit() {
        return type == LedgerEntryType.DEBIT;
    }

    public Money getSignedAmount() {
        return isDebit() ? amount.negate() : amount;
    }

    public LedgerEntryId getId() {
        return id;
    }

    public WalletId getWalletId() {
        return walletId;
    }

    public TransactionId getTransactionId() {
        return transactionId;
    }

    public LedgerEntryType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
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
        LedgerEntry that = (LedgerEntry) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("LedgerEntry{id=%s, type=%s, amount=%s, wallet=%s}",
                id, type, amount, walletId);
    }

    public static final class Builder {
        private LedgerEntryId id;
        private WalletId walletId;
        private TransactionId transactionId;
        private LedgerEntryType type;
        private Money amount;
        private String description;
        private Instant createdAt;

        private Builder() {
        }

        public Builder id(LedgerEntryId id) {
            this.id = id;
            return this;
        }

        public Builder walletId(WalletId walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder transactionId(TransactionId transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder type(LedgerEntryType type) {
            this.type = type;
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

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LedgerEntry build() {
            Objects.requireNonNull(id, "Ledger entry ID is required");
            Objects.requireNonNull(walletId, "Wallet ID is required");
            Objects.requireNonNull(transactionId, "Transaction ID is required");
            Objects.requireNonNull(type, "Ledger entry type is required");
            Objects.requireNonNull(amount, "Amount is required");
            Objects.requireNonNull(createdAt, "Created timestamp is required");

            if (description == null || description.isBlank()) {
                this.description = type == LedgerEntryType.CREDIT ? "Credit" : "Debit";
            }

            return new LedgerEntry(this);
        }
    }
}
