package com.fintech.wallet.interfaces.rest.controller;

import com.fintech.wallet.application.command.CreateWalletCommand;
import com.fintech.wallet.application.command.DepositMoneyCommand;
import com.fintech.wallet.application.command.WithdrawMoneyCommand;
import com.fintech.wallet.application.port.in.CreateWalletUseCase;
import com.fintech.wallet.application.port.in.DepositMoneyUseCase;
import com.fintech.wallet.application.port.in.WithdrawMoneyUseCase;
import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.domain.exception.WalletNotFoundException;
import com.fintech.wallet.domain.model.LedgerEntry;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.LedgerEntryId;
import com.fintech.wallet.domain.valueobject.WalletId;
import com.fintech.wallet.interfaces.rest.dto.CreateWalletRequest;
import com.fintech.wallet.interfaces.rest.dto.DepositRequest;
import com.fintech.wallet.interfaces.rest.dto.DepositResponse;
import com.fintech.wallet.interfaces.rest.dto.LedgerEntryResponse;
import com.fintech.wallet.interfaces.rest.dto.TransactionHistoryResponse;
import com.fintech.wallet.interfaces.rest.dto.WalletResponse;
import com.fintech.wallet.interfaces.rest.dto.WithdrawRequest;
import com.fintech.wallet.interfaces.rest.dto.WithdrawResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * REST controller for wallet operations.
 * Thin controller delegating to use cases.
 */
@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final DepositMoneyUseCase depositMoneyUseCase;
    private final WithdrawMoneyUseCase withdrawMoneyUseCase;
    private final LoadWalletPort loadWalletPort;

    public WalletController(
            CreateWalletUseCase createWalletUseCase,
            DepositMoneyUseCase depositMoneyUseCase,
            WithdrawMoneyUseCase withdrawMoneyUseCase,
            LoadWalletPort loadWalletPort) {
        this.createWalletUseCase = createWalletUseCase;
        this.depositMoneyUseCase = depositMoneyUseCase;
        this.withdrawMoneyUseCase = withdrawMoneyUseCase;
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

    @PostMapping("/{walletId}/deposit")
    @Transactional
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable String walletId,
            @Valid @RequestBody DepositRequest request) {

        DepositMoneyCommand command = new DepositMoneyCommand(
                walletId,
                request.getAmount(),
                request.getCurrency(),
                request.getDescription());

        LedgerEntryId ledgerEntryId = depositMoneyUseCase.execute(command);

        // Load updated wallet for response
        Wallet wallet = loadWalletPort.loadById(WalletId.of(walletId))
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        DepositResponse response = DepositResponse.builder()
                .ledgerEntryId(ledgerEntryId.toString())
                .walletId(walletId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .newBalance(wallet.calculateBalance().getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Deposit")
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{walletId}/withdraw")
    @Transactional
    public ResponseEntity<WithdrawResponse> withdraw(
            @PathVariable String walletId,
            @Valid @RequestBody WithdrawRequest request) {

        WithdrawMoneyCommand command = new WithdrawMoneyCommand(
                walletId,
                request.getAmount(),
                request.getCurrency(),
                request.getDescription());

        LedgerEntryId ledgerEntryId = withdrawMoneyUseCase.execute(command);

        // Load updated wallet for response
        Wallet wallet = loadWalletPort.loadById(WalletId.of(walletId))
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        WithdrawResponse response = WithdrawResponse.builder()
                .ledgerEntryId(ledgerEntryId.toString())
                .walletId(walletId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .newBalance(wallet.calculateBalance().getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Withdrawal")
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get transaction history (ledger entries) for a wallet.
     * Entries are sorted by creation time in descending order (newest first).
     */
    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(@PathVariable String walletId) {
        WalletId id = WalletId.of(walletId);

        Wallet wallet = loadWalletPort.loadById(id)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        List<LedgerEntryResponse> entries = wallet.getLedgerEntries().stream()
                .sorted(Comparator.comparing(LedgerEntry::getCreatedAt).reversed())
                .map(this::toLedgerEntryResponse)
                .toList();

        TransactionHistoryResponse response = TransactionHistoryResponse.builder()
                .walletId(walletId)
                .currency(wallet.getCurrency().getCode())
                .balance(wallet.calculateBalance().getAmount().toPlainString())
                .totalEntries(entries.size())
                .entries(entries)
                .build();

        return ResponseEntity.ok(response);
    }

    private LedgerEntryResponse toLedgerEntryResponse(LedgerEntry entry) {
        return LedgerEntryResponse.builder()
                .id(entry.getId().toString())
                .walletId(entry.getWalletId().toString())
                .transactionId(entry.getTransactionId().toString())
                .type(entry.getType().name())
                .amount(entry.getAmount().getAmount().toPlainString())
                .currency(entry.getAmount().getCurrency().getCode())
                .description(entry.getDescription())
                .createdAt(entry.getCreatedAt())
                .build();
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
