package com.fintech.wallet.interfaces.rest.controller;

import com.fintech.wallet.application.command.CrossCurrencyTransferCommand;
import com.fintech.wallet.application.port.in.CrossCurrencyTransferUseCase;
import com.fintech.wallet.application.port.in.CrossCurrencyTransferUseCase.CrossCurrencyTransferResult;
import com.fintech.wallet.application.port.out.ExchangeRatePort;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.interfaces.rest.dto.CrossCurrencyTransferRequest;
import com.fintech.wallet.interfaces.rest.dto.CrossCurrencyTransferResponse;
import com.fintech.wallet.interfaces.rest.dto.ExchangeRatesResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * REST controller for currency exchange operations.
 */
@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {

    private final CrossCurrencyTransferUseCase crossCurrencyTransferUseCase;
    private final ExchangeRatePort exchangeRatePort;

    public ExchangeController(
            CrossCurrencyTransferUseCase crossCurrencyTransferUseCase,
            ExchangeRatePort exchangeRatePort) {
        this.crossCurrencyTransferUseCase = crossCurrencyTransferUseCase;
        this.exchangeRatePort = exchangeRatePort;
    }

    /**
     * Get current exchange rates for a base currency.
     */
    @GetMapping("/rates/{baseCurrency}")
    public ResponseEntity<ExchangeRatesResponse> getExchangeRates(@PathVariable String baseCurrency) {
        Currency currency = Currency.of(baseCurrency);
        Map<String, BigDecimal> rates = exchangeRatePort.getAllRates(currency);

        ExchangeRatesResponse response = ExchangeRatesResponse.builder()
                .baseCurrency(currency.getCode())
                .rates(rates)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Execute a cross-currency transfer between wallets.
     */
    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<CrossCurrencyTransferResponse> crossCurrencyTransfer(
            @Valid @RequestBody CrossCurrencyTransferRequest request) {

        CrossCurrencyTransferCommand command = new CrossCurrencyTransferCommand(
                request.getSourceWalletId(),
                request.getDestinationWalletId(),
                request.getAmount(),
                request.getSourceCurrency(),
                request.getTargetCurrency(),
                request.getDescription()
        );

        CrossCurrencyTransferResult result = crossCurrencyTransferUseCase.execute(command);

        CrossCurrencyTransferResponse response = CrossCurrencyTransferResponse.builder()
                .transactionId(result.transactionId())
                .sourceAmount(result.sourceAmount())
                .sourceCurrency(result.sourceCurrency())
                .targetAmount(result.targetAmount())
                .targetCurrency(result.targetCurrency())
                .exchangeRate(result.exchangeRate())
                .feeAmount(result.feeAmount())
                .timestamp(result.timestamp())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
