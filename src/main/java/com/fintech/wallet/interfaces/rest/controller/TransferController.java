package com.fintech.wallet.interfaces.rest.controller;

import com.fintech.wallet.application.command.TransferMoneyCommand;
import com.fintech.wallet.application.port.in.TransferMoneyUseCase;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.interfaces.rest.dto.TransferMoneyRequest;
import com.fintech.wallet.interfaces.rest.dto.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * REST controller for money transfer operations.
 * Thin controller delegating to use cases.
 */
@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferMoneyUseCase transferMoneyUseCase;

    public TransferController(TransferMoneyUseCase transferMoneyUseCase) {
        this.transferMoneyUseCase = transferMoneyUseCase;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferMoneyRequest request) {
        TransferMoneyCommand command = new TransferMoneyCommand(
                request.getSourceWalletId(),
                request.getDestinationWalletId(),
                request.getAmount(),
                request.getCurrency(),
                request.getDescription());

        TransactionId transactionId = transferMoneyUseCase.execute(command);

        TransferResponse response = TransferResponse.builder()
                .transactionId(transactionId.toString())
                .sourceWalletId(request.getSourceWalletId())
                .destinationWalletId(request.getDestinationWalletId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status("COMPLETED")
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
