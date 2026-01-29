import React from 'react';
import type { LucideIcon } from 'lucide-react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'glass';
    size?: 'sm' | 'md' | 'lg';
    icon?: LucideIcon;
    iconPosition?: 'left' | 'right';
    isLoading?: boolean;
}

export function Button({
    className,
    variant = 'primary',
    size = 'md',
    icon: Icon,
    iconPosition = 'left',
    isLoading,
    children,
    disabled,
    ...props
}: ButtonProps) {
    const baseStyles = 'inline-flex items-center justify-center font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-accent-primary/50 disabled:opacity-50 disabled:cursor-not-allowed active:scale-[0.98] rounded-xl';

    const variants = {
        primary: 'bg-accent-primary hover:bg-indigo-500 text-white shadow-lg shadow-accent-primary/25 border border-transparent',
        secondary: 'bg-slate-800 hover:bg-slate-700 text-white border border-slate-700',
        outline: 'bg-transparent border border-slate-600 text-slate-300 hover:text-white hover:border-slate-500 hover:bg-slate-800/50',
        ghost: 'bg-transparent text-slate-400 hover:text-white hover:bg-slate-800/50',
        glass: 'glass text-white hover:bg-slate-800/60 shadow-lg',
    };

    const sizes = {
        sm: 'text-sm px-3 py-1.5 gap-1.5',
        md: 'text-sm px-4 py-2.5 gap-2',
        lg: 'text-base px-6 py-3 gap-2.5',
    };

    return (
        <button
            className={cn(baseStyles, variants[variant], sizes[size], className)}
            disabled={isLoading || disabled}
            {...props}
        >
            {isLoading ? (
                <span className="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin mr-2" />
            ) : Icon && iconPosition === 'left' ? (
                <Icon className="w-4 h-4" />
            ) : null}

            {children}

            {!isLoading && Icon && iconPosition === 'right' && (
                <Icon className="w-4 h-4" />
            )}
        </button>
    );
}
