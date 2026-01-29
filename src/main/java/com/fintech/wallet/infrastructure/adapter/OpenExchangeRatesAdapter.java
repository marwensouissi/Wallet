package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.ExchangeRatePort;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter for fetching exchange rates from Open Exchange Rates API.
 * Includes caching to minimize API calls.
 */
@Component
public class OpenExchangeRatesAdapter implements ExchangeRatePort {

    private static final Logger log = LoggerFactory.getLogger(OpenExchangeRatesAdapter.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private static final String BASE_URL = "https://openexchangerates.org/api";

    private final WebClient webClient;
    private final String apiKey;
    private final boolean enabled;

    // Simple in-memory cache
    private final Map<String, CachedRates> ratesCache = new ConcurrentHashMap<>();

    public OpenExchangeRatesAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${exchange.api.key:}") String apiKey,
            @Value("${exchange.api.enabled:false}") boolean enabled) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
        this.apiKey = apiKey;
        this.enabled = enabled && !apiKey.isBlank();
    }

    @Override
    public Optional<ExchangeRate> getExchangeRate(Currency sourceCurrency, Currency targetCurrency) {
        if (!enabled) {
            log.debug("Exchange rate API disabled, using fallback rates");
            return getFallbackRate(sourceCurrency, targetCurrency);
        }

        try {
            Map<String, BigDecimal> rates = getAllRates(sourceCurrency);
            BigDecimal rate = rates.get(targetCurrency.getCode());
            
            if (rate == null) {
                log.warn("Exchange rate not found for {} -> {}", sourceCurrency, targetCurrency);
                return Optional.empty();
            }

            return Optional.of(ExchangeRate.of(
                sourceCurrency,
                targetCurrency,
                rate,
                Instant.now()
            ));
        } catch (Exception e) {
            log.error("Failed to fetch exchange rate: {}", e.getMessage());
            return getFallbackRate(sourceCurrency, targetCurrency);
        }
    }

    @Override
    public Map<String, BigDecimal> getAllRates(Currency baseCurrency) {
        String cacheKey = baseCurrency.getCode();
        CachedRates cached = ratesCache.get(cacheKey);

        if (cached != null && !cached.isExpired()) {
            return cached.rates;
        }

        if (!enabled) {
            return getFallbackRates();
        }

        try {
            ExchangeRatesResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/latest.json")
                    .queryParam("app_id", apiKey)
                    .queryParam("base", baseCurrency.getCode())
                    .build())
                .retrieve()
                .bodyToMono(ExchangeRatesResponse.class)
                .block(Duration.ofSeconds(10));

            if (response != null && response.rates != null) {
                ratesCache.put(cacheKey, new CachedRates(response.rates, Instant.now()));
                return response.rates;
            }
        } catch (Exception e) {
            log.error("Failed to fetch exchange rates from API: {}", e.getMessage());
        }

        return getFallbackRates();
    }

    private Optional<ExchangeRate> getFallbackRate(Currency source, Currency target) {
        Map<String, BigDecimal> fallbackRates = getFallbackRates();
        
        // Convert through USD if needed
        BigDecimal sourceToUsd = source.getCode().equals("USD") ? BigDecimal.ONE 
            : BigDecimal.ONE.divide(fallbackRates.getOrDefault(source.getCode(), BigDecimal.ONE), 6, java.math.RoundingMode.HALF_UP);
        BigDecimal usdToTarget = fallbackRates.getOrDefault(target.getCode(), BigDecimal.ONE);
        
        BigDecimal rate = sourceToUsd.multiply(usdToTarget);
        
        return Optional.of(ExchangeRate.of(source, target, rate, Instant.now()));
    }

    private Map<String, BigDecimal> getFallbackRates() {
        // Static fallback rates (USD base) for when API is unavailable
        return Map.of(
            "USD", BigDecimal.ONE,
            "EUR", new BigDecimal("0.92"),
            "GBP", new BigDecimal("0.79"),
            "CHF", new BigDecimal("0.88"),
            "JPY", new BigDecimal("149.50"),
            "CAD", new BigDecimal("1.36"),
            "AUD", new BigDecimal("1.53"),
            "NZD", new BigDecimal("1.64"),
            "SGD", new BigDecimal("1.34"),
            "HKD", new BigDecimal("7.82")
        );
    }

    private record CachedRates(Map<String, BigDecimal> rates, Instant timestamp) {
        boolean isExpired() {
            return timestamp.plus(CACHE_TTL).isBefore(Instant.now());
        }
    }

    // Response DTO for Open Exchange Rates API
    @SuppressWarnings("unused")
    private static class ExchangeRatesResponse {
        public String disclaimer;
        public String license;
        public long timestamp;
        public String base;
        public Map<String, BigDecimal> rates;
    }
}
