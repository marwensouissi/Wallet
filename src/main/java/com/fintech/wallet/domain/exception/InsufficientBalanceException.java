package com.fintech.wallet.domain.exception;

/**
 * Thrown when an operation would result in insufficient wallet balance.
 */
public class InsufficientBalanceException extends RuntimeException {

    private final String walletId;
    private final String requestedAmount;
    private final String availableBalance;

    public InsufficientBalanceException(String walletId, String requestedAmount, String availableBalance) {
        super(String.format(
                "Insufficient balance in wallet %s. Requested: %s, Available: %s",
                walletId, requestedAmount, availableBalance));
        this.walletId = walletId;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public String getWalletId() {
        return walletId;
    }

    public String getRequestedAmount() {
        return requestedAmount;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }
}
