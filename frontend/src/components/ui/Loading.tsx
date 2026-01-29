// ============================================
// Loading Components - Spinners and skeletons
// ============================================

import React from 'react';
import { cn } from '../../lib/utils';

interface SpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

export function Spinner({ size = 'md', className }: SpinnerProps) {
  const sizes = {
    sm: 'w-4 h-4 border-2',
    md: 'w-6 h-6 border-2',
    lg: 'w-8 h-8 border-3',
  };

  return (
    <div
      className={cn(
        'rounded-full border-accent-primary border-t-transparent animate-spin',
        sizes[size],
        className
      )}
    />
  );
}

interface LoadingOverlayProps {
  message?: string;
}

export function LoadingOverlay({ message = 'Loading...' }: LoadingOverlayProps) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
      <div className="flex flex-col items-center gap-4 p-8 glass rounded-2xl">
        <Spinner size="lg" />
        <p className="text-slate-300">{message}</p>
      </div>
    </div>
  );
}

interface SkeletonProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'text' | 'circular' | 'rectangular';
  width?: string | number;
  height?: string | number;
}

export function Skeleton({
  className,
  variant = 'rectangular',
  width,
  height,
  ...props
}: SkeletonProps) {
  const variants = {
    text: 'rounded',
    circular: 'rounded-full',
    rectangular: 'rounded-xl',
  };

  return (
    <div
      className={cn(
        'bg-slate-800 animate-pulse',
        variants[variant],
        className
      )}
      style={{ width, height }}
      {...props}
    />
  );
}

export function CardSkeleton() {
  return (
    <div className="glass p-6 rounded-2xl space-y-4">
      <div className="flex items-center justify-between">
        <Skeleton width={100} height={16} />
        <Skeleton variant="circular" width={40} height={40} />
      </div>
      <Skeleton width="60%" height={32} />
      <Skeleton width="40%" height={16} />
    </div>
  );
}

export function TransactionSkeleton() {
  return (
    <div className="flex items-center gap-4 p-4">
      <Skeleton variant="circular" width={44} height={44} />
      <div className="flex-1 space-y-2">
        <Skeleton width="60%" height={16} />
        <Skeleton width="40%" height={12} />
      </div>
      <Skeleton width={80} height={20} />
    </div>
  );
}

export function TableSkeleton({ rows = 5 }: { rows?: number }) {
  return (
    <div className="space-y-3">
      {Array.from({ length: rows }).map((_, i) => (
        <div key={i} className="flex items-center gap-4 p-4 glass rounded-xl">
          <Skeleton width="15%" height={16} />
          <Skeleton width="25%" height={16} />
          <Skeleton width="20%" height={16} />
          <Skeleton width="15%" height={16} />
          <Skeleton width="10%" height={24} />
        </div>
      ))}
    </div>
  );
}
