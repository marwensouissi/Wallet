package com.fintech.wallet.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Response DTO for monthly summary.
 */
public class MonthlySummaryResponse {

    private String walletId;
    private String month;
    private String currency;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal totalTransfersIn;
    private BigDecimal totalTransfersOut;
    private BigDecimal netChange;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private int transactionCount;
    private Map<String, BigDecimal> spendingByCategory;

    private MonthlySummaryResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public String getWalletId() {
        return walletId;
    }

    public String getMonth() {
        return month;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getTotalDeposits() {
        return totalDeposits;
    }

    public BigDecimal getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public BigDecimal getTotalTransfersIn() {
        return totalTransfersIn;
    }

    public BigDecimal getTotalTransfersOut() {
        return totalTransfersOut;
    }

    public BigDecimal getNetChange() {
        return netChange;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public Map<String, BigDecimal> getSpendingByCategory() {
        return spendingByCategory;
    }

    public static class Builder {
        private final MonthlySummaryResponse response = new MonthlySummaryResponse();

        public Builder walletId(String walletId) {
            response.walletId = walletId;
            return this;
        }

        public Builder month(String month) {
            response.month = month;
            return this;
        }

        public Builder currency(String currency) {
            response.currency = currency;
            return this;
        }

        public Builder totalDeposits(BigDecimal totalDeposits) {
            response.totalDeposits = totalDeposits;
            return this;
        }

        public Builder totalWithdrawals(BigDecimal totalWithdrawals) {
            response.totalWithdrawals = totalWithdrawals;
            return this;
        }

        public Builder totalTransfersIn(BigDecimal totalTransfersIn) {
            response.totalTransfersIn = totalTransfersIn;
            return this;
        }

        public Builder totalTransfersOut(BigDecimal totalTransfersOut) {
            response.totalTransfersOut = totalTransfersOut;
            return this;
        }

        public Builder netChange(BigDecimal netChange) {
            response.netChange = netChange;
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

        public Builder transactionCount(int transactionCount) {
            response.transactionCount = transactionCount;
            return this;
        }

        public Builder spendingByCategory(Map<String, BigDecimal> spendingByCategory) {
            response.spendingByCategory = spendingByCategory;
            return this;
        }

        public MonthlySummaryResponse build() {
            return response;
        }
    }
}
