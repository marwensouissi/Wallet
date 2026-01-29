import React from 'react';
import { Layout } from '../components/layout/Layout';
import { DashboardCard } from '../components/dashboard/DashboardCard';
import { RecentTransactions } from '../components/dashboard/RecentTransactions';
import { Button } from '../components/ui/Button';
import { Wallet, Send, Plus, ArrowDownToLine, Bell } from 'lucide-react';

export default function DashboardPage() {
    return (
        <Layout>
            {/* Header */}
            <header className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">Welcome back, Marwen</h1>
                    <p className="text-slate-400">Here's what's happening with your portfolio today.</p>
                </div>
                <div className="flex items-center gap-4">
                    <Button variant="ghost" size="sm" icon={Bell} className="text-slate-400 hover:text-white">
                        Notifications
                    </Button>
                    <div className="w-10 h-10 rounded-full bg-slate-800 border border-slate-700 overflow-hidden">
                        {/* Placeholder for avatar */}
                        <div className="w-full h-full flex items-center justify-center text-slate-500 text-sm font-bold">M</div>
                    </div>
                </div>
            </header>

            {/* Main Content Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

                {/* Left Column - Cards & Quick Actions */}
                <div className="lg:col-span-2 space-y-8">

                    {/* Balance Cards */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <DashboardCard
                            title="Total Balance"
                            amount="$24,562.00"
                            change="+12.5%"
                            changeType="positive"
                            icon={<Wallet className="w-6 h-6" />}
                            className="bg-gradient-to-br from-indigo-600/20 to-slate-900/50 border-indigo-500/20"
                        />
                        <DashboardCard
                            title="Monthly Spending"
                            amount="$1,240.50"
                            change="-2.4%"
                            changeType="positive" // lower spending is positive usually, but let's keep it simple
                            icon={<ArrowDownToLine className="w-6 h-6" />}
                        />
                    </div>

                    {/* Quick Actions */}
                    <div className="glass p-6 rounded-2xl">
                        <h3 className="text-sm font-medium text-slate-400 uppercase mb-4">Quick Actions</h3>
                        <div className="flex flex-wrap gap-4">
                            <Button icon={Send} variant="primary">Send Crypto</Button>
                            <Button icon={Plus} variant="secondary">Add Money</Button>
                            <Button icon={ArrowDownToLine} variant="outline">Request</Button>
                        </div>
                    </div>

                    {/* Chart Placeholder (Simulated) */}
                    <div className="glass p-6 rounded-2xl min-h-[300px] flex items-center justify-center relative overflow-hidden">
                        <h3 className="absolute top-6 left-6 text-lg font-semibold text-white">Market Overview</h3>
                        <div className="text-slate-500 text-sm">Chart Component Placeholder</div>
                        {/* We could add a real chart later with Recharts */}
                    </div>

                </div>

                {/* Right Column - Transactions */}
                <div className="lg:col-span-1">
                    <RecentTransactions />
                </div>

            </div>
        </Layout>
    );
}
