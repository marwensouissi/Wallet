package com.fintech.wallet.domain.service;

import com.fintech.wallet.domain.exception.InvalidCurrencyException;
import com.fintech.wallet.domain.model.LedgerEntry;
import com.fintech.wallet.domain.model.Transaction;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.Money;

import java.util.Objects;

/**
 * Domain service for transfer operations between wallets.
 * Encapsulates the business rules for money transfers.
 * 
 * Domain services contain domain logic that doesn't naturally fit
 * within a single aggregate but still belongs in the domain layer.
 */
public final class TransferDomainService {

    /**
     * Represents the result of a transfer operation.
     */
    public static final class TransferResult {
        private final Transaction transaction;
        private final LedgerEntry sourceEntry;
        private final LedgerEntry destinationEntry;

        private TransferResult(Transaction transaction, LedgerEntry sourceEntry, LedgerEntry destinationEntry) {
            this.transaction = transaction;
            this.sourceEntry = sourceEntry;
            this.destinationEntry = destinationEntry;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public LedgerEntry getSourceEntry() {
            return sourceEntry;
        }

        public LedgerEntry getDestinationEntry() {
            return destinationEntry;
        }
    }

    /**
     * Executes a transfer between two wallets.
     * 
     * Business rules:
     * - Both wallets must use the same currency
     * - Transfer amount currency must match wallet currency
     * - Source wallet must have sufficient balance
     * 
     * @param sourceWallet the wallet to debit
     * @param destinationWallet the wallet to credit
     * @param amount the amount to transfer
     * @param description optional description
     * @return the transfer result containing transaction and ledger entries
     * @throws InvalidCurrencyException if currencies don't match
     * @throws com.fintech.wallet.domain.exception.InsufficientBalanceException if balance is insufficient
     */
    public TransferResult transfer(Wallet sourceWallet, Wallet destinationWallet, 
                                   Money amount, String description) {
        Objects.requireNonNull(sourceWallet, "Source wallet is required");
        Objects.requireNonNull(destinationWallet, "Destination wallet is required");
        Objects.requireNonNull(amount, "Amount is required");

        validateSameCurrency(sourceWallet, destinationWallet);
        validateAmountCurrency(sourceWallet, amount);

        // Create the transaction record
        Transaction transaction = Transaction.createTransfer(
                sourceWallet.getId(),
                destinationWallet.getId(),
                amount,
                description);

        // Create ledger entries in both wallets
        LedgerEntry debitEntry = sourceWallet.debit(
                amount, 
                transaction.getId(), 
                buildDebitDescription(destinationWallet, description));

        LedgerEntry creditEntry = destinationWallet.credit(
                amount, 
                transaction.getId(), 
                buildCreditDescription(sourceWallet, description));

        return new TransferResult(transaction, debitEntry, creditEntry);
    }

    private void validateSameCurrency(Wallet source, Wallet destination) {
        if (!source.getCurrency().isSameAs(destination.getCurrency())) {
            throw new InvalidCurrencyException(
                    String.format("Source wallet currency %s does not match destination wallet currency %s",
                            source.getCurrency().getCode(),
                            destination.getCurrency().getCode()));
        }
    }

    private void validateAmountCurrency(Wallet wallet, Money amount) {
        if (!amount.getCurrency().isSameAs(wallet.getCurrency())) {
            throw new InvalidCurrencyException(
                    String.format("Transfer amount currency %s does not match wallet currency %s",
                            amount.getCurrency().getCode(),
                            wallet.getCurrency().getCode()));
        }
    }

    private String buildDebitDescription(Wallet destinationWallet, String description) {
        String baseDescription = "Transfer to " + destinationWallet.getId();
        return description != null && !description.isBlank() 
                ? baseDescription + " - " + description 
                : baseDescription;
    }

    private String buildCreditDescription(Wallet sourceWallet, String description) {
        String baseDescription = "Transfer from " + sourceWallet.getId();
        return description != null && !description.isBlank() 
                ? baseDescription + " - " + description 
                : baseDescription;
    }
}
