// ============================================
// Input Component - Form inputs
// ============================================

import React from 'react';
import { cn } from '../../lib/utils';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  icon?: React.ReactNode;
}

export function Input({
  className,
  label,
  error,
  icon,
  id,
  ...props
}: InputProps) {
  const inputId = id || label?.toLowerCase().replace(/\s+/g, '-');

  return (
    <div className="w-full">
      {label && (
        <label
          htmlFor={inputId}
          className="block text-sm font-medium text-slate-300 mb-2"
        >
          {label}
        </label>
      )}
      <div className="relative">
        {icon && (
          <div className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500">
            {icon}
          </div>
        )}
        <input
          id={inputId}
          className={cn(
            'w-full px-4 py-3 rounded-xl',
            'bg-slate-800/50 border border-slate-700',
            'text-white placeholder-slate-500',
            'focus:outline-none focus:ring-2 focus:ring-accent-primary/50 focus:border-accent-primary',
            'transition-all duration-200',
            'disabled:opacity-50 disabled:cursor-not-allowed',
            icon && 'pl-10',
            error && 'border-rose-500 focus:ring-rose-500/50 focus:border-rose-500',
            className
          )}
          {...props}
        />
      </div>
      {error && (
        <p className="mt-2 text-sm text-rose-400">{error}</p>
      )}
    </div>
  );
}

interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
}

export function Textarea({
  className,
  label,
  error,
  id,
  ...props
}: TextareaProps) {
  const textareaId = id || label?.toLowerCase().replace(/\s+/g, '-');

  return (
    <div className="w-full">
      {label && (
        <label
          htmlFor={textareaId}
          className="block text-sm font-medium text-slate-300 mb-2"
        >
          {label}
        </label>
      )}
      <textarea
        id={textareaId}
        className={cn(
          'w-full px-4 py-3 rounded-xl',
          'bg-slate-800/50 border border-slate-700',
          'text-white placeholder-slate-500',
          'focus:outline-none focus:ring-2 focus:ring-accent-primary/50 focus:border-accent-primary',
          'transition-all duration-200',
          'disabled:opacity-50 disabled:cursor-not-allowed',
          'resize-none',
          error && 'border-rose-500 focus:ring-rose-500/50 focus:border-rose-500',
          className
        )}
        {...props}
      />
      {error && (
        <p className="mt-2 text-sm text-rose-400">{error}</p>
      )}
    </div>
  );
}
