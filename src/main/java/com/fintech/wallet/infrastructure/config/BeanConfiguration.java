package com.fintech.wallet.infrastructure.config;

import com.fintech.wallet.application.port.in.CreateWalletUseCase;
import com.fintech.wallet.application.port.in.DepositMoneyUseCase;
import com.fintech.wallet.application.port.in.TransferMoneyUseCase;
import com.fintech.wallet.application.port.in.WithdrawMoneyUseCase;
import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.application.port.out.SaveTransactionPort;
import com.fintech.wallet.application.port.out.SaveWalletPort;
import com.fintech.wallet.application.usecase.CreateWalletUseCaseHandler;
import com.fintech.wallet.application.usecase.DepositMoneyUseCaseHandler;
import com.fintech.wallet.application.usecase.TransferMoneyUseCaseHandler;
import com.fintech.wallet.application.usecase.WithdrawMoneyUseCaseHandler;
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
}
