import React from 'react';
import { LayoutDashboard, Wallet, ArrowRightLeft, User, Settings, LogOut } from 'lucide-react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

export function Sidebar() {
    const navItems = [
        { icon: LayoutDashboard, label: 'Dashboard', active: true },
        { icon: Wallet, label: 'My Wallet', active: false },
        { icon: ArrowRightLeft, label: 'Transactions', active: false },
        { icon: User, label: 'Profile', active: false },
        { icon: Settings, label: 'Settings', active: false },
    ];

    return (
        <div className="w-64 h-screen bg-slate-900 border-r border-slate-800 flex flex-col fixed left-0 top-0">
            <div className="p-6">
                <div className="flex items-center gap-3 mb-8">
                    <div className="w-8 h-8 bg-accent-primary rounded-lg flex items-center justify-center">
                        <Wallet className="w-5 h-5 text-white" />
                    </div>
                    <span className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-slate-400">
                        CryptoWall
                    </span>
                </div>

                <nav className="space-y-1">
                    {navItems.map((item) => (
                        <button
                            key={item.label}
                            className={cn(
                                "w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group",
                                item.active
                                    ? "bg-accent-primary/10 text-accent-primary"
                                    : "text-slate-400 hover:text-white hover:bg-slate-800/50"
                            )}
                        >
                            <item.icon className={cn("w-5 h-5", item.active ? "text-accent-primary" : "text-slate-500 group-hover:text-white")} />
                            <span className="font-medium">{item.label}</span>
                            {item.active && (
                                <div className="ml-auto w-1.5 h-1.5 rounded-full bg-accent-primary shadow-[0_0_8px_rgba(99,102,241,0.5)]" />
                            )}
                        </button>
                    ))}
                </nav>
            </div>

            <div className="mt-auto p-6 border-t border-slate-800">
                <button className="w-full flex items-center gap-3 px-4 py-3 text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 rounded-xl transition-colors">
                    <LogOut className="w-5 h-5" />
                    <span className="font-medium">Logout</span>
                </button>
            </div>
        </div>
    );
}
