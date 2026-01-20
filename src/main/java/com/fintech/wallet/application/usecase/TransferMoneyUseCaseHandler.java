package com.fintech.wallet.application.usecase;

import com.fintech.wallet.application.command.TransferMoneyCommand;
import com.fintech.wallet.application.port.in.TransferMoneyUseCase;
import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.application.port.out.SaveTransactionPort;
import com.fintech.wallet.application.port.out.SaveWalletPort;
import com.fintech.wallet.domain.exception.InvalidCurrencyException;
import com.fintech.wallet.domain.exception.WalletNotFoundException;
import com.fintech.wallet.domain.model.Transaction;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.util.Objects;

/**
 * Use case handler for money transfers between wallets.
 * Orchestrates the complete transfer process including validation,
 * ledger entry creation, and transaction recording.
 *
 * Transaction boundary is at this level (not at repository level).
 */
public class TransferMoneyUseCaseHandler implements TransferMoneyUseCase {

    private final LoadWalletPort loadWalletPort;
    private final SaveWalletPort saveWalletPort;
    private final SaveTransactionPort saveTransactionPort;

    public TransferMoneyUseCaseHandler(LoadWalletPort loadWalletPort,
            SaveWalletPort saveWalletPort,
            SaveTransactionPort saveTransactionPort) {
        this.loadWalletPort = Objects.requireNonNull(loadWalletPort, "LoadWalletPort is required");
        this.saveWalletPort = Objects.requireNonNull(saveWalletPort, "SaveWalletPort is required");
        this.saveTransactionPort = Objects.requireNonNull(saveTransactionPort, "SaveTransactionPort is required");
    }

    @Override
    public TransactionId execute(TransferMoneyCommand command) {
        Objects.requireNonNull(command, "TransferMoneyCommand is required");

        WalletId sourceWalletId = WalletId.of(command.getSourceWalletId());
        WalletId destinationWalletId = WalletId.of(command.getDestinationWalletId());

        Wallet sourceWallet = loadWalletPort.loadById(sourceWalletId)
                .orElseThrow(() -> new WalletNotFoundException(sourceWalletId.toString()));

        Wallet destinationWallet = loadWalletPort.loadById(destinationWalletId)
                .orElseThrow(() -> new WalletNotFoundException(destinationWalletId.toString()));

        validateSameCurrency(sourceWallet, destinationWallet);

        Currency currency = Currency.of(command.getCurrency());
        Money transferAmount = Money.of(command.getAmount(), currency);

        validateWalletCurrency(sourceWallet, currency);

        Transaction transaction = Transaction.createTransfer(
                sourceWalletId,
                destinationWalletId,
                transferAmount,
                command.getDescription());

        sourceWallet.debit(transferAmount, transaction.getId(), "Transfer to " + destinationWalletId);
        destinationWallet.credit(transferAmount, transaction.getId(), "Transfer from " + sourceWalletId);

        saveWalletPort.save(sourceWallet);
        saveWalletPort.save(destinationWallet);
        saveTransactionPort.save(transaction);

        return transaction.getId();
    }

    private void validateSameCurrency(Wallet source, Wallet destination) {
        if (!source.getCurrency().isSameAs(destination.getCurrency())) {
            throw new InvalidCurrencyException(
                    source.getCurrency().getCode(),
                    destination.getCurrency().getCode());
        }
    }

    private void validateWalletCurrency(Wallet wallet, Currency transferCurrency) {
        if (!wallet.getCurrency().isSameAs(transferCurrency)) {
            throw new InvalidCurrencyException(
                    String.format("Transfer currency %s does not match wallet currency %s",
                            transferCurrency, wallet.getCurrency()));
        }
    }
}
