package com.fintech.wallet.application.port.in;

import com.fintech.wallet.application.command.CreateWalletCommand;
import com.fintech.wallet.domain.valueobject.WalletId;

/**
 * Input port for creating a wallet.
 * Defines the use case interface that the application exposes.
 */
public interface CreateWalletUseCase {

    /**
     * Creates a new wallet with the specified currency.
     *
     * @param command the create wallet command
     * @return the ID of the created wallet
     */
    WalletId execute(CreateWalletCommand command);
}
