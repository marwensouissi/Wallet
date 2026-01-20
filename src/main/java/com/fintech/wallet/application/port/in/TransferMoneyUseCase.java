package com.fintech.wallet.application.port.in;

import com.fintech.wallet.application.command.TransferMoneyCommand;
import com.fintech.wallet.domain.valueobject.TransactionId;

/**
 * Input port for transferring money between wallets.
 * Defines the use case interface that the application exposes.
 */
public interface TransferMoneyUseCase {

    /**
     * Transfers money from source wallet to destination wallet.
     *
     * @param command the transfer money command
     * @return the ID of the created transaction
     */
    TransactionId execute(TransferMoneyCommand command);
}
