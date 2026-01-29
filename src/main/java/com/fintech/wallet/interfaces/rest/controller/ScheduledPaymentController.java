package com.fintech.wallet.interfaces.rest.controller;

import com.fintech.wallet.application.command.CreateScheduledPaymentCommand;
import com.fintech.wallet.application.port.in.ScheduledPaymentUseCase;
import com.fintech.wallet.domain.model.ScheduledPayment;
import com.fintech.wallet.domain.valueobject.ScheduledPaymentId;
import com.fintech.wallet.interfaces.rest.dto.CreateScheduledPaymentRequest;
import com.fintech.wallet.interfaces.rest.dto.ScheduledPaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for scheduled payments.
 */
@RestController
@RequestMapping("/api/scheduled-payments")
public class ScheduledPaymentController {

    private final ScheduledPaymentUseCase scheduledPaymentUseCase;

    public ScheduledPaymentController(ScheduledPaymentUseCase scheduledPaymentUseCase) {
        this.scheduledPaymentUseCase = scheduledPaymentUseCase;
    }

    /**
     * Create a new scheduled payment.
     */
    @PostMapping
    public ResponseEntity<ScheduledPaymentResponse> createScheduledPayment(
            @Valid @RequestBody CreateScheduledPaymentRequest request) {

        CreateScheduledPaymentCommand command = new CreateScheduledPaymentCommand(
                request.getSourceWalletId(),
                request.getDestinationWalletId(),
                request.getAmount(),
                request.getCurrency(),
                request.getDescription(),
                request.getRecurrencePattern(),
                request.getStartDate(),
                request.getEndDate(),
                request.getMaxExecutions()
        );

        ScheduledPaymentId paymentId = scheduledPaymentUseCase.createScheduledPayment(command);
        ScheduledPayment payment = scheduledPaymentUseCase.getScheduledPayment(paymentId.toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(payment));
    }

    /**
     * Get a scheduled payment by ID.
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ScheduledPaymentResponse> getScheduledPayment(@PathVariable String paymentId) {
        ScheduledPayment payment = scheduledPaymentUseCase.getScheduledPayment(paymentId);
        return ResponseEntity.ok(toResponse(payment));
    }

    /**
     * Get all scheduled payments for a wallet.
     */
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<ScheduledPaymentResponse>> getScheduledPaymentsForWallet(
            @PathVariable String walletId) {
        List<ScheduledPayment> payments = scheduledPaymentUseCase.getScheduledPaymentsForWallet(walletId);
        List<ScheduledPaymentResponse> responses = payments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Pause a scheduled payment.
     */
    @PostMapping("/{paymentId}/pause")
    public ResponseEntity<Void> pauseScheduledPayment(@PathVariable String paymentId) {
        scheduledPaymentUseCase.pauseScheduledPayment(paymentId);
        return ResponseEntity.ok().build();
    }

    /**
     * Resume a paused scheduled payment.
     */
    @PostMapping("/{paymentId}/resume")
    public ResponseEntity<Void> resumeScheduledPayment(@PathVariable String paymentId) {
        scheduledPaymentUseCase.resumeScheduledPayment(paymentId);
        return ResponseEntity.ok().build();
    }

    /**
     * Cancel a scheduled payment.
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<Void> cancelScheduledPayment(@PathVariable String paymentId) {
        scheduledPaymentUseCase.cancelScheduledPayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    private ScheduledPaymentResponse toResponse(ScheduledPayment payment) {
        return ScheduledPaymentResponse.builder()
                .id(payment.getId().toString())
                .sourceWalletId(payment.getSourceWalletId().toString())
                .destinationWalletId(payment.getDestinationWalletId().toString())
                .amount(payment.getAmount().getAmount())
                .currency(payment.getAmount().getCurrency().getCode())
                .description(payment.getDescription())
                .recurrencePattern(payment.getRecurrencePattern().name())
                .startDate(payment.getStartDate())
                .endDate(payment.getEndDate())
                .nextExecutionDate(payment.getNextExecutionDate())
                .executionCount(payment.getExecutionCount())
                .maxExecutions(payment.getMaxExecutions())
                .status(payment.getStatus().name())
                .createdAt(payment.getCreatedAt())
                .lastModifiedAt(payment.getLastModifiedAt())
                .build();
    }
}
