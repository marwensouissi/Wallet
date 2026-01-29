// ============================================
// Wallet Detail Page - Single wallet view
// ============================================

import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  Plus, 
  ArrowUpRight, 
  Send,
  Download,
  MoreVertical,
  TrendingUp,
  TrendingDown
} from 'lucide-react';
import { Layout } from '../components/layout/Layout';
import { Button } from '../components/ui/Button';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Modal } from '../components/ui/Modal';
import { Spinner } from '../components/ui/Loading';
import { TransactionList } from '../components/transactions/TransactionItem';
import { AmountInput } from '../components/common/AmountInput';
import { Textarea } from '../components/ui/Input';
import { WalletService } from '../services/api';
import { TransactionService } from '../services/api';
import { useToast } from '../components/ui/Toast';
import { formatCurrency } from '../lib/formatters';
import type { Wallet, Transaction } from '../types';

type ModalType = 'deposit' | 'withdraw' | 'transfer' | null;

export default function WalletDetailPage() {
  const { walletId } = useParams<{ walletId: string }>();
  const navigate = useNavigate();
  const { success, error: showError } = useToast();

  const [wallet, setWallet] = useState<Wallet | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalType, setModalType] = useState<ModalType>(null);
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    if (walletId) {
      loadWalletData();
    }
  }, [walletId]);

  const loadWalletData = async () => {
    if (!walletId) return;
    setLoading(true);
    try {
      const [walletData, transactionData] = await Promise.all([
        WalletService.getById(walletId),
        WalletService.getTransactions(walletId),
      ]);
      setWallet(walletData);
      setTransactions(transactionData);
    } catch (err) {
      showError('Failed to load wallet', 'Please try again later.');
      navigate('/wallets');
    } finally {
      setLoading(false);
    }
  };

  const handleDeposit = async () => {
    if (!walletId || !amount) return;
    setProcessing(true);
    try {
      await TransactionService.deposit({
        walletId,
        amount: parseFloat(amount),
        description: description || undefined,
      });
      success('Deposit Successful', `${formatCurrency(parseFloat(amount), wallet?.currency || 'USD')} has been added.`);
      closeModal();
      loadWalletData();
    } catch (err) {
      showError('Deposit Failed', 'Please try again.');
    } finally {
      setProcessing(false);
    }
  };

  const handleWithdraw = async () => {
    if (!walletId || !amount) return;
    setProcessing(true);
    try {
      await TransactionService.withdraw({
        walletId,
        amount: parseFloat(amount),
        description: description || undefined,
      });
      success('Withdrawal Successful', `${formatCurrency(parseFloat(amount), wallet?.currency || 'USD')} has been withdrawn.`);
      closeModal();
      loadWalletData();
    } catch (err) {
      showError('Withdrawal Failed', 'Insufficient balance or try again.');
    } finally {
      setProcessing(false);
    }
  };

  const closeModal = () => {
    setModalType(null);
    setAmount('');
    setDescription('');
  };

  if (loading) {
    return (
      <Layout>
        <div className="flex items-center justify-center h-96">
          <Spinner size="lg" />
        </div>
      </Layout>
    );
  }

  if (!wallet) {
    return (
      <Layout>
        <div className="text-center py-16">
          <p className="text-slate-400">Wallet not found</p>
          <Button variant="outline" className="mt-4" onClick={() => navigate('/wallets')}>
            Back to Wallets
          </Button>
        </div>
      </Layout>
    );
  }

  // Mock stats - in real app would come from API
  const monthlyChange = 12.5;
  const isPositive = monthlyChange >= 0;

  return (
    <Layout>
      {/* Header */}
      <header className="mb-8">
        <button
          onClick={() => navigate('/wallets')}
          className="flex items-center gap-2 text-slate-400 hover:text-white mb-4 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Wallets</span>
        </button>

        <div className="flex justify-between items-start">
          <div>
            <h1 className="text-2xl font-bold text-white mb-1">
              {wallet.currency} Wallet
            </h1>
            <p className="text-slate-400 font-mono text-sm">{wallet.id}</p>
          </div>
          <Button variant="ghost" size="sm" icon={MoreVertical}>
            Options
          </Button>
        </div>
      </header>

      {/* Balance Card */}
      <Card variant="gradient" className="mb-8">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-slate-400 text-sm mb-1">Available Balance</p>
            <p className="text-4xl font-bold text-white mb-2">
              {formatCurrency(wallet.balance, wallet.currency)}
            </p>
            <div className="flex items-center gap-2">
              {isPositive ? (
                <TrendingUp className="w-4 h-4 text-emerald-400" />
              ) : (
                <TrendingDown className="w-4 h-4 text-rose-400" />
              )}
              <span className={isPositive ? 'text-emerald-400' : 'text-rose-400'}>
                {isPositive ? '+' : ''}{monthlyChange}%
              </span>
              <span className="text-slate-500">this month</span>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="flex gap-3">
            <Button
              icon={Plus}
              variant="primary"
              onClick={() => setModalType('deposit')}
            >
              Deposit
            </Button>
            <Button
              icon={ArrowUpRight}
              variant="secondary"
              onClick={() => setModalType('withdraw')}
            >
              Withdraw
            </Button>
            <Button
              icon={Send}
              variant="outline"
              onClick={() => navigate(`/transactions/transfer?from=${wallet.id}`)}
            >
              Transfer
            </Button>
          </div>
        </div>
      </Card>

      {/* Transactions */}
      <Card>
        <CardHeader className="flex items-center justify-between">
          <CardTitle>Recent Transactions</CardTitle>
          <Button
            variant="ghost"
            size="sm"
            icon={Download}
            onClick={() => navigate(`/reports?wallet=${wallet.id}`)}
          >
            Export
          </Button>
        </CardHeader>
        <CardContent>
          <TransactionList
            transactions={transactions}
            onItemClick={(tx) => navigate(`/transactions/${tx.id}`)}
            emptyMessage="No transactions yet. Make your first deposit!"
          />
        </CardContent>
      </Card>

      {/* Deposit Modal */}
      <Modal
        isOpen={modalType === 'deposit'}
        onClose={closeModal}
        title="Deposit Money"
        description="Add funds to your wallet"
      >
        <div className="space-y-6">
          <AmountInput
            value={amount}
            onChange={setAmount}
            currency={wallet.currency}
            label="Amount"
          />
          <Textarea
            label="Description (optional)"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="e.g., Salary deposit"
            rows={2}
          />
          <div className="flex gap-3">
            <Button variant="outline" className="flex-1" onClick={closeModal}>
              Cancel
            </Button>
            <Button
              variant="primary"
              className="flex-1"
              onClick={handleDeposit}
              isLoading={processing}
              disabled={!amount || parseFloat(amount) <= 0}
            >
              Deposit
            </Button>
          </div>
        </div>
      </Modal>

      {/* Withdraw Modal */}
      <Modal
        isOpen={modalType === 'withdraw'}
        onClose={closeModal}
        title="Withdraw Money"
        description="Remove funds from your wallet"
      >
        <div className="space-y-6">
          <AmountInput
            value={amount}
            onChange={setAmount}
            currency={wallet.currency}
            label="Amount"
          />
          <p className="text-sm text-slate-400">
            Available: {formatCurrency(wallet.balance, wallet.currency)}
          </p>
          <Textarea
            label="Description (optional)"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="e.g., ATM withdrawal"
            rows={2}
          />
          <div className="flex gap-3">
            <Button variant="outline" className="flex-1" onClick={closeModal}>
              Cancel
            </Button>
            <Button
              variant="primary"
              className="flex-1"
              onClick={handleWithdraw}
              isLoading={processing}
              disabled={!amount || parseFloat(amount) <= 0 || parseFloat(amount) > wallet.balance}
            >
              Withdraw
            </Button>
          </div>
        </div>
      </Modal>
    </Layout>
  );
}
