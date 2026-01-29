// ============================================
// Wallet Card Component
// ============================================

import { Wallet as WalletIcon, TrendingUp, TrendingDown } from 'lucide-react';
import { cn } from '../../lib/utils';
import type { Wallet } from '../../types';
import { formatCurrency } from '../../lib/formatters';

interface WalletCardProps {
  wallet: Wallet;
  isSelected?: boolean;
  onClick?: () => void;
  showActions?: boolean;
}

export function WalletCard({
  wallet,
  isSelected = false,
  onClick,
  showActions = false,
}: WalletCardProps) {
  // Mock change percentage - in real app would come from API
  const changePercent = 5.2;
  const isPositive = changePercent >= 0;

  return (
    <div
      onClick={onClick}
      className={cn(
        'p-6 rounded-2xl transition-all duration-200',
        'border cursor-pointer',
        isSelected
          ? 'bg-gradient-to-br from-indigo-600/20 to-slate-900/50 border-indigo-500/40 shadow-lg shadow-indigo-500/10'
          : 'glass border-slate-800 hover:border-slate-700 hover:bg-slate-800/50'
      )}
    >
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <div
            className={cn(
              'w-12 h-12 rounded-xl flex items-center justify-center',
              isSelected ? 'bg-indigo-500/20' : 'bg-slate-800'
            )}
          >
            <WalletIcon
              className={cn(
                'w-6 h-6',
                isSelected ? 'text-indigo-400' : 'text-slate-400'
              )}
            />
          </div>
          <div>
            <h3 className="font-semibold text-white">{wallet.currency} Wallet</h3>
            <p className="text-sm text-slate-400 truncate max-w-[120px]">
              {wallet.id.slice(0, 8)}...
            </p>
          </div>
        </div>

        {showActions && (
          <button className="p-2 text-slate-400 hover:text-white hover:bg-slate-700 rounded-lg transition-colors">
            •••
          </button>
        )}
      </div>

      <div className="space-y-1">
        <p className="text-2xl font-bold text-white">
          {formatCurrency(wallet.balance, wallet.currency)}
        </p>
        <div className="flex items-center gap-2">
          {isPositive ? (
            <TrendingUp className="w-4 h-4 text-emerald-400" />
          ) : (
            <TrendingDown className="w-4 h-4 text-rose-400" />
          )}
          <span
            className={cn(
              'text-sm font-medium',
              isPositive ? 'text-emerald-400' : 'text-rose-400'
            )}
          >
            {isPositive ? '+' : ''}{changePercent}%
          </span>
          <span className="text-sm text-slate-500">this month</span>
        </div>
      </div>
    </div>
  );
}

interface WalletCardCompactProps {
  wallet: Wallet;
  isSelected?: boolean;
  onClick?: () => void;
}

export function WalletCardCompact({
  wallet,
  isSelected = false,
  onClick,
}: WalletCardCompactProps) {
  return (
    <button
      onClick={onClick}
      className={cn(
        'w-full flex items-center gap-3 p-3 rounded-xl transition-all',
        isSelected
          ? 'bg-accent-primary/10 border border-accent-primary/30'
          : 'hover:bg-slate-800/50 border border-transparent'
      )}
    >
      <div
        className={cn(
          'w-10 h-10 rounded-lg flex items-center justify-center',
          isSelected ? 'bg-accent-primary/20' : 'bg-slate-800'
        )}
      >
        <WalletIcon
          className={cn(
            'w-5 h-5',
            isSelected ? 'text-accent-primary' : 'text-slate-400'
          )}
        />
      </div>
      <div className="flex-1 text-left">
        <p className="font-medium text-white">{wallet.currency}</p>
        <p className="text-sm text-slate-400">
          {formatCurrency(wallet.balance, wallet.currency)}
        </p>
      </div>
      {isSelected && (
        <div className="w-2 h-2 rounded-full bg-accent-primary" />
      )}
    </button>
  );
}
