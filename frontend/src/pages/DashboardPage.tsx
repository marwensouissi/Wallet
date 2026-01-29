// ============================================
// Dashboard Page - Main Overview
// ============================================

import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Layout } from '../components/layout/Layout';
import { DashboardCard } from '../components/dashboard/DashboardCard';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge, getStatusBadgeVariant } from '../components/ui/Badge';
import { Spinner } from '../components/ui/Loading';
import { 
  Wallet, 
  Send, 
  Plus,
  Bell,
  ArrowRightLeft,
  Clock,
  TrendingUp,
  ArrowUpRight,
  ArrowDownLeft,
  Eye
} from 'lucide-react';
import { WalletService, TransactionService, ScheduledPaymentService } from '../services/api';
import type { Wallet as WalletType, Transaction, ScheduledPayment } from '../types';
import { formatCurrency, formatRelativeTime } from '../lib/formatters';

export default function DashboardPage() {
  const navigate = useNavigate();
  
  // State
  const [wallets, setWallets] = useState<WalletType[]>([]);
  const [recentTransactions, setRecentTransactions] = useState<Transaction[]>([]);
  const [scheduledPayments, setScheduledPayments] = useState<ScheduledPayment[]>([]);
  const [loading, setLoading] = useState(true);

  // Calculate totals
  const totalBalance = wallets.reduce((sum, w) => {
    // Simple conversion - in real app, use exchange rates
    return sum + w.balance;
  }, 0);

  const primaryWallet = wallets.find(w => w.currency === 'USD') || wallets[0];

  // Load data
  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const [walletsData, scheduledData] = await Promise.all([
        WalletService.getAll(),
        ScheduledPaymentService.getAll(),
      ]);
      
      setWallets(walletsData);
      setScheduledPayments(scheduledData.filter(p => p.status === 'ACTIVE').slice(0, 3));

      // Load recent transactions from all wallets
      if (walletsData.length > 0) {
        const allTransactions: Transaction[] = [];
        for (const wallet of walletsData.slice(0, 3)) {
          try {
            const txs = await TransactionService.getByWallet(wallet.id);
            allTransactions.push(...txs);
          } catch {
            // Ignore errors for individual wallets
          }
        }
        // Sort by date and take most recent
        allTransactions.sort((a, b) => 
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        setRecentTransactions(allTransactions.slice(0, 5));
      }
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Layout>
        <div className="flex flex-col items-center justify-center min-h-100 gap-4">
          <Spinner size="lg" />
          <p className="text-slate-400">Loading dashboard...</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      {/* Header */}
      <header className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-2xl font-bold text-white mb-1">Welcome back!</h1>
          <p className="text-slate-400">Here's what's happening with your portfolio today.</p>
        </div>
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm" icon={Bell} className="text-slate-400 hover:text-white">
            <span className="hidden sm:inline">Notifications</span>
          </Button>
          <div className="w-10 h-10 rounded-full bg-slate-800 border border-slate-700 overflow-hidden">
            <div className="w-full h-full flex items-center justify-center text-slate-500 text-sm font-bold">
              U
            </div>
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
              amount={formatCurrency(totalBalance, primaryWallet?.currency || 'USD')}
              change={`${wallets.length} wallet${wallets.length !== 1 ? 's' : ''}`}
              changeType="neutral"
              icon={<Wallet className="w-6 h-6" />}
              className="bg-linear-to-br from-indigo-600/20 to-slate-900/50 border-indigo-500/20"
            />
            <DashboardCard
              title="Active Schedules"
              amount={String(scheduledPayments.length)}
              change="Recurring payments"
              changeType="neutral"
              icon={<Clock className="w-6 h-6" />}
            />
          </div>

          {/* Quick Actions */}
          <Card className="p-6">
            <h3 className="text-sm font-medium text-slate-400 uppercase mb-4">Quick Actions</h3>
            <div className="flex flex-wrap gap-4">
              <Button 
                icon={Send} 
                variant="primary"
                onClick={() => navigate('/transfer')}
              >
                Send Money
              </Button>
              <Button 
                icon={Plus} 
                variant="secondary"
                onClick={() => navigate('/wallets')}
              >
                Add Wallet
              </Button>
              <Button 
                icon={ArrowRightLeft} 
                variant="outline"
                onClick={() => navigate('/exchange')}
              >
                Exchange
              </Button>
            </div>
          </Card>

          {/* Wallets Overview */}
          <Card className="p-6">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-semibold text-white">Your Wallets</h3>
              <Button 
                variant="ghost" 
                size="sm"
                onClick={() => navigate('/wallets')}
              >
                View All
              </Button>
            </div>
            
            {wallets.length === 0 ? (
              <div className="text-center py-8">
                <Wallet className="w-12 h-12 text-slate-600 mx-auto mb-3" />
                <p className="text-slate-400 mb-4">No wallets yet</p>
                <Button 
                  variant="primary" 
                  size="sm"
                  onClick={() => navigate('/wallets')}
                >
                  Create Your First Wallet
                </Button>
              </div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {wallets.slice(0, 4).map(wallet => (
                  <div
                    key={wallet.id}
                    onClick={() => navigate(`/wallets/${wallet.id}`)}
                    className="p-4 bg-slate-800/50 rounded-xl border border-slate-700/50 hover:border-indigo-500/30 hover:bg-slate-800 transition-all cursor-pointer group"
                  >
                    <div className="flex items-center justify-between mb-2">
                      <div className="w-10 h-10 rounded-full bg-indigo-500/20 flex items-center justify-center">
                        <span className="text-sm font-bold text-indigo-400">
                          {wallet.currency}
                        </span>
                      </div>
                      <Eye className="w-4 h-4 text-slate-500 group-hover:text-indigo-400 transition-colors" />
                    </div>
                    <p className="text-white font-semibold text-lg">
                      {formatCurrency(wallet.balance, wallet.currency)}
                    </p>
                    <p className="text-sm text-slate-500">{wallet.currency} Wallet</p>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>

        {/* Right Column - Transactions & Schedules */}
        <div className="lg:col-span-1 space-y-6">
          {/* Recent Transactions */}
          <Card className="p-6">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-semibold text-white">Recent Activity</h3>
              <Button 
                variant="ghost" 
                size="sm"
                onClick={() => navigate('/transactions')}
              >
                View All
              </Button>
            </div>

            {recentTransactions.length === 0 ? (
              <div className="text-center py-8">
                <TrendingUp className="w-10 h-10 text-slate-600 mx-auto mb-3" />
                <p className="text-slate-400 text-sm">No transactions yet</p>
              </div>
            ) : (
              <div className="space-y-3">
                {recentTransactions.map(tx => (
                  <div
                    key={tx.id}
                    className="flex items-center justify-between p-3 rounded-xl hover:bg-slate-800/50 transition-colors"
                  >
                    <div className="flex items-center gap-3">
                      <div className={`w-9 h-9 rounded-full flex items-center justify-center ${
                        tx.type === 'DEPOSIT' 
                          ? 'bg-emerald-500/20 text-emerald-400' 
                          : tx.type === 'WITHDRAWAL'
                          ? 'bg-rose-500/20 text-rose-400'
                          : 'bg-blue-500/20 text-blue-400'
                      }`}>
                        {tx.type === 'DEPOSIT' ? (
                          <ArrowDownLeft className="w-4 h-4" />
                        ) : tx.type === 'WITHDRAWAL' ? (
                          <ArrowUpRight className="w-4 h-4" />
                        ) : (
                          <ArrowRightLeft className="w-4 h-4" />
                        )}
                      </div>
                      <div>
                        <p className="text-white text-sm font-medium">{tx.type}</p>
                        <p className="text-xs text-slate-500">
                          {formatRelativeTime(tx.createdAt)}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className={`font-medium text-sm ${
                        tx.type === 'DEPOSIT' ? 'text-emerald-400' : 'text-white'
                      }`}>
                        {tx.type === 'DEPOSIT' ? '+' : '-'}
                        {formatCurrency(tx.amount, tx.currency)}
                      </p>
                      <Badge variant={getStatusBadgeVariant(tx.status)} size="sm">
                        {tx.status}
                      </Badge>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>

          {/* Upcoming Scheduled Payments */}
          <Card className="p-6">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-semibold text-white">Upcoming Payments</h3>
              <Button 
                variant="ghost" 
                size="sm"
                onClick={() => navigate('/scheduled-payments')}
              >
                View All
              </Button>
            </div>

            {scheduledPayments.length === 0 ? (
              <div className="text-center py-8">
                <Clock className="w-10 h-10 text-slate-600 mx-auto mb-3" />
                <p className="text-slate-400 text-sm">No scheduled payments</p>
              </div>
            ) : (
              <div className="space-y-3">
                {scheduledPayments.map(payment => (
                  <div
                    key={payment.id}
                    className="p-3 rounded-xl bg-slate-800/30 border border-slate-700/50"
                  >
                    <div className="flex items-center justify-between mb-2">
                      <p className="text-white font-medium text-sm">
                        {formatCurrency(payment.amount, payment.currency)}
                      </p>
                      <Badge variant="info" size="sm">
                        {payment.recurrencePattern}
                      </Badge>
                    </div>
                    <p className="text-xs text-slate-500">
                      Next: {formatRelativeTime(payment.nextExecutionDate)}
                    </p>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>
      </div>
    </Layout>
  );
}
