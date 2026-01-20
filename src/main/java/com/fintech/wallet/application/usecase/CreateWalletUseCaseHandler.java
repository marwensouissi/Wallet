package com.fintech.wallet.application.usecase;

import com.fintech.wallet.application.command.CreateWalletCommand;
import com.fintech.wallet.application.port.in.CreateWalletUseCase;
import com.fintech.wallet.application.port.out.SaveWalletPort;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.util.Objects;

/**
 * Use case handler for creating wallets.
 * Orchestrates the wallet creation process.
 */
public class CreateWalletUseCaseHandler implements CreateWalletUseCase {

    private final SaveWalletPort saveWalletPort;

    public CreateWalletUseCaseHandler(SaveWalletPort saveWalletPort) {
        this.saveWalletPort = Objects.requireNonNull(saveWalletPort, "SaveWalletPort is required");
    }

    @Override
    public WalletId execute(CreateWalletCommand command) {
        Objects.requireNonNull(command, "CreateWalletCommand is required");

        Currency currency = Currency.of(command.getCurrency());
        Wallet wallet = Wallet.create(currency);

        saveWalletPort.save(wallet);

        return wallet.getId();
    }
}
