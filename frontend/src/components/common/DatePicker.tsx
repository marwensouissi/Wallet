// ============================================
// Date Picker Component
// ============================================

import { Calendar } from 'lucide-react';
import { cn } from '../../lib/utils';

interface DatePickerProps {
  value: string;
  onChange: (value: string) => void;
  label?: string;
  error?: string;
  min?: string;
  max?: string;
  disabled?: boolean;
}

export function DatePicker({
  value,
  onChange,
  label,
  error,
  min,
  max,
  disabled = false,
}: DatePickerProps) {
  return (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-slate-300 mb-2">
          {label}
        </label>
      )}
      <div className="relative">
        <input
          type="date"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          min={min}
          max={max}
          disabled={disabled}
          className={cn(
            'w-full px-4 py-3 pl-11 rounded-xl',
            'bg-slate-800/50 border border-slate-700',
            'text-white',
            'focus:outline-none focus:ring-2 focus:ring-accent-primary/50 focus:border-accent-primary',
            'transition-all duration-200',
            'disabled:opacity-50 disabled:cursor-not-allowed',
            error && 'border-rose-500',
            // Custom styling for date picker
            '[&::-webkit-calendar-picker-indicator]:invert',
            '[&::-webkit-calendar-picker-indicator]:opacity-50',
            '[&::-webkit-calendar-picker-indicator]:hover:opacity-100'
          )}
        />
        <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-500" />
      </div>
      {error && <p className="mt-2 text-sm text-rose-400">{error}</p>}
    </div>
  );
}

interface DateRangePickerProps {
  startDate: string;
  endDate: string;
  onStartDateChange: (value: string) => void;
  onEndDateChange: (value: string) => void;
  startLabel?: string;
  endLabel?: string;
  error?: string;
}

export function DateRangePicker({
  startDate,
  endDate,
  onStartDateChange,
  onEndDateChange,
  startLabel = 'Start Date',
  endLabel = 'End Date',
  error,
}: DateRangePickerProps) {
  return (
    <div className="w-full space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <DatePicker
          value={startDate}
          onChange={onStartDateChange}
          label={startLabel}
          max={endDate || undefined}
        />
        <DatePicker
          value={endDate}
          onChange={onEndDateChange}
          label={endLabel}
          min={startDate || undefined}
        />
      </div>
      {error && <p className="text-sm text-rose-400">{error}</p>}
    </div>
  );
}
