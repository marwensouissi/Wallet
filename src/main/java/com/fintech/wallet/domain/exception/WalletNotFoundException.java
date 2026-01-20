package com.fintech.wallet.domain.exception;

/**
 * Thrown when a wallet cannot be found by its identifier.
 */
public class WalletNotFoundException extends RuntimeException {

    private final String walletId;

    public WalletNotFoundException(String walletId) {
        super(String.format("Wallet not found with ID: %s", walletId));
        this.walletId = walletId;
    }

    public String getWalletId() {
        return walletId;
    }
}
