// ============================================
// Reports Page - Financial Analytics & Statements
// ============================================

import { useState, useEffect } from 'react';
import { Layout } from '../components/layout/Layout';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Select } from '../components/ui/Select';
import { Input } from '../components/ui/Input';
import { Badge } from '../components/ui/Badge';
import { Spinner } from '../components/ui/Loading';
import { EmptyState } from '../components/ui/EmptyState';
import { 
  BarChart3,
  Download,
  FileText,
  Calendar,
  TrendingUp,
  TrendingDown,
  ArrowUpRight,
  ArrowDownLeft,
  Wallet,
  AlertCircle
} from 'lucide-react';
import { ReportsService, WalletService } from '../services/api';
import type { Wallet as WalletType, AccountStatement, MonthlySummary } from '../types';
import { formatCurrency, formatDate } from '../lib/formatters';

export default function ReportsPage() {
  // State
  const [wallets, setWallets] = useState<WalletType[]>([]);
  const [selectedWalletId, setSelectedWalletId] = useState('');
  const [loading, setLoading] = useState(true);
  const [reportLoading, setReportLoading] = useState(false);
  const [exportLoading, setExportLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Report data
  const [statement, setStatement] = useState<AccountStatement | null>(null);
  const [monthlySummary, setMonthlySummary] = useState<MonthlySummary | null>(null);

  // Date range
  const [startDate, setStartDate] = useState(() => {
    const date = new Date();
    date.setMonth(date.getMonth() - 1);
    return date.toISOString().split('T')[0];
  });
  const [endDate, setEndDate] = useState(() => new Date().toISOString().split('T')[0]);

  // Load wallets
  useEffect(() => {
    loadWallets();
  }, []);

  const loadWallets = async () => {
    setLoading(true);
    try {
      const walletsData = await WalletService.getAll();
      setWallets(walletsData);
      if (walletsData.length > 0) {
        setSelectedWalletId(walletsData[0].id);
      }
    } catch {
      setError('Failed to load wallets');
    } finally {
      setLoading(false);
    }
  };

  // Generate statement
  const generateStatement = async () => {
    if (!selectedWalletId) return;

    setReportLoading(true);
    setError(null);
    try {
      const [statementData, summaryData] = await Promise.all([
        ReportsService.getStatement(selectedWalletId, startDate, endDate),
        ReportsService.getMonthlySummary(
          selectedWalletId,
          new Date().getFullYear(),
          new Date().getMonth() + 1
        ),
      ]);
      setStatement(statementData);
      setMonthlySummary(summaryData);
    } catch {
      setError('Failed to generate report');
    } finally {
      setReportLoading(false);
    }
  };

  // Export report
  const handleExport = async (format: 'pdf' | 'csv') => {
    if (!selectedWalletId) return;

    setExportLoading(true);
    try {
      const blob = await ReportsService.exportStatement(
        selectedWalletId,
        startDate,
        endDate,
        format
      );
      const wallet = wallets.find(w => w.id === selectedWalletId);
      const filename = `statement-${wallet?.currency || 'wallet'}-${startDate}-to-${endDate}.${format}`;
      ReportsService.downloadFile(blob, filename);
    } catch {
      setError('Failed to export report');
    } finally {
      setExportLoading(false);
    }
  };

  if (loading) {
    return (
      <Layout>
        <div className="flex flex-col items-center justify-center min-h-100 gap-4">
          <Spinner size="lg" />
          <p className="text-slate-400">Loading reports...</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      {/* Header */}
      <header className="mb-8">
        <h1 className="text-2xl font-bold text-white mb-2">Reports & Analytics</h1>
        <p className="text-slate-400">View statements, summaries, and export financial data</p>
      </header>

      {/* Error Alert */}
      {error && (
        <div className="mb-6 p-4 bg-rose-500/10 border border-rose-500/20 rounded-xl flex items-center gap-3 text-rose-400">
          <AlertCircle className="w-5 h-5 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Report Controls */}
      <Card className="p-6 mb-8">
        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
          <FileText className="w-5 h-5 text-indigo-400" />
          Generate Report
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          {/* Wallet Select */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              Wallet
            </label>
            <Select
              value={selectedWalletId}
              onChange={(e) => setSelectedWalletId(e.target.value)}
              options={wallets.map(w => ({
                value: w.id,
                label: `${w.currency} Wallet`
              }))}
            />
          </div>

          {/* Start Date */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              Start Date
            </label>
            <Input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
            />
          </div>

          {/* End Date */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              End Date
            </label>
            <Input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
            />
          </div>

          {/* Generate Button */}
          <div className="flex items-end">
            <Button
              variant="primary"
              className="w-full"
              onClick={generateStatement}
              isLoading={reportLoading}
              disabled={!selectedWalletId}
            >
              <BarChart3 className="w-4 h-4 mr-2" />
              Generate
            </Button>
          </div>
        </div>

        {/* Export Buttons */}
        {statement && (
          <div className="flex gap-3 pt-4 border-t border-slate-800">
            <Button
              variant="outline"
              size="sm"
              onClick={() => handleExport('pdf')}
              disabled={exportLoading}
            >
              <Download className="w-4 h-4 mr-2" />
              Export PDF
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => handleExport('csv')}
              disabled={exportLoading}
            >
              <Download className="w-4 h-4 mr-2" />
              Export CSV
            </Button>
          </div>
        )}
      </Card>

      {/* Report Content */}
      {!statement && !reportLoading && (
        <EmptyState
          icon={BarChart3}
          title="No report generated"
          description="Select a wallet and date range, then click Generate to view your report"
        />
      )}

      {reportLoading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4">
          <Spinner size="lg" />
          <p className="text-slate-400">Generating report...</p>
        </div>
      )}

      {statement && !reportLoading && (
        <div className="space-y-8">
          {/* Summary Cards */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            {/* Opening Balance */}
            <Card className="p-6">
              <div className="flex items-center gap-3 mb-3">
                <div className="w-10 h-10 rounded-full bg-slate-700 flex items-center justify-center">
                  <Wallet className="w-5 h-5 text-slate-400" />
                </div>
                <span className="text-sm text-slate-400">Opening Balance</span>
              </div>
              <p className="text-2xl font-bold text-white">
                {formatCurrency(statement.openingBalance, statement.currency)}
              </p>
              <p className="text-xs text-slate-500 mt-1">{formatDate(statement.startDate)}</p>
            </Card>

            {/* Closing Balance */}
            <Card className="p-6">
              <div className="flex items-center gap-3 mb-3">
                <div className="w-10 h-10 rounded-full bg-indigo-500/20 flex items-center justify-center">
                  <Wallet className="w-5 h-5 text-indigo-400" />
                </div>
                <span className="text-sm text-slate-400">Closing Balance</span>
              </div>
              <p className="text-2xl font-bold text-white">
                {formatCurrency(statement.closingBalance, statement.currency)}
              </p>
              <p className="text-xs text-slate-500 mt-1">{formatDate(statement.endDate)}</p>
            </Card>

            {/* Income */}
            {monthlySummary && (
              <Card className="p-6">
                <div className="flex items-center gap-3 mb-3">
                  <div className="w-10 h-10 rounded-full bg-emerald-500/20 flex items-center justify-center">
                    <TrendingUp className="w-5 h-5 text-emerald-400" />
                  </div>
                  <span className="text-sm text-slate-400">Total Income</span>
                </div>
                <p className="text-2xl font-bold text-emerald-400">
                  +{formatCurrency(monthlySummary.totalIncome, monthlySummary.currency)}
                </p>
                <p className="text-xs text-slate-500 mt-1">This month</p>
              </Card>
            )}

            {/* Expenses */}
            {monthlySummary && (
              <Card className="p-6">
                <div className="flex items-center gap-3 mb-3">
                  <div className="w-10 h-10 rounded-full bg-rose-500/20 flex items-center justify-center">
                    <TrendingDown className="w-5 h-5 text-rose-400" />
                  </div>
                  <span className="text-sm text-slate-400">Total Expenses</span>
                </div>
                <p className="text-2xl font-bold text-rose-400">
                  -{formatCurrency(monthlySummary.totalExpenses, monthlySummary.currency)}
                </p>
                <p className="text-xs text-slate-500 mt-1">This month</p>
              </Card>
            )}
          </div>

          {/* Transaction List */}
          <Card className="p-6">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-semibold text-white flex items-center gap-2">
                <Calendar className="w-5 h-5 text-indigo-400" />
                Statement Transactions
              </h3>
              <Badge variant="info">
                {statement.transactions.length} transactions
              </Badge>
            </div>

            {statement.transactions.length === 0 ? (
              <p className="text-center text-slate-400 py-8">
                No transactions in this period
              </p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-slate-800">
                      <th className="text-left text-sm font-medium text-slate-400 py-3 px-4">Date</th>
                      <th className="text-left text-sm font-medium text-slate-400 py-3 px-4">Type</th>
                      <th className="text-left text-sm font-medium text-slate-400 py-3 px-4">Description</th>
                      <th className="text-right text-sm font-medium text-slate-400 py-3 px-4">Amount</th>
                      <th className="text-right text-sm font-medium text-slate-400 py-3 px-4">Balance</th>
                    </tr>
                  </thead>
                  <tbody>
                    {statement.transactions.map((tx, index) => (
                      <tr 
                        key={index} 
                        className="border-b border-slate-800/50 hover:bg-slate-800/30 transition-colors"
                      >
                        <td className="py-4 px-4 text-sm text-slate-300">
                          {formatDate(tx.date)}
                        </td>
                        <td className="py-4 px-4">
                          <div className="flex items-center gap-2">
                            {tx.type === 'CREDIT' ? (
                              <ArrowDownLeft className="w-4 h-4 text-emerald-400" />
                            ) : (
                              <ArrowUpRight className="w-4 h-4 text-rose-400" />
                            )}
                            <span className={`text-sm ${
                              tx.type === 'CREDIT' ? 'text-emerald-400' : 'text-rose-400'
                            }`}>
                              {tx.type}
                            </span>
                          </div>
                        </td>
                        <td className="py-4 px-4 text-sm text-slate-400">
                          {tx.description || '-'}
                        </td>
                        <td className={`py-4 px-4 text-sm text-right font-medium ${
                          tx.type === 'CREDIT' ? 'text-emerald-400' : 'text-rose-400'
                        }`}>
                          {tx.type === 'CREDIT' ? '+' : '-'}
                          {formatCurrency(tx.amount, statement.currency)}
                        </td>
                        <td className="py-4 px-4 text-sm text-right text-white font-medium">
                          {formatCurrency(tx.balance, statement.currency)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </Card>
        </div>
      )}
    </Layout>
  );
}
