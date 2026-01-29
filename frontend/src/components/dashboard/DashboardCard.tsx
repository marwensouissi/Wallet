import React from 'react';
import { ArrowUpRight, ArrowDownRight, TrendingUp } from 'lucide-react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

interface DashboardCardProps {
    title: string;
    amount: string;
    change?: string;
    changeType?: 'positive' | 'negative' | 'neutral';
    icon?: React.ReactNode;
    className?: string;
}

export function DashboardCard({
    title,
    amount,
    change,
    changeType = 'neutral',
    icon,
    className
}: DashboardCardProps) {
    return (
        <div className={cn("glass p-6 rounded-2xl relative overflow-hidden group hover:bg-slate-800/50 transition-colors duration-300", className)}>
            <div className="absolute top-0 right-0 p-3 opacity-10 group-hover:opacity-20 transition-opacity">
                {/* Decorative background icon effect */}
                {icon && <div className="scale-150">{icon}</div>}
            </div>

            <div className="flex justify-between items-start mb-4 relative z-10">
                <h3 className="text-slate-400 text-sm font-medium tracking-wide uppercase">{title}</h3>
                {icon && <div className="text-indigo-400">{icon}</div>}
            </div>

            <div className="relative z-10">
                <div className="text-3xl font-bold text-white mb-1 tracking-tight">{amount}</div>

                {change && (
                    <div className={cn(
                        "flex items-center text-sm font-medium",
                        changeType === 'positive' && "text-emerald-400",
                        changeType === 'negative' && "text-rose-400",
                        changeType === 'neutral' && "text-slate-400"
                    )}>
                        {changeType === 'positive' && <ArrowUpRight className="w-4 h-4 mr-1" />}
                        {changeType === 'negative' && <ArrowDownRight className="w-4 h-4 mr-1" />}
                        {changeType === 'neutral' && <TrendingUp className="w-4 h-4 mr-1" />}
                        <span className={changeType === 'positive' ? "bg-emerald-400/10 px-1.5 py-0.5 rounded" : changeType === 'negative' ? "bg-rose-400/10 px-1.5 py-0.5 rounded" : ""}>
                            {change}
                        </span>
                        <span className="text-slate-500 ml-2 font-normal">vs last month</span>
                    </div>
                )}
            </div>
        </div>
    );
}
