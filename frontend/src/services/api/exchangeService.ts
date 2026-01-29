// ============================================
// Exchange Service - Currency Exchange API
// ============================================

import { apiClient } from './client';
import type { 
  ExchangeRates, 
  CrossCurrencyTransferRequest, 
  CrossCurrencyTransferResponse 
} from '../../types';

const EXCHANGE_ENDPOINT = '/exchange';

/**
 * Exchange Service - Handles currency exchange operations
 */
export const ExchangeService = {
  /**
   * Get current exchange rates
   */
  async getRates(baseCurrency: string = 'USD'): Promise<ExchangeRates> {
    const response = await apiClient.get<ExchangeRates>(
      `${EXCHANGE_ENDPOINT}/rates`,
      { params: { baseCurrency } }
    );
    return response.data;
  },

  /**
   * Perform a cross-currency transfer
   */
  async crossCurrencyTransfer(
    request: CrossCurrencyTransferRequest
  ): Promise<CrossCurrencyTransferResponse> {
    const response = await apiClient.post<CrossCurrencyTransferResponse>(
      `${EXCHANGE_ENDPOINT}/transfer`,
      request
    );
    return response.data;
  },

  /**
   * Get supported currencies
   */
  async getSupportedCurrencies(): Promise<string[]> {
    const response = await apiClient.get<string[]>(`${EXCHANGE_ENDPOINT}/currencies`);
    return response.data;
  },

  /**
   * Calculate converted amount (preview)
   */
  async calculateConversion(
    amount: number,
    sourceCurrency: string,
    targetCurrency: string
  ): Promise<{ convertedAmount: number; exchangeRate: number }> {
    const response = await apiClient.get<{ convertedAmount: number; exchangeRate: number }>(
      `${EXCHANGE_ENDPOINT}/calculate`,
      { params: { amount, sourceCurrency, targetCurrency } }
    );
    return response.data;
  },
};
