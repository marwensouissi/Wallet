package com.fintech.wallet.application.usecase;

import com.fintech.wallet.application.command.DepositMoneyCommand;
import com.fintech.wallet.application.port.in.DepositMoneyUseCase;
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
 * Use case handler for depositing money into a wallet.
 * Orchestrates the deposit process.
 */
public class DepositMoneyUseCaseHandler implements DepositMoneyUseCase {

    private final LoadWalletPort loadWalletPort;
    private final SaveWalletPort saveWalletPort;

    public DepositMoneyUseCaseHandler(LoadWalletPort loadWalletPort, SaveWalletPort saveWalletPort) {
        this.loadWalletPort = Objects.requireNonNull(loadWalletPort, "LoadWalletPort is required");
        this.saveWalletPort = Objects.requireNonNull(saveWalletPort, "SaveWalletPort is required");
    }

    @Override
    public LedgerEntryId execute(DepositMoneyCommand command) {
        Objects.requireNonNull(command, "DepositMoneyCommand is required");

        WalletId walletId = WalletId.of(command.getWalletId());

        // Load the wallet
        Wallet wallet = loadWalletPort.loadById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(command.getWalletId()));

        // Create money value object
        Money amount = Money.of(command.getAmount().toString(), command.getCurrency());

        // Generate a transaction ID for this deposit
        TransactionId transactionId = TransactionId.generate();

        // Credit the wallet
        LedgerEntry ledgerEntry = wallet.credit(amount, transactionId, command.getDescription());

        // Persist the updated wallet
        saveWalletPort.save(wallet);

        return ledgerEntry.getId();
    }
}
