package com.fintech.wallet.infrastructure.config;

import com.fintech.wallet.application.port.in.CreateWalletUseCase;
import com.fintech.wallet.application.port.in.CrossCurrencyTransferUseCase;
import com.fintech.wallet.application.port.in.DepositMoneyUseCase;
import com.fintech.wallet.application.port.in.ReportingUseCase;
import com.fintech.wallet.application.port.in.ScheduledPaymentUseCase;
import com.fintech.wallet.application.port.in.TransferMoneyUseCase;
import com.fintech.wallet.application.port.in.WithdrawMoneyUseCase;
import com.fintech.wallet.application.port.out.DomainEventPublisher;
import com.fintech.wallet.application.port.out.ExchangeRatePort;
import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.application.port.out.ReportExportPort;
import com.fintech.wallet.application.port.out.SaveTransactionPort;
import com.fintech.wallet.application.port.out.SaveWalletPort;
import com.fintech.wallet.application.port.out.ScheduledPaymentPort;
import com.fintech.wallet.application.usecase.CreateWalletUseCaseHandler;
import com.fintech.wallet.application.usecase.CrossCurrencyTransferUseCaseHandler;
import com.fintech.wallet.application.usecase.DepositMoneyUseCaseHandler;
import com.fintech.wallet.application.usecase.ReportingUseCaseHandler;
import com.fintech.wallet.application.usecase.ScheduledPaymentUseCaseHandler;
import com.fintech.wallet.application.usecase.TransferMoneyUseCaseHandler;
import com.fintech.wallet.application.usecase.WithdrawMoneyUseCaseHandler;
import com.fintech.wallet.domain.service.CurrencyExchangeDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for wiring use cases with their dependencies.
 * Explicit bean definitions for clarity and testability.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public CreateWalletUseCase createWalletUseCase(SaveWalletPort saveWalletPort) {
        return new CreateWalletUseCaseHandler(saveWalletPort);
    }

    @Bean
    public DepositMoneyUseCase depositMoneyUseCase(LoadWalletPort loadWalletPort, SaveWalletPort saveWalletPort) {
        return new DepositMoneyUseCaseHandler(loadWalletPort, saveWalletPort);
    }

    @Bean
    public WithdrawMoneyUseCase withdrawMoneyUseCase(LoadWalletPort loadWalletPort, SaveWalletPort saveWalletPort) {
        return new WithdrawMoneyUseCaseHandler(loadWalletPort, saveWalletPort);
    }

    @Bean
    public TransferMoneyUseCase transferMoneyUseCase(LoadWalletPort loadWalletPort,
            SaveWalletPort saveWalletPort,
            SaveTransactionPort saveTransactionPort) {
        return new TransferMoneyUseCaseHandler(loadWalletPort, saveWalletPort, saveTransactionPort);
    }

    @Bean
    public CurrencyExchangeDomainService currencyExchangeDomainService() {
        return new CurrencyExchangeDomainService();
    }

    @Bean
    public CrossCurrencyTransferUseCase crossCurrencyTransferUseCase(
            LoadWalletPort loadWalletPort,
            SaveWalletPort saveWalletPort,
            SaveTransactionPort saveTransactionPort,
            ExchangeRatePort exchangeRatePort,
            CurrencyExchangeDomainService currencyExchangeDomainService) {
        return new CrossCurrencyTransferUseCaseHandler(
                loadWalletPort, saveWalletPort, saveTransactionPort,
                exchangeRatePort, currencyExchangeDomainService);
    }

    @Bean
    public ReportingUseCase reportingUseCase(LoadWalletPort loadWalletPort, ReportExportPort reportExportPort) {
        return new ReportingUseCaseHandler(loadWalletPort, reportExportPort);
    }

    @Bean
    public ScheduledPaymentUseCase scheduledPaymentUseCase(
            ScheduledPaymentPort scheduledPaymentPort,
            LoadWalletPort loadWalletPort,
            TransferMoneyUseCase transferMoneyUseCase,
            DomainEventPublisher eventPublisher) {
        return new ScheduledPaymentUseCaseHandler(
                scheduledPaymentPort, loadWalletPort, transferMoneyUseCase, eventPublisher);
    }
}
