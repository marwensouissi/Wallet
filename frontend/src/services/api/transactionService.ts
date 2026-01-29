// ============================================
// Transaction Service - API Integration
// ============================================

import { apiClient } from './client';
import type { 
  DepositRequest, 
  WithdrawRequest, 
  TransferRequest, 
  TransferResponse,
  Transaction 
} from '../../types';

const TRANSACTIONS_ENDPOINT = '/transactions';
const WALLETS_ENDPOINT = '/wallets';

/**
 * Transaction Service - Handles deposits, withdrawals, and transfers
 */
export const TransactionService = {
  /**
   * Deposit money into a wallet
   */
  async deposit(request: DepositRequest): Promise<Transaction> {
    const response = await apiClient.post<Transaction>(
      `${WALLETS_ENDPOINT}/${request.walletId}/deposit`,
      { amount: request.amount, description: request.description }
    );
    return response.data;
  },

  /**
   * Withdraw money from a wallet
   */
  async withdraw(request: WithdrawRequest): Promise<Transaction> {
    const response = await apiClient.post<Transaction>(
      `${WALLETS_ENDPOINT}/${request.walletId}/withdraw`,
      { amount: request.amount, description: request.description }
    );
    return response.data;
  },

  /**
   * Transfer money between wallets
   */
  async transfer(request: TransferRequest): Promise<TransferResponse> {
    const response = await apiClient.post<TransferResponse>(
      `${TRANSACTIONS_ENDPOINT}/transfer`,
      request
    );
    return response.data;
  },

  /**
   * Get transaction by ID
   */
  async getById(transactionId: string): Promise<Transaction> {
    const response = await apiClient.get<Transaction>(
      `${TRANSACTIONS_ENDPOINT}/${transactionId}`
    );
    return response.data;
  },

  /**
   * Get recent transactions across all wallets
   */
  async getRecent(limit: number = 10): Promise<Transaction[]> {
    const response = await apiClient.get<Transaction[]>(
      `${TRANSACTIONS_ENDPOINT}/recent`,
      { params: { limit } }
    );
    return response.data;
  },

  /**
   * Get transactions for a specific wallet
   */
  async getByWallet(walletId: string, limit: number = 20): Promise<Transaction[]> {
    const response = await apiClient.get<Transaction[]>(
      `${WALLETS_ENDPOINT}/${walletId}/transactions`,
      { params: { limit } }
    );
    return response.data;
  },
};
