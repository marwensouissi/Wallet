package com.fintech.wallet.application.port.out;

import com.fintech.wallet.domain.model.Wallet;

/**
 * Output port for saving wallets to persistence.
 * Implemented by infrastructure adapters.
 */
public interface SaveWalletPort {

    /**
     * Saves a wallet and its ledger entries.
     *
     * @param wallet the wallet to save
     */
    void save(Wallet wallet);
}
