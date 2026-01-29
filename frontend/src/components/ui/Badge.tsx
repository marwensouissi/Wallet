// ============================================
// Badge Component - Status badges
// ============================================

import React from 'react';
import { cn } from '../../lib/utils';

type BadgeVariant = 'default' | 'success' | 'warning' | 'error' | 'info';

interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant;
  size?: 'sm' | 'md';
}

export function Badge({
  className,
  variant = 'default',
  size = 'md',
  children,
  ...props
}: BadgeProps) {
  const variants = {
    default: 'bg-slate-700 text-slate-300',
    success: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30',
    warning: 'bg-amber-500/20 text-amber-400 border-amber-500/30',
    error: 'bg-rose-500/20 text-rose-400 border-rose-500/30',
    info: 'bg-blue-500/20 text-blue-400 border-blue-500/30',
  };

  const sizes = {
    sm: 'text-xs px-2 py-0.5',
    md: 'text-xs px-2.5 py-1',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center font-medium rounded-full border',
        variants[variant],
        sizes[size],
        className
      )}
      {...props}
    >
      {children}
    </span>
  );
}

/**
 * Get badge variant for transaction status
 */
export function getStatusBadgeVariant(status: string): BadgeVariant {
  switch (status.toUpperCase()) {
    case 'COMPLETED':
    case 'ACTIVE':
    case 'SUCCESS':
      return 'success';
    case 'PENDING':
    case 'PAUSED':
      return 'warning';
    case 'FAILED':
    case 'CANCELLED':
    case 'REVERSED':
      return 'error';
    default:
      return 'default';
  }
}
