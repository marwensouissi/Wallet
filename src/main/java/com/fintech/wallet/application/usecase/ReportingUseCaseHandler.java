package com.fintech.wallet.application.usecase;

import com.fintech.wallet.application.port.in.ReportingUseCase;
import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.application.port.out.ReportExportPort;
import com.fintech.wallet.domain.exception.WalletNotFoundException;
import com.fintech.wallet.domain.model.LedgerEntry;
import com.fintech.wallet.domain.model.LedgerEntryType;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.AccountStatement;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.MonthlySummary;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Use case handler for generating reports and analytics.
 */
public class ReportingUseCaseHandler implements ReportingUseCase {

    private final LoadWalletPort loadWalletPort;
    private final ReportExportPort reportExportPort;

    public ReportingUseCaseHandler(LoadWalletPort loadWalletPort, ReportExportPort reportExportPort) {
        this.loadWalletPort = Objects.requireNonNull(loadWalletPort);
        this.reportExportPort = Objects.requireNonNull(reportExportPort);
    }

    @Override
    public AccountStatement generateAccountStatement(String walletId, LocalDate startDate, LocalDate endDate) {
        WalletId id = WalletId.of(walletId);
        Wallet wallet = loadWalletPort.loadById(id)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        List<LedgerEntry> allEntries = wallet.getLedgerEntries();
        
        // Filter entries within date range
        List<LedgerEntry> filteredEntries = allEntries.stream()
                .filter(entry -> {
                    LocalDate entryDate = entry.getCreatedAt()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
                })
                .sorted(Comparator.comparing(LedgerEntry::getCreatedAt))
                .toList();

        // Calculate opening balance (sum of entries before start date)
        Money openingBalance = calculateBalanceUpTo(allEntries, startDate, wallet.getCurrency());
        
        // Build statement entries with running balance
        List<AccountStatement.StatementEntry> statementEntries = new ArrayList<>();
        Money runningBalance = openingBalance;
        
        for (LedgerEntry entry : filteredEntries) {
            if (entry.isCredit()) {
                runningBalance = runningBalance.add(entry.getAmount());
            } else {
                runningBalance = Money.of(
                    runningBalance.getAmount().subtract(entry.getAmount().getAmount()),
                    runningBalance.getCurrency()
                );
            }
            
            statementEntries.add(new AccountStatement.StatementEntry(
                entry.getCreatedAt(),
                entry.getType().name(),
                entry.getDescription(),
                entry.getAmount(),
                runningBalance,
                entry.getTransactionId().toString()
            ));
        }

        Money closingBalance = runningBalance;

        return new AccountStatement(
            id,
            wallet.getCurrency(),
            startDate,
            endDate,
            openingBalance,
            closingBalance,
            statementEntries,
            statementEntries.size()
        );
    }

    @Override
    public MonthlySummary generateMonthlySummary(String walletId, YearMonth month) {
        WalletId id = WalletId.of(walletId);
        Wallet wallet = loadWalletPort.loadById(id)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        LocalDate startOfMonth = month.atDay(1);
        LocalDate endOfMonth = month.atEndOfMonth();

        List<LedgerEntry> allEntries = wallet.getLedgerEntries();
        
        // Filter entries for the month
        List<LedgerEntry> monthEntries = allEntries.stream()
                .filter(entry -> {
                    LocalDate entryDate = entry.getCreatedAt()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return !entryDate.isBefore(startOfMonth) && !entryDate.isAfter(endOfMonth);
                })
                .toList();

        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;
        BigDecimal totalTransfersIn = BigDecimal.ZERO;
        BigDecimal totalTransfersOut = BigDecimal.ZERO;
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();

        for (LedgerEntry entry : monthEntries) {
            BigDecimal amount = entry.getAmount().getAmount();
            String description = entry.getDescription().toLowerCase();
            
            if (entry.getType() == LedgerEntryType.CREDIT) {
                if (description.contains("transfer from")) {
                    totalTransfersIn = totalTransfersIn.add(amount);
                } else {
                    totalDeposits = totalDeposits.add(amount);
                }
            } else {
                if (description.contains("transfer to")) {
                    totalTransfersOut = totalTransfersOut.add(amount);
                } else {
                    totalWithdrawals = totalWithdrawals.add(amount);
                }
                
                // Categorize spending
                String category = categorizeTransaction(description);
                spendingByCategory.merge(category, amount, BigDecimal::add);
            }
        }

        Money openingBalance = calculateBalanceUpTo(allEntries, startOfMonth, wallet.getCurrency());
        Money closingBalance = calculateBalanceUpTo(allEntries, endOfMonth.plusDays(1), wallet.getCurrency());

        return MonthlySummary.create(
            id,
            month,
            wallet.getCurrency(),
            totalDeposits,
            totalWithdrawals,
            totalTransfersIn,
            totalTransfersOut,
            openingBalance.getAmount(),
            closingBalance.getAmount(),
            monthEntries.size(),
            spendingByCategory
        );
    }

    @Override
    public byte[] exportStatementToPdf(AccountStatement statement) {
        return reportExportPort.exportToPdf(statement);
    }

    @Override
    public byte[] exportStatementToCsv(AccountStatement statement) {
        return reportExportPort.exportToCsv(statement);
    }

    private Money calculateBalanceUpTo(List<LedgerEntry> entries, LocalDate date, Currency currency) {
        BigDecimal balance = BigDecimal.ZERO;
        
        for (LedgerEntry entry : entries) {
            LocalDate entryDate = entry.getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            
            if (entryDate.isBefore(date)) {
                if (entry.isCredit()) {
                    balance = balance.add(entry.getAmount().getAmount());
                } else {
                    balance = balance.subtract(entry.getAmount().getAmount());
                }
            }
        }
        
        return Money.of(balance.max(BigDecimal.ZERO), currency);
    }

    private String categorizeTransaction(String description) {
        if (description.contains("atm") || description.contains("cash")) {
            return "Cash Withdrawal";
        } else if (description.contains("transfer")) {
            return "Transfers";
        } else if (description.contains("payment") || description.contains("bill")) {
            return "Bill Payments";
        } else if (description.contains("purchase") || description.contains("shop")) {
            return "Shopping";
        } else {
            return "Other";
        }
    }
}
