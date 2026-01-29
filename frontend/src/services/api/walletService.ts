// ============================================
// Wallet Service - API Integration
// ============================================

import { apiClient } from './client';
import type { Wallet, CreateWalletRequest, Transaction } from '../../types';

const WALLETS_ENDPOINT = '/wallets';

/**
 * Wallet Service - Handles all wallet-related API operations
 */
export const WalletService = {
  /**
   * Create a new wallet
   */
  async create(request: CreateWalletRequest): Promise<Wallet> {
    const response = await apiClient.post<Wallet>(WALLETS_ENDPOINT, request);
    return response.data;
  },

  /**
   * Get wallet by ID
   */
  async getById(walletId: string): Promise<Wallet> {
    const response = await apiClient.get<Wallet>(`${WALLETS_ENDPOINT}/${walletId}`);
    return response.data;
  },

  /**
   * Get all wallets for current user
   */
  async getAll(): Promise<Wallet[]> {
    const response = await apiClient.get<Wallet[]>(WALLETS_ENDPOINT);
    return response.data;
  },

  /**
   * Get transaction history for a wallet
   */
  async getTransactions(
    walletId: string, 
    params?: { page?: number; size?: number; startDate?: string; endDate?: string }
  ): Promise<Transaction[]> {
    const response = await apiClient.get<Transaction[]>(
      `${WALLETS_ENDPOINT}/${walletId}/transactions`,
      { params }
    );
    return response.data;
  },

  /**
   * Delete a wallet
   */
  async delete(walletId: string): Promise<void> {
    await apiClient.delete(`${WALLETS_ENDPOINT}/${walletId}`);
  },
};
