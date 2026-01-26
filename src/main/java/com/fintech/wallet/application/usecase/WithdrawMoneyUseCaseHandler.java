package com.fintech.wallet.application.usecase;

import com.fintech.wallet.application.command.WithdrawMoneyCommand;
import com.fintech.wallet.application.port.in.WithdrawMoneyUseCase;
import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.application.port.out.SaveWalletPort;
import com.fintech.wallet.domain.exception.WalletNotFoundException;
import com.fintech.wallet.domain.model.LedgerEntry;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.LedgerEntryId;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.util.Objects;

/**
 * Use case handler for withdrawing money from a wallet.
 * Orchestrates the withdrawal process.
 */
public class WithdrawMoneyUseCaseHandler implements WithdrawMoneyUseCase {

    private final LoadWalletPort loadWalletPort;
    private final SaveWalletPort saveWalletPort;

    public WithdrawMoneyUseCaseHandler(LoadWalletPort loadWalletPort, SaveWalletPort saveWalletPort) {
        this.loadWalletPort = Objects.requireNonNull(loadWalletPort, "LoadWalletPort is required");
        this.saveWalletPort = Objects.requireNonNull(saveWalletPort, "SaveWalletPort is required");
    }

    @Override
    public LedgerEntryId execute(WithdrawMoneyCommand command) {
        Objects.requireNonNull(command, "WithdrawMoneyCommand is required");

        WalletId walletId = WalletId.of(command.getWalletId());

        // Load the wallet
        Wallet wallet = loadWalletPort.loadById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(command.getWalletId()));

        // Create money value object
        Money amount = Money.of(command.getAmount().toString(), command.getCurrency());

        // Generate a transaction ID for this withdrawal
        TransactionId transactionId = TransactionId.generate();

        // Debit the wallet (will throw InsufficientBalanceException if not enough funds)
        LedgerEntry ledgerEntry = wallet.debit(amount, transactionId, command.getDescription());

        // Persist the updated wallet
        saveWalletPort.save(wallet);

        return ledgerEntry.getId();
    }
}
