package com.fintech.wallet.domain.event;

import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.WalletId;

/**
 * Event raised when money is withdrawn from a wallet.
 */
public final class MoneyWithdrawnEvent extends DomainEvent {

    private final WalletId walletId;
    private final Money amount;
    private final Money newBalance;
    private final String description;

    public MoneyWithdrawnEvent(WalletId walletId, Money amount, Money newBalance, String description) {
        super();
        this.walletId = walletId;
        this.amount = amount;
        this.newBalance = newBalance;
        this.description = description;
    }

    @Override
    public String getEventType() {
        return "MONEY_WITHDRAWN";
    }

    public WalletId getWalletId() {
        return walletId;
    }

    public Money getAmount() {
        return amount;
    }

    public Money getNewBalance() {
        return newBalance;
    }

    public String getDescription() {
        return description;
    }
}
