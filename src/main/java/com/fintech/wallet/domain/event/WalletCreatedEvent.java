package com.fintech.wallet.domain.event;

import com.fintech.wallet.domain.valueobject.WalletId;

/**
 * Event raised when a new wallet is created.
 */
public final class WalletCreatedEvent extends DomainEvent {

    private final WalletId walletId;
    private final String currency;

    public WalletCreatedEvent(WalletId walletId, String currency) {
        super();
        this.walletId = walletId;
        this.currency = currency;
    }

    @Override
    public String getEventType() {
        return "WALLET_CREATED";
    }

    public WalletId getWalletId() {
        return walletId;
    }

    public String getCurrency() {
        return currency;
    }
}
