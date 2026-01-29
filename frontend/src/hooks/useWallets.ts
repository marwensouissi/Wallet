// ============================================
// useWallets Hook - Wallet data management
// ============================================

import { useState, useEffect, useCallback } from 'react';
import { WalletService } from '../services/api';
import { getErrorMessage } from '../services/api/client';
import type { Wallet, CreateWalletRequest } from '../types';

interface UseWalletsReturn {
  wallets: Wallet[];
  selectedWallet: Wallet | null;
  loading: boolean;
  error: string | null;
  fetchWallets: () => Promise<void>;
  selectWallet: (wallet: Wallet | null) => void;
  createWallet: (request: CreateWalletRequest) => Promise<Wallet | null>;
  refreshWallet: (walletId: string) => Promise<void>;
}

/**
 * Hook for managing wallet data
 */
export function useWallets(): UseWalletsReturn {
  const [wallets, setWallets] = useState<Wallet[]>([]);
  const [selectedWallet, setSelectedWallet] = useState<Wallet | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchWallets = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await WalletService.getAll();
      setWallets(data);
      // Auto-select first wallet if none selected
      if (data.length > 0 && !selectedWallet) {
        setSelectedWallet(data[0]);
      }
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [selectedWallet]);

  const selectWallet = useCallback((wallet: Wallet | null) => {
    setSelectedWallet(wallet);
  }, []);

  const createWallet = useCallback(async (request: CreateWalletRequest): Promise<Wallet | null> => {
    setLoading(true);
    setError(null);
    try {
      const newWallet = await WalletService.create(request);
      setWallets((prev) => [...prev, newWallet]);
      return newWallet;
    } catch (err) {
      setError(getErrorMessage(err));
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const refreshWallet = useCallback(async (walletId: string) => {
    try {
      const updatedWallet = await WalletService.getById(walletId);
      setWallets((prev) =>
        prev.map((w) => (w.id === walletId ? updatedWallet : w))
      );
      if (selectedWallet?.id === walletId) {
        setSelectedWallet(updatedWallet);
      }
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }, [selectedWallet?.id]);

  useEffect(() => {
    fetchWallets();
  }, []);

  return {
    wallets,
    selectedWallet,
    loading,
    error,
    fetchWallets,
    selectWallet,
    createWallet,
    refreshWallet,
  };
}
