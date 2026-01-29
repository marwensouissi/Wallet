// ============================================
// Transactions Page - All transactions
// ============================================

import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ArrowRightLeft, Filter, Download, Search } from 'lucide-react';
import { Layout } from '../components/layout/Layout';
import { Button } from '../components/ui/Button';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { TransactionSkeleton } from '../components/ui/Loading';
import { EmptyState } from '../components/ui/EmptyState';
import { TransactionList } from '../components/transactions/TransactionItem';
import { WalletSelector } from '../components/common/WalletSelector';
import { DateRangePicker } from '../components/common/DatePicker';
import { useWallets, useTransactions } from '../hooks';
import type { Wallet } from '../types';

const TRANSACTION_TYPE_OPTIONS = [
  { value: '', label: 'All Types' },
  { value: 'DEPOSIT', label: 'Deposits' },
  { value: 'WITHDRAWAL', label: 'Withdrawals' },
  { value: 'TRANSFER', label: 'Transfers' },
];

export default function TransactionsPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { wallets, loading: walletsLoading } = useWallets();
  const { transactions, loading, fetchTransactions } = useTransactions();

  const [selectedWallet, setSelectedWallet] = useState<Wallet | null>(null);
  const [typeFilter, setTypeFilter] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [showFilters, setShowFilters] = useState(false);

  // Set initial wallet from URL params
  useEffect(() => {
    const walletIdParam = searchParams.get('wallet');
    if (walletIdParam && wallets.length > 0) {
      const wallet = wallets.find((w) => w.id === walletIdParam);
      if (wallet) {
        setSelectedWallet(wallet);
      }
    }
  }, [wallets, searchParams]);

  // Fetch transactions when wallet changes
  useEffect(() => {
    if (selectedWallet) {
      fetchTransactions(selectedWallet.id);
    }
  }, [selectedWallet]);

  // Filter transactions
  const filteredTransactions = transactions.filter((tx) => {
    if (typeFilter && tx.type !== typeFilter) return false;
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      const matchesId = tx.id.toLowerCase().includes(query);
      const matchesDesc = tx.description?.toLowerCase().includes(query);
      if (!matchesId && !matchesDesc) return false;
    }
    return true;
  });

  return (
    <Layout>
      {/* Header */}
      <header className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-2xl font-bold text-white mb-1">Transactions</h1>
          <p className="text-slate-400">View and manage all your transactions</p>
        </div>
        <div className="flex gap-3">
          <Button
            icon={Filter}
            variant="outline"
            onClick={() => setShowFilters(!showFilters)}
          >
            Filters
          </Button>
          <Button
            icon={Download}
            variant="secondary"
            onClick={() => navigate('/reports')}
          >
            Export
          </Button>
        </div>
      </header>

      {/* Filters */}
      {showFilters && (
        <Card className="mb-6">
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <WalletSelector
                wallets={wallets}
                selectedWallet={selectedWallet}
                onSelect={setSelectedWallet}
                label="Wallet"
              />
              <Select
                label="Transaction Type"
                options={TRANSACTION_TYPE_OPTIONS}
                value={typeFilter}
                onChange={(e) => setTypeFilter(e.target.value)}
              />
              <DateRangePicker
                startDate={startDate}
                endDate={endDate}
                onStartDateChange={setStartDate}
                onEndDateChange={setEndDate}
              />
            </div>
          </CardContent>
        </Card>
      )}

      {/* Search */}
      <div className="mb-6">
        <Input
          placeholder="Search by ID or description..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          icon={<Search className="w-5 h-5" />}
        />
      </div>

      {/* Transactions List */}
      <Card>
        <CardHeader>
          <CardTitle>
            {selectedWallet
              ? `${selectedWallet.currency} Wallet Transactions`
              : 'Select a Wallet'}
          </CardTitle>
        </CardHeader>
        <CardContent>
          {!selectedWallet ? (
            <div className="py-8">
              <WalletSelector
                wallets={wallets}
                selectedWallet={selectedWallet}
                onSelect={setSelectedWallet}
                label="Choose a wallet to view transactions"
              />
            </div>
          ) : loading || walletsLoading ? (
            <div className="space-y-2">
              {[1, 2, 3, 4, 5].map((i) => (
                <TransactionSkeleton key={i} />
              ))}
            </div>
          ) : filteredTransactions.length === 0 ? (
            <EmptyState
              icon={ArrowRightLeft}
              title="No Transactions Found"
              description={
                transactions.length === 0
                  ? 'This wallet has no transactions yet.'
                  : 'No transactions match your filters.'
              }
            />
          ) : (
            <TransactionList
              transactions={filteredTransactions}
              onItemClick={(tx) => navigate(`/transactions/${tx.id}`)}
            />
          )}
        </CardContent>
      </Card>
    </Layout>
  );
}
