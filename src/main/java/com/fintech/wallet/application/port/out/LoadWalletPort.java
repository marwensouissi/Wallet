package com.fintech.wallet.application.port.out;

import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.util.Optional;

/**
 * Output port for loading wallets from persistence.
 * Implemented by infrastructure adapters.
 */
public interface LoadWalletPort {

    /**
     * Loads a wallet by its ID.
     *
     * @param walletId the wallet identifier
     * @return an Optional containing the wallet if found, empty otherwise
     */
    Optional<Wallet> loadById(WalletId walletId);
}
