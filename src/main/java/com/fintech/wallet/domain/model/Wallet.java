package com.fintech.wallet.domain.model;

import com.fintech.wallet.domain.exception.InsufficientBalanceException;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Wallet aggregate root.
 * Balance is calculated from ledger entries (no stored balance column).
 * This approach prevents race conditions and provides a complete audit trail.
 */
public final class Wallet {

    private final WalletId id;
    private final Currency currency;
    private final List<LedgerEntry> ledgerEntries;
    private final Instant createdAt;

    private Wallet(WalletId id, Currency currency, List<LedgerEntry> ledgerEntries, Instant createdAt) {
        this.id = id;
        this.currency = currency;
        this.ledgerEntries = new ArrayList<>(ledgerEntries);
        this.createdAt = createdAt;
    }

    public static Wallet create(Currency currency) {
        return new Wallet(
                WalletId.generate(),
                currency,
                new ArrayList<>(),
                Instant.now());
    }

    public static Wallet reconstitute(WalletId id, Currency currency,
            List<LedgerEntry> ledgerEntries, Instant createdAt) {
        return new Wallet(id, currency, ledgerEntries, createdAt);
    }

    /**
     * Calculates the current balance by summing all ledger entries.
     * Credits add to balance, debits subtract from balance.
     */
    public Money calculateBalance() {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;

        for (LedgerEntry entry : ledgerEntries) {
            if (entry.isCredit()) {
                total = total.add(entry.getAmount().getAmount());
            } else {
                total = total.subtract(entry.getAmount().getAmount());
            }
        }

        // Balance should never be negative if business rules are enforced
        if (total.compareTo(java.math.BigDecimal.ZERO) < 0) {
            total = java.math.BigDecimal.ZERO;
        }

        return Money.of(total, currency);
    }

    /**
     * Credits money to this wallet.
     * Creates and adds a credit ledger entry.
     */
    public LedgerEntry credit(Money amount, TransactionId transactionId, String description) {
        validateCurrency(amount);

        LedgerEntry creditEntry = LedgerEntry.createCredit(
                this.id,
                transactionId,
                amount,
                description);

        this.ledgerEntries.add(creditEntry);
        return creditEntry;
    }

    /**
     * Debits money from this wallet.
     * Validates sufficient balance before creating debit entry.
     *
     * @throws InsufficientBalanceException if balance is insufficient
     */
    public LedgerEntry debit(Money amount, TransactionId transactionId, String description) {
        validateCurrency(amount);
        validateSufficientBalance(amount);

        LedgerEntry debitEntry = LedgerEntry.createDebit(
                this.id,
                transactionId,
                amount,
                description);

        this.ledgerEntries.add(debitEntry);
        return debitEntry;
    }

    /**
     * Checks if this wallet can support a debit of the given amount.
     */
    public boolean canDebit(Money amount) {
        validateCurrency(amount);
        Money balance = calculateBalance();
        return balance.isGreaterThanOrEqual(amount);
    }

    private void validateCurrency(Money amount) {
        if (!amount.getCurrency().isSameAs(this.currency)) {
            throw new com.fintech.wallet.domain.exception.InvalidCurrencyException(
                    String.format("Wallet currency %s does not match amount currency %s",
                            this.currency, amount.getCurrency()));
        }
    }

    private void validateSufficientBalance(Money amount) {
        Money balance = calculateBalance();
        if (!balance.isGreaterThanOrEqual(amount)) {
            throw new InsufficientBalanceException(
                    this.id.toString(),
                    amount.toString(),
                    balance.toString());
        }
    }

    public WalletId getId() {
        return id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public List<LedgerEntry> getLedgerEntries() {
        return Collections.unmodifiableList(ledgerEntries);
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
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Wallet{id=%s, currency=%s, entries=%d}",
                id, currency, ledgerEntries.size());
    }
}
