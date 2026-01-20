package com.fintech.wallet.interfaces.rest.controller;

import com.fintech.wallet.application.command.CreateWalletCommand;
import com.fintech.wallet.application.port.in.CreateWalletUseCase;
import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.domain.exception.WalletNotFoundException;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.WalletId;
import com.fintech.wallet.interfaces.rest.dto.CreateWalletRequest;
import com.fintech.wallet.interfaces.rest.dto.WalletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for wallet operations.
 * Thin controller delegating to use cases.
 */
@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final LoadWalletPort loadWalletPort;

    public WalletController(CreateWalletUseCase createWalletUseCase, LoadWalletPort loadWalletPort) {
        this.createWalletUseCase = createWalletUseCase;
        this.loadWalletPort = loadWalletPort;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        CreateWalletCommand command = new CreateWalletCommand(request.getCurrency());
        WalletId walletId = createWalletUseCase.execute(command);

        Wallet wallet = loadWalletPort.loadById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId.toString()));

        WalletResponse response = toResponse(wallet);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable String walletId) {
        WalletId id = WalletId.of(walletId);

        Wallet wallet = loadWalletPort.loadById(id)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        WalletResponse response = toResponse(wallet);
        return ResponseEntity.ok(response);
    }

    private WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId().toString())
                .currency(wallet.getCurrency().getCode())
                .balance(wallet.calculateBalance().getAmount())
                .createdAt(wallet.getCreatedAt())
                .build();
    }
}
