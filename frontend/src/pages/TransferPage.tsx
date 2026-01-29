// ============================================
// Transfer Page - Send Money Between Wallets
// ============================================

import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Layout } from '../components/layout/Layout';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { Spinner } from '../components/ui/Loading';
import { 
  Send,
  ArrowRight,
  AlertCircle,
  CheckCircle,
  Wallet,
  User,
  MessageSquare
} from 'lucide-react';
import { WalletService, TransactionService } from '../services/api';
import type { Wallet as WalletType } from '../types';
import { formatCurrency } from '../lib/formatters';

export default function TransferPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const preselectedWalletId = searchParams.get('from');

  // State
  const [wallets, setWallets] = useState<WalletType[]>([]);
  const [loading, setLoading] = useState(true);
  const [transferLoading, setTransferLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  // Form state
  const [sourceWalletId, setSourceWalletId] = useState(preselectedWalletId || '');
  const [destinationWalletId, setDestinationWalletId] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');

  // Load wallets
  useEffect(() => {
    loadWallets();
  }, []);

  const loadWallets = async () => {
    setLoading(true);
    try {
      const walletsData = await WalletService.getAll();
      setWallets(walletsData);
      if (preselectedWalletId && walletsData.find(w => w.id === preselectedWalletId)) {
        setSourceWalletId(preselectedWalletId);
      } else if (walletsData.length > 0 && !sourceWalletId) {
        setSourceWalletId(walletsData[0].id);
      }
    } catch {
      setError('Failed to load wallets');
    } finally {
      setLoading(false);
    }
  };

  // Handle transfer
  const handleTransfer = async () => {
    if (!sourceWalletId || !destinationWalletId || !amount || parseFloat(amount) <= 0) {
      setError('Please fill in all required fields');
      return;
    }

    const sourceWallet = wallets.find(w => w.id === sourceWalletId);
    if (sourceWallet && parseFloat(amount) > sourceWallet.balance) {
      setError('Insufficient balance');
      return;
    }

    setTransferLoading(true);
    setError(null);

    try {
      await TransactionService.transfer({
        sourceWalletId,
        destinationWalletId,
        amount: parseFloat(amount),
        description: description || undefined,
      });
      setSuccess(true);
    } catch {
      setError('Transfer failed. Please try again.');
    } finally {
      setTransferLoading(false);
    }
  };

  const sourceWallet = wallets.find(w => w.id === sourceWalletId);
  const destWallet = wallets.find(w => w.id === destinationWalletId);

  if (loading) {
    return (
      <Layout>
        <div className="flex flex-col items-center justify-center min-h-100 gap-4">
          <Spinner size="lg" />
          <p className="text-slate-400">Loading...</p>
        </div>
      </Layout>
    );
  }

  // Success state
  if (success) {
    return (
      <Layout>
        <div className="max-w-md mx-auto">
          <Card className="p-8 text-center">
            <div className="w-16 h-16 rounded-full bg-emerald-500/20 flex items-center justify-center mx-auto mb-6">
              <CheckCircle className="w-8 h-8 text-emerald-400" />
            </div>
            <h2 className="text-2xl font-bold text-white mb-2">Transfer Successful!</h2>
            <p className="text-slate-400 mb-6">
              {formatCurrency(parseFloat(amount), sourceWallet?.currency || 'USD')} has been transferred successfully.
            </p>
            <div className="flex items-center justify-center gap-4 text-sm text-slate-400 mb-8">
              <div className="flex items-center gap-2">
                <Wallet className="w-4 h-4" />
                <span>{sourceWallet?.currency}</span>
              </div>
              <ArrowRight className="w-4 h-4 text-indigo-400" />
              <div className="flex items-center gap-2">
                <Wallet className="w-4 h-4" />
                <span>{destWallet?.currency}</span>
              </div>
            </div>
            <div className="flex gap-3">
              <Button
                variant="outline"
                className="flex-1"
                onClick={() => navigate('/transactions')}
              >
                View Transactions
              </Button>
              <Button
                variant="primary"
                className="flex-1"
                onClick={() => {
                  setSuccess(false);
                  setAmount('');
                  setDescription('');
                  setDestinationWalletId('');
                }}
              >
                New Transfer
              </Button>
            </div>
          </Card>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      {/* Header */}
      <header className="mb-8">
        <h1 className="text-2xl font-bold text-white mb-2">Send Money</h1>
        <p className="text-slate-400">Transfer funds between wallets instantly</p>
      </header>

      <div className="max-w-2xl mx-auto">
        <Card className="p-6">
          {/* Error Alert */}
          {error && (
            <div className="mb-6 p-4 bg-rose-500/10 border border-rose-500/20 rounded-xl flex items-center gap-3 text-rose-400">
              <AlertCircle className="w-5 h-5 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <div className="space-y-6">
            {/* From Wallet */}
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-2">
                <Wallet className="w-4 h-4 inline mr-2" />
                From Wallet
              </label>
              <Select
                value={sourceWalletId}
                onChange={(e) => setSourceWalletId(e.target.value)}
                options={[
                  { value: '', label: 'Select source wallet' },
                  ...wallets
                    .filter(w => w.id !== destinationWalletId)
                    .map(w => ({
                      value: w.id,
                      label: `${w.currency} Wallet - ${formatCurrency(w.balance, w.currency)}`
                    }))
                ]}
              />
              {sourceWallet && (
                <p className="mt-2 text-sm text-slate-500">
                  Available balance: {formatCurrency(sourceWallet.balance, sourceWallet.currency)}
                </p>
              )}
            </div>

            {/* Visual Arrow */}
            <div className="flex justify-center">
              <div className="w-10 h-10 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center">
                <ArrowRight className="w-5 h-5 text-indigo-400 rotate-90" />
              </div>
            </div>

            {/* To Wallet */}
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-2">
                <User className="w-4 h-4 inline mr-2" />
                To Wallet
              </label>
              <Select
                value={destinationWalletId}
                onChange={(e) => setDestinationWalletId(e.target.value)}
                options={[
                  { value: '', label: 'Select destination wallet' },
                  ...wallets
                    .filter(w => w.id !== sourceWalletId)
                    .map(w => ({
                      value: w.id,
                      label: `${w.currency} Wallet - ${formatCurrency(w.balance, w.currency)}`
                    }))
                ]}
              />
            </div>

            {/* Amount */}
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-2">
                Amount
              </label>
              <div className="relative">
                <Input
                  type="number"
                  placeholder="0.00"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  min="0"
                  step="0.01"
                  className="text-2xl font-bold py-4"
                />
                {sourceWallet && (
                  <span className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 font-medium">
                    {sourceWallet.currency}
                  </span>
                )}
              </div>
              {sourceWallet && amount && parseFloat(amount) > 0 && (
                <div className="mt-2 flex items-center justify-between text-sm">
                  <span className="text-slate-500">
                    Remaining balance after transfer
                  </span>
                  <span className={`font-medium ${
                    parseFloat(amount) > sourceWallet.balance ? 'text-rose-400' : 'text-slate-300'
                  }`}>
                    {formatCurrency(sourceWallet.balance - parseFloat(amount), sourceWallet.currency)}
                  </span>
                </div>
              )}
            </div>

            {/* Quick Amount Buttons */}
            {sourceWallet && (
              <div className="flex gap-2">
                {[25, 50, 100, 250].map(quickAmount => (
                  <button
                    key={quickAmount}
                    onClick={() => setAmount(String(Math.min(quickAmount, sourceWallet.balance)))}
                    className="flex-1 py-2 px-3 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-400 hover:text-white text-sm font-medium transition-colors"
                  >
                    {sourceWallet.currency} {quickAmount}
                  </button>
                ))}
                <button
                  onClick={() => setAmount(String(sourceWallet.balance))}
                  className="flex-1 py-2 px-3 rounded-lg bg-indigo-500/20 hover:bg-indigo-500/30 text-indigo-400 text-sm font-medium transition-colors"
                >
                  Max
                </button>
              </div>
            )}

            {/* Description */}
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-2">
                <MessageSquare className="w-4 h-4 inline mr-2" />
                Description (Optional)
              </label>
              <Input
                placeholder="What's this transfer for?"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
            </div>

            {/* Transfer Summary */}
            {sourceWallet && destWallet && amount && parseFloat(amount) > 0 && (
              <div className="p-4 bg-slate-800/50 rounded-xl border border-slate-700">
                <h4 className="text-sm font-medium text-slate-400 mb-3">Transfer Summary</h4>
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-slate-400">Amount</span>
                    <span className="text-white font-medium">
                      {formatCurrency(parseFloat(amount), sourceWallet.currency)}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-400">From</span>
                    <span className="text-white">{sourceWallet.currency} Wallet</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-400">To</span>
                    <span className="text-white">{destWallet.currency} Wallet</span>
                  </div>
                  {sourceWallet.currency !== destWallet.currency && (
                    <p className="text-xs text-amber-400 pt-2">
                      Note: This is a cross-currency transfer. Consider using the Exchange page for better rates.
                    </p>
                  )}
                </div>
              </div>
            )}

            {/* Submit Button */}
            <Button
              variant="primary"
              size="lg"
              className="w-full"
              onClick={handleTransfer}
              isLoading={transferLoading}
              disabled={!sourceWalletId || !destinationWalletId || !amount || parseFloat(amount) <= 0}
            >
              <Send className="w-5 h-5 mr-2" />
              Send Money
            </Button>

            {/* Cancel */}
            <Button
              variant="ghost"
              className="w-full"
              onClick={() => navigate(-1)}
            >
              Cancel
            </Button>
          </div>
        </Card>
      </div>
    </Layout>
  );
}
