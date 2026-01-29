package com.fintech.wallet.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * Response DTO for exchange rates.
 */
public class ExchangeRatesResponse {

    private String baseCurrency;
    private Map<String, BigDecimal> rates;
    private Instant timestamp;

    private ExchangeRatesResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public static class Builder {
        private final ExchangeRatesResponse response = new ExchangeRatesResponse();

        public Builder baseCurrency(String baseCurrency) {
            response.baseCurrency = baseCurrency;
            return this;
        }

        public Builder rates(Map<String, BigDecimal> rates) {
            response.rates = rates;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            response.timestamp = timestamp;
            return this;
        }

        public ExchangeRatesResponse build() {
            return response;
        }
    }
}
