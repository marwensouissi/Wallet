// ============================================
// Amount Input Component - Currency-aware input
// ============================================

import { useState } from 'react';
import { cn } from '../../lib/utils';
import { getCurrencySymbol } from '../../lib/formatters';

interface AmountInputProps {
  value: string;
  onChange: (value: string) => void;
  currency: string;
  label?: string;
  error?: string;
  disabled?: boolean;
  placeholder?: string;
}

export function AmountInput({
  value,
  onChange,
  currency,
  label,
  error,
  disabled = false,
  placeholder = '0.00',
}: AmountInputProps) {
  const [isFocused, setIsFocused] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    // Allow only valid decimal numbers
    if (val === '' || /^\d*\.?\d{0,2}$/.test(val)) {
      onChange(val);
    }
  };

  return (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-slate-300 mb-2">
          {label}
        </label>
      )}
      <div
        className={cn(
          'flex items-center rounded-xl transition-all duration-200',
          'bg-slate-800/50 border',
          isFocused
            ? 'border-accent-primary ring-2 ring-accent-primary/50'
            : 'border-slate-700',
          error && 'border-rose-500',
          disabled && 'opacity-50 cursor-not-allowed'
        )}
      >
        <span className="pl-4 text-slate-400 font-medium">
          {getCurrencySymbol(currency)}
        </span>
        <input
          type="text"
          inputMode="decimal"
          value={value}
          onChange={handleChange}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          disabled={disabled}
          placeholder={placeholder}
          className={cn(
            'flex-1 px-2 py-3 bg-transparent text-white text-lg font-semibold',
            'focus:outline-none placeholder-slate-600',
            'disabled:cursor-not-allowed'
          )}
        />
        <span className="pr-4 text-slate-400 font-medium">
          {currency}
        </span>
      </div>
      {error && <p className="mt-2 text-sm text-rose-400">{error}</p>}
    </div>
  );
}
