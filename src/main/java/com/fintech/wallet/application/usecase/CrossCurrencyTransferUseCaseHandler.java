package com.fintech.wallet.application.usecase;

import com.fintech.wallet.application.command.CrossCurrencyTransferCommand;
import com.fintech.wallet.application.port.in.CrossCurrencyTransferUseCase;
import com.fintech.wallet.application.port.out.ExchangeRatePort;
import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.application.port.out.SaveTransactionPort;
import com.fintech.wallet.application.port.out.SaveWalletPort;
import com.fintech.wallet.domain.exception.WalletNotFoundException;
import com.fintech.wallet.domain.model.Transaction;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.service.CurrencyExchangeDomainService;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.ExchangeRate;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.time.Instant;
import java.util.Objects;

/**
 * Use case handler for cross-currency transfers.
 * Handles currency conversion with real exchange rates and fees.
 */
public class CrossCurrencyTransferUseCaseHandler implements CrossCurrencyTransferUseCase {

    private final LoadWalletPort loadWalletPort;
    private final SaveWalletPort saveWalletPort;
    private final SaveTransactionPort saveTransactionPort;
    private final ExchangeRatePort exchangeRatePort;
    private final CurrencyExchangeDomainService currencyExchangeService;

    public CrossCurrencyTransferUseCaseHandler(
            LoadWalletPort loadWalletPort,
            SaveWalletPort saveWalletPort,
            SaveTransactionPort saveTransactionPort,
            ExchangeRatePort exchangeRatePort,
            CurrencyExchangeDomainService currencyExchangeService) {
        this.loadWalletPort = Objects.requireNonNull(loadWalletPort);
        this.saveWalletPort = Objects.requireNonNull(saveWalletPort);
        this.saveTransactionPort = Objects.requireNonNull(saveTransactionPort);
        this.exchangeRatePort = Objects.requireNonNull(exchangeRatePort);
        this.currencyExchangeService = Objects.requireNonNull(currencyExchangeService);
    }

    @Override
    public CrossCurrencyTransferResult execute(CrossCurrencyTransferCommand command) {
        Objects.requireNonNull(command, "Command is required");

        WalletId sourceWalletId = WalletId.of(command.getSourceWalletId());
        WalletId destinationWalletId = WalletId.of(command.getDestinationWalletId());

        Wallet sourceWallet = loadWalletPort.loadById(sourceWalletId)
                .orElseThrow(() -> new WalletNotFoundException(sourceWalletId.toString()));

        Wallet destinationWallet = loadWalletPort.loadById(destinationWalletId)
                .orElseThrow(() -> new WalletNotFoundException(destinationWalletId.toString()));

        Currency sourceCurrency = Currency.of(command.getSourceCurrency());
        Currency targetCurrency = Currency.of(command.getTargetCurrency());

        // Validate wallet currencies match command currencies
        validateWalletCurrency(sourceWallet, sourceCurrency);
        validateWalletCurrency(destinationWallet, targetCurrency);

        Money sourceAmount = Money.of(command.getSourceAmount(), sourceCurrency);
        
        // Calculate fee first
        Money fee = currencyExchangeService.calculateExchangeFee(sourceAmount);
        Money amountAfterFee = Money.of(
            sourceAmount.getAmount().subtract(fee.getAmount()),
            sourceCurrency
        );

        // Get exchange rate and convert
        ExchangeRate exchangeRate = exchangeRatePort.getExchangeRate(sourceCurrency, targetCurrency)
                .orElseThrow(() -> new IllegalStateException(
                    String.format("Exchange rate not available for %s to %s", sourceCurrency, targetCurrency)));

        Money targetAmount = currencyExchangeService.convert(amountAfterFee, exchangeRate);

        // Create transaction
        Transaction transaction = Transaction.createTransfer(
                sourceWalletId,
                destinationWalletId,
                sourceAmount,
                command.getDescription() + String.format(" (Rate: %s)", exchangeRate.getRate()));

        // Execute the transfer
        sourceWallet.debit(sourceAmount, transaction.getId(), 
            "Cross-currency transfer to " + destinationWalletId);
        destinationWallet.credit(targetAmount, transaction.getId(),
            "Cross-currency transfer from " + sourceWalletId);

        // Persist changes
        saveWalletPort.save(sourceWallet);
        saveWalletPort.save(destinationWallet);
        saveTransactionPort.save(transaction);

        return new CrossCurrencyTransferResult(
            transaction.getId().toString(),
            sourceAmount.getAmount(),
            sourceCurrency.getCode(),
            targetAmount.getAmount(),
            targetCurrency.getCode(),
            exchangeRate.getRate(),
            fee.getAmount(),
            Instant.now()
        );
    }

    private void validateWalletCurrency(Wallet wallet, Currency expectedCurrency) {
        if (!wallet.getCurrency().isSameAs(expectedCurrency)) {
            throw new IllegalArgumentException(
                String.format("Wallet %s currency %s does not match expected currency %s",
                    wallet.getId(), wallet.getCurrency(), expectedCurrency));
        }
    }
}
