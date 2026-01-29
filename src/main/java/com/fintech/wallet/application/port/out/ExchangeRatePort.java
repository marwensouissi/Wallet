package com.fintech.wallet.application.port.out;

import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.ExchangeRate;

import java.util.Optional;

/**
 * Output port for fetching exchange rates from external providers.
 * Implemented by infrastructure adapters.
 */
public interface ExchangeRatePort {

    /**
     * Fetches the current exchange rate between two currencies.
     *
     * @param sourceCurrency the source currency
     * @param targetCurrency the target currency
     * @return the exchange rate, or empty if not available
     */
    Optional<ExchangeRate> getExchangeRate(Currency sourceCurrency, Currency targetCurrency);

    /**
     * Fetches all exchange rates for a base currency.
     *
     * @param baseCurrency the base currency
     * @return map of currency codes to rates
     */
    java.util.Map<String, java.math.BigDecimal> getAllRates(Currency baseCurrency);
}
