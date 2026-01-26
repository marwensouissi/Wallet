package com.fintech.wallet.application.port.in;

import com.fintech.wallet.application.command.DepositMoneyCommand;
import com.fintech.wallet.domain.valueobject.LedgerEntryId;

/**
 * Input port for depositing money into a wallet.
 * Defines the use case interface that the application exposes.
 */
public interface DepositMoneyUseCase {

    /**
     * Deposits money into a wallet.
     *
     * @param command the deposit money command
     * @return the ID of the created ledger entry
     */
    LedgerEntryId execute(DepositMoneyCommand command);
}
