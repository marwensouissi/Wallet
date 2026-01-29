// ============================================
// Wallet Selector Component
// ============================================

import { useState } from 'react';
import { ChevronDown, Wallet as WalletIcon, Check } from 'lucide-react';
import { cn } from '../../lib/utils';
import { formatCurrency } from '../../lib/formatters';
import type { Wallet } from '../../types';

interface WalletSelectorProps {
  wallets: Wallet[];
  selectedWallet: Wallet | null;
  onSelect: (wallet: Wallet) => void;
  label?: string;
  error?: string;
  excludeWalletId?: string;
  disabled?: boolean;
}

export function WalletSelector({
  wallets,
  selectedWallet,
  onSelect,
  label,
  error,
  excludeWalletId,
  disabled = false,
}: WalletSelectorProps) {
  const [isOpen, setIsOpen] = useState(false);

  const filteredWallets = excludeWalletId
    ? wallets.filter((w) => w.id !== excludeWalletId)
    : wallets;

  const handleSelect = (wallet: Wallet) => {
    onSelect(wallet);
    setIsOpen(false);
  };

  return (
    <div className="w-full relative">
      {label && (
        <label className="block text-sm font-medium text-slate-300 mb-2">
          {label}
        </label>
      )}
      
      <button
        type="button"
        onClick={() => !disabled && setIsOpen(!isOpen)}
        disabled={disabled}
        className={cn(
          'w-full flex items-center gap-3 p-3 rounded-xl',
          'bg-slate-800/50 border border-slate-700',
          'transition-all duration-200',
          'focus:outline-none focus:ring-2 focus:ring-accent-primary/50 focus:border-accent-primary',
          isOpen && 'ring-2 ring-accent-primary/50 border-accent-primary',
          error && 'border-rose-500',
          disabled && 'opacity-50 cursor-not-allowed'
        )}
      >
        {selectedWallet ? (
          <>
            <div className="w-10 h-10 rounded-lg bg-accent-primary/20 flex items-center justify-center">
              <WalletIcon className="w-5 h-5 text-accent-primary" />
            </div>
            <div className="flex-1 text-left">
              <p className="font-medium text-white">{selectedWallet.currency} Wallet</p>
              <p className="text-sm text-slate-400">
                Balance: {formatCurrency(selectedWallet.balance, selectedWallet.currency)}
              </p>
            </div>
          </>
        ) : (
          <>
            <div className="w-10 h-10 rounded-lg bg-slate-700 flex items-center justify-center">
              <WalletIcon className="w-5 h-5 text-slate-400" />
            </div>
            <span className="flex-1 text-left text-slate-400">Select a wallet</span>
          </>
        )}
        <ChevronDown
          className={cn(
            'w-5 h-5 text-slate-400 transition-transform',
            isOpen && 'rotate-180'
          )}
        />
      </button>

      {/* Dropdown */}
      {isOpen && (
        <div className="absolute z-10 w-full mt-2 py-2 bg-slate-800 border border-slate-700 rounded-xl shadow-xl">
          {filteredWallets.length === 0 ? (
            <p className="px-4 py-3 text-slate-400 text-sm">No wallets available</p>
          ) : (
            filteredWallets.map((wallet) => (
              <button
                key={wallet.id}
                onClick={() => handleSelect(wallet)}
                className={cn(
                  'w-full flex items-center gap-3 px-4 py-3 transition-colors',
                  'hover:bg-slate-700/50',
                  selectedWallet?.id === wallet.id && 'bg-accent-primary/10'
                )}
              >
                <div className="w-10 h-10 rounded-lg bg-slate-700 flex items-center justify-center">
                  <WalletIcon className="w-5 h-5 text-slate-400" />
                </div>
                <div className="flex-1 text-left">
                  <p className="font-medium text-white">{wallet.currency} Wallet</p>
                  <p className="text-sm text-slate-400">
                    {formatCurrency(wallet.balance, wallet.currency)}
                  </p>
                </div>
                {selectedWallet?.id === wallet.id && (
                  <Check className="w-5 h-5 text-accent-primary" />
                )}
              </button>
            ))
          )}
        </div>
      )}

      {error && <p className="mt-2 text-sm text-rose-400">{error}</p>}
    </div>
  );
}
