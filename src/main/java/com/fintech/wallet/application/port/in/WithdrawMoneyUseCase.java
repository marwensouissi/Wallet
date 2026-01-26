package com.fintech.wallet.application.port.in;

import com.fintech.wallet.application.command.WithdrawMoneyCommand;
import com.fintech.wallet.domain.valueobject.LedgerEntryId;

/**
 * Input port for withdrawing money from a wallet.
 * Defines the use case interface that the application exposes.
 */
public interface WithdrawMoneyUseCase {

    /**
     * Withdraws money from a wallet.
     *
     * @param command the withdraw money command
     * @return the ID of the created ledger entry
     * @throws com.fintech.wallet.domain.exception.InsufficientBalanceException if balance is insufficient
     */
    LedgerEntryId execute(WithdrawMoneyCommand command);
}
