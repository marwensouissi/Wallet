import type { InputHTMLAttributes, ReactNode } from 'react';
import { cn } from './Button';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    icon?: ReactNode;
    error?: string;
}

export const Input = ({ label, icon, error, className, ...props }: InputProps) => {
    return (
        <div className="space-y-2">
            {label && (
                <label className="text-sm font-medium text-gray-300 ml-1 block">
                    {label}
                </label>
            )}
            <div className="relative group">
                <input
                    className={cn(
                        "w-full bg-black/20 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-gray-500 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-all",
                        icon ? "pl-11" : "",
                        error ? "border-red-500/50 focus:border-red-500 focus:ring-red-500" : "",
                        className
                    )}
                    {...props}
                />
                {icon && (
                    <div className="absolute left-3.5 top-3.5 text-gray-500 group-focus-within:text-indigo-400 transition-colors">
                        {icon}
                    </div>
                )}
            </div>
            {error && (
                <p className="text-xs text-red-400 ml-1">{error}</p>
            )}
        </div>
    );
};
