package com.fintech.wallet.domain.event;

import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.domain.valueobject.WalletId;

/**
 * Event raised when money is transferred between wallets.
 */
public final class MoneyTransferredEvent extends DomainEvent {

    private final TransactionId transactionId;
    private final WalletId sourceWalletId;
    private final WalletId destinationWalletId;
    private final Money amount;
    private final String description;
    private final boolean crossCurrency;
    private final Money convertedAmount;

    public MoneyTransferredEvent(TransactionId transactionId, WalletId sourceWalletId, 
            WalletId destinationWalletId, Money amount, String description) {
        this(transactionId, sourceWalletId, destinationWalletId, amount, description, false, null);
    }

    public MoneyTransferredEvent(TransactionId transactionId, WalletId sourceWalletId, 
            WalletId destinationWalletId, Money amount, String description,
            boolean crossCurrency, Money convertedAmount) {
        super();
        this.transactionId = transactionId;
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.amount = amount;
        this.description = description;
        this.crossCurrency = crossCurrency;
        this.convertedAmount = convertedAmount;
    }

    @Override
    public String getEventType() {
        return crossCurrency ? "CROSS_CURRENCY_TRANSFER" : "MONEY_TRANSFERRED";
    }

    public TransactionId getTransactionId() {
        return transactionId;
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

    public boolean isCrossCurrency() {
        return crossCurrency;
    }

    public Money getConvertedAmount() {
        return convertedAmount;
    }
}
