// ============================================
// Transaction Item Component
// ============================================

import { ArrowUpRight, ArrowDownLeft, ArrowRightLeft, Wallet } from 'lucide-react';
import { cn } from '../../lib/utils';
import { Badge, getStatusBadgeVariant } from '../ui/Badge';
import { formatCurrency, formatRelativeTime, truncateId } from '../../lib/formatters';
import type { Transaction, TransactionType } from '../../types';

interface TransactionItemProps {
  transaction: Transaction;
  onClick?: () => void;
}

export function TransactionItem({ transaction, onClick }: TransactionItemProps) {
  const { icon: Icon, color, label } = getTransactionDisplay(transaction.type);

  return (
    <div
      onClick={onClick}
      className={cn(
        'flex items-center gap-4 p-4 rounded-xl transition-all',
        onClick && 'cursor-pointer hover:bg-slate-800/50'
      )}
    >
      {/* Icon */}
      <div className={cn('w-11 h-11 rounded-xl flex items-center justify-center', color)}>
        <Icon className="w-5 h-5" />
      </div>

      {/* Details */}
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2">
          <p className="font-medium text-white">{label}</p>
          <Badge variant={getStatusBadgeVariant(transaction.status)} size="sm">
            {transaction.status}
          </Badge>
        </div>
        <p className="text-sm text-slate-400 truncate">
          {transaction.description || truncateId(transaction.id)}
        </p>
      </div>

      {/* Amount & Time */}
      <div className="text-right">
        <p
          className={cn(
            'font-semibold',
            transaction.type === 'DEPOSIT' ? 'text-emerald-400' : 'text-white'
          )}
        >
          {transaction.type === 'DEPOSIT' ? '+' : '-'}
          {formatCurrency(transaction.amount, transaction.currency)}
        </p>
        <p className="text-sm text-slate-500">
          {formatRelativeTime(transaction.createdAt)}
        </p>
      </div>
    </div>
  );
}

function getTransactionDisplay(type: TransactionType) {
  switch (type) {
    case 'DEPOSIT':
      return {
        icon: ArrowDownLeft,
        color: 'bg-emerald-500/20 text-emerald-400',
        label: 'Deposit',
      };
    case 'WITHDRAWAL':
      return {
        icon: ArrowUpRight,
        color: 'bg-rose-500/20 text-rose-400',
        label: 'Withdrawal',
      };
    case 'TRANSFER':
      return {
        icon: ArrowRightLeft,
        color: 'bg-blue-500/20 text-blue-400',
        label: 'Transfer',
      };
    default:
      return {
        icon: Wallet,
        color: 'bg-slate-700 text-slate-400',
        label: 'Transaction',
      };
  }
}

interface TransactionListProps {
  transactions: Transaction[];
  onItemClick?: (transaction: Transaction) => void;
  emptyMessage?: string;
}

export function TransactionList({
  transactions,
  onItemClick,
  emptyMessage = 'No transactions found',
}: TransactionListProps) {
  if (transactions.length === 0) {
    return (
      <div className="py-12 text-center">
        <Wallet className="w-12 h-12 mx-auto text-slate-600 mb-3" />
        <p className="text-slate-400">{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="divide-y divide-slate-800">
      {transactions.map((transaction) => (
        <TransactionItem
          key={transaction.id}
          transaction={transaction}
          onClick={onItemClick ? () => onItemClick(transaction) : undefined}
        />
      ))}
    </div>
  );
}
