// ============================================
// useTransactions Hook - Transaction data management
// ============================================

import { useState, useCallback } from 'react';
import { TransactionService, WalletService } from '../services/api';
import { getErrorMessage } from '../services/api/client';
import type { 
  Transaction, 
  DepositRequest, 
  WithdrawRequest, 
  TransferRequest,
  TransferResponse 
} from '../types';

interface UseTransactionsReturn {
  transactions: Transaction[];
  loading: boolean;
  error: string | null;
  fetchTransactions: (walletId: string) => Promise<void>;
  fetchRecentTransactions: (limit?: number) => Promise<void>;
  deposit: (request: DepositRequest) => Promise<Transaction | null>;
  withdraw: (request: WithdrawRequest) => Promise<Transaction | null>;
  transfer: (request: TransferRequest) => Promise<TransferResponse | null>;
}

/**
 * Hook for managing transaction data and operations
 */
export function useTransactions(): UseTransactionsReturn {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchTransactions = useCallback(async (walletId: string) => {
    setLoading(true);
    setError(null);
    try {
      const data = await WalletService.getTransactions(walletId);
      setTransactions(data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchRecentTransactions = useCallback(async (limit: number = 10) => {
    setLoading(true);
    setError(null);
    try {
      const data = await TransactionService.getRecent(limit);
      setTransactions(data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  const deposit = useCallback(async (request: DepositRequest): Promise<Transaction | null> => {
    setLoading(true);
    setError(null);
    try {
      const transaction = await TransactionService.deposit(request);
      setTransactions((prev) => [transaction, ...prev]);
      return transaction;
    } catch (err) {
      setError(getErrorMessage(err));
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const withdraw = useCallback(async (request: WithdrawRequest): Promise<Transaction | null> => {
    setLoading(true);
    setError(null);
    try {
      const transaction = await TransactionService.withdraw(request);
      setTransactions((prev) => [transaction, ...prev]);
      return transaction;
    } catch (err) {
      setError(getErrorMessage(err));
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const transfer = useCallback(async (request: TransferRequest): Promise<TransferResponse | null> => {
    setLoading(true);
    setError(null);
    try {
      const response = await TransactionService.transfer(request);
      // Refresh transactions after transfer
      return response;
    } catch (err) {
      setError(getErrorMessage(err));
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    transactions,
    loading,
    error,
    fetchTransactions,
    fetchRecentTransactions,
    deposit,
    withdraw,
    transfer,
  };
}
