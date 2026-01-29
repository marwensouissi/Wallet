package com.fintech.wallet.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO for cross-currency transfer.
 */
public class CrossCurrencyTransferResponse {

    private String transactionId;
    private BigDecimal sourceAmount;
    private String sourceCurrency;
    private BigDecimal targetAmount;
    private String targetCurrency;
    private BigDecimal exchangeRate;
    private BigDecimal feeAmount;
    private Instant timestamp;

    private CrossCurrencyTransferResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getSourceAmount() {
        return sourceAmount;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public static class Builder {
        private final CrossCurrencyTransferResponse response = new CrossCurrencyTransferResponse();

        public Builder transactionId(String transactionId) {
            response.transactionId = transactionId;
            return this;
        }

        public Builder sourceAmount(BigDecimal sourceAmount) {
            response.sourceAmount = sourceAmount;
            return this;
        }

        public Builder sourceCurrency(String sourceCurrency) {
            response.sourceCurrency = sourceCurrency;
            return this;
        }

        public Builder targetAmount(BigDecimal targetAmount) {
            response.targetAmount = targetAmount;
            return this;
        }

        public Builder targetCurrency(String targetCurrency) {
            response.targetCurrency = targetCurrency;
            return this;
        }

        public Builder exchangeRate(BigDecimal exchangeRate) {
            response.exchangeRate = exchangeRate;
            return this;
        }

        public Builder feeAmount(BigDecimal feeAmount) {
            response.feeAmount = feeAmount;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            response.timestamp = timestamp;
            return this;
        }

        public CrossCurrencyTransferResponse build() {
            return response;
        }
    }
}
