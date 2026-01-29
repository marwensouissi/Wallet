package com.fintech.wallet.interfaces.rest.controller;

import com.fintech.wallet.application.port.in.ReportingUseCase;
import com.fintech.wallet.domain.valueobject.AccountStatement;
import com.fintech.wallet.domain.valueobject.MonthlySummary;
import com.fintech.wallet.interfaces.rest.dto.AccountStatementResponse;
import com.fintech.wallet.interfaces.rest.dto.MonthlySummaryResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.Collectors;

/**
 * REST controller for analytics and reporting.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportingUseCase reportingUseCase;

    public ReportController(ReportingUseCase reportingUseCase) {
        this.reportingUseCase = reportingUseCase;
    }

    /**
     * Get account statement for a wallet within a date range.
     */
    @GetMapping("/wallets/{walletId}/statement")
    public ResponseEntity<AccountStatementResponse> getAccountStatement(
            @PathVariable String walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AccountStatement statement = reportingUseCase.generateAccountStatement(walletId, startDate, endDate);
        return ResponseEntity.ok(toResponse(statement));
    }

    /**
     * Get monthly summary for a wallet.
     */
    @GetMapping("/wallets/{walletId}/monthly-summary")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @PathVariable String walletId,
            @RequestParam int year,
            @RequestParam int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        MonthlySummary summary = reportingUseCase.generateMonthlySummary(walletId, yearMonth);
        return ResponseEntity.ok(toResponse(summary));
    }

    /**
     * Export account statement as PDF.
     */
    @GetMapping("/wallets/{walletId}/statement/pdf")
    public ResponseEntity<byte[]> exportStatementPdf(
            @PathVariable String walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AccountStatement statement = reportingUseCase.generateAccountStatement(walletId, startDate, endDate);
        byte[] pdf = reportingUseCase.exportStatementToPdf(statement);

        String filename = String.format("statement_%s_%s_to_%s.pdf", walletId, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /**
     * Export account statement as CSV.
     */
    @GetMapping("/wallets/{walletId}/statement/csv")
    public ResponseEntity<byte[]> exportStatementCsv(
            @PathVariable String walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AccountStatement statement = reportingUseCase.generateAccountStatement(walletId, startDate, endDate);
        byte[] csv = reportingUseCase.exportStatementToCsv(statement);

        String filename = String.format("statement_%s_%s_to_%s.csv", walletId, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    private AccountStatementResponse toResponse(AccountStatement statement) {
        return AccountStatementResponse.builder()
                .walletId(statement.walletId().toString())
                .currency(statement.currency().getCode())
                .startDate(statement.startDate())
                .endDate(statement.endDate())
                .openingBalance(statement.openingBalance().getAmount())
                .closingBalance(statement.closingBalance().getAmount())
                .totalTransactions(statement.totalTransactions())
                .entries(statement.entries().stream()
                        .map(e -> new AccountStatementResponse.StatementEntryDto(
                                e.date(),
                                e.type(),
                                e.description(),
                                e.amount().getAmount(),
                                e.amount().getCurrency().getCode(),
                                e.runningBalance().getAmount(),
                                e.transactionId()
                        ))
                        .collect(Collectors.toList()))
                .build();
    }

    private MonthlySummaryResponse toResponse(MonthlySummary summary) {
        return MonthlySummaryResponse.builder()
                .walletId(summary.walletId().toString())
                .month(summary.month().toString())
                .currency(summary.currency().getCode())
                .totalDeposits(summary.totalDeposits())
                .totalWithdrawals(summary.totalWithdrawals())
                .totalTransfersIn(summary.totalTransfersIn())
                .totalTransfersOut(summary.totalTransfersOut())
                .netChange(summary.netChange())
                .openingBalance(summary.openingBalance())
                .closingBalance(summary.closingBalance())
                .transactionCount(summary.transactionCount())
                .spendingByCategory(summary.spendingByCategory())
                .build();
    }
}
