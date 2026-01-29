package com.fintech.wallet.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for account statement.
 */
public class AccountStatementResponse {

    private String walletId;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private int totalTransactions;
    private List<StatementEntryDto> entries;

    private AccountStatementResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public String getWalletId() {
        return walletId;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public List<StatementEntryDto> getEntries() {
        return entries;
    }

    public record StatementEntryDto(
        Instant date,
        String type,
        String description,
        BigDecimal amount,
        String currency,
        BigDecimal runningBalance,
        String transactionId
    ) {}

    public static class Builder {
        private final AccountStatementResponse response = new AccountStatementResponse();

        public Builder walletId(String walletId) {
            response.walletId = walletId;
            return this;
        }

        public Builder currency(String currency) {
            response.currency = currency;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            response.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            response.endDate = endDate;
            return this;
        }

        public Builder openingBalance(BigDecimal openingBalance) {
            response.openingBalance = openingBalance;
            return this;
        }

        public Builder closingBalance(BigDecimal closingBalance) {
            response.closingBalance = closingBalance;
            return this;
        }

        public Builder totalTransactions(int totalTransactions) {
            response.totalTransactions = totalTransactions;
            return this;
        }

        public Builder entries(List<StatementEntryDto> entries) {
            response.entries = entries;
            return this;
        }

        public AccountStatementResponse build() {
            return response;
        }
    }
}
