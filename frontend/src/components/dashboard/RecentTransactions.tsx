import React from 'react';
import { Coffee, ShoppingBag, DollarSign } from 'lucide-react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

export function RecentTransactions() {
    const transactions = [
        { id: 1, name: 'Starbucks Coffee', date: 'Today, 10:23 AM', amount: '-$5.50', type: 'expense', icon: Coffee, category: 'Food' },
        { id: 2, name: 'Salary Deposit', date: 'Yesterday, 9:00 AM', amount: '+$3,200.00', type: 'income', icon: DollarSign, category: 'Income' },
        { id: 3, name: 'Amazon Purchase', date: 'Jan 24, 2:30 PM', amount: '-$124.99', type: 'expense', icon: ShoppingBag, category: 'Shopping' },
        { id: 4, name: 'Freelance Work', date: 'Jan 22, 4:15 PM', amount: '+$450.00', type: 'income', icon: DollarSign, category: 'Income' },
    ];

    return (
        <div className="glass rounded-2xl p-6">
            <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-semibold text-white">Recent Transactions</h3>
                <button className="text-sm text-accent-primary hover:text-indigo-400 transition-colors">View All</button>
            </div>

            <div className="space-y-4">
                {transactions.map((tx) => (
                    <div key={tx.id} className="flex items-center justify-between p-3 rounded-xl hover:bg-slate-800/50 transition-colors group cursor-pointer">
                        <div className="flex items-center gap-4">
                            <div className={cn(
                                "w-10 h-10 rounded-full flex items-center justify-center",
                                tx.type === 'income' ? "bg-emerald-500/10 text-emerald-400" : "bg-slate-800 text-slate-400"
                            )}>
                                <tx.icon className="w-5 h-5" />
                            </div>
                            <div>
                                <div className="font-medium text-white group-hover:text-accent-primary transition-colors">{tx.name}</div>
                                <div className="text-xs text-slate-500">{tx.date}</div>
                            </div>
                        </div>

                        <div className="text-right">
                            <div className={cn(
                                "font-semibold",
                                tx.type === 'income' ? "text-emerald-400" : "text-white"
                            )}>
                                {tx.amount}
                            </div>
                            <div className="text-xs text-slate-500">{tx.category}</div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
