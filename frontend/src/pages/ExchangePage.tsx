// ============================================
// Exchange Page - Currency Exchange
// ============================================

import { useState, useEffect } from 'react';
import { Layout } from '../components/layout/Layout';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { Spinner } from '../components/ui/Loading';
import { Badge } from '../components/ui/Badge';
import { 
  ArrowRightLeft, 
  RefreshCw, 
  TrendingUp, 
  Clock,
  CheckCircle,
  AlertCircle
} from 'lucide-react';
import { ExchangeService, WalletService } from '../services/api';
import type { Wallet, ExchangeRates } from '../types';
import { formatCurrency, formatDateTime } from '../lib/formatters';

// Currency display info
const CURRENCY_INFO: Record<string, { name: string; symbol: string }> = {
  USD: { name: 'US Dollar', symbol: '$' },
  EUR: { name: 'Euro', symbol: '€' },
  GBP: { name: 'British Pound', symbol: '£' },
  JPY: { name: 'Japanese Yen', symbol: '¥' },
  CHF: { name: 'Swiss Franc', symbol: 'Fr' },
  CAD: { name: 'Canadian Dollar', symbol: 'C$' },
  AUD: { name: 'Australian Dollar', symbol: 'A$' },
  CNY: { name: 'Chinese Yuan', symbol: '¥' },
};

export default function ExchangePage() {
  // State
  const [wallets, setWallets] = useState<Wallet[]>([]);
  const [rates, setRates] = useState<ExchangeRates | null>(null);
  const [supportedCurrencies, setSupportedCurrencies] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [exchangeLoading, setExchangeLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  
  // Form state
  const [sourceWalletId, setSourceWalletId] = useState('');
  const [destinationWalletId, setDestinationWalletId] = useState('');
  const [amount, setAmount] = useState('');
  const [convertedAmount, setConvertedAmount] = useState<number | null>(null);
  const [exchangeRate, setExchangeRate] = useState<number | null>(null);

  // Load initial data
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [walletsData, ratesData, currencies] = await Promise.all([
        WalletService.getAll(),
        ExchangeService.getRates('USD'),
        ExchangeService.getSupportedCurrencies(),
      ]);
      setWallets(walletsData);
      setRates(ratesData);
      setSupportedCurrencies(currencies);
    } catch {
      setError('Failed to load exchange data');
    } finally {
      setLoading(false);
    }
  };

  // Calculate conversion when inputs change
  useEffect(() => {
    const calculatePreview = async () => {
      const sourceWallet = wallets.find(w => w.id === sourceWalletId);
      const destWallet = wallets.find(w => w.id === destinationWalletId);
      
      if (sourceWallet && destWallet && amount && parseFloat(amount) > 0) {
        try {
          const result = await ExchangeService.calculateConversion(
            parseFloat(amount),
            sourceWallet.currency,
            destWallet.currency
          );
          setConvertedAmount(result.convertedAmount);
          setExchangeRate(result.exchangeRate);
        } catch {
          setConvertedAmount(null);
          setExchangeRate(null);
        }
      } else {
        setConvertedAmount(null);
        setExchangeRate(null);
      }
    };

    const debounce = setTimeout(calculatePreview, 300);
    return () => clearTimeout(debounce);
  }, [sourceWalletId, destinationWalletId, amount, wallets]);

  // Handle exchange
  const handleExchange = async () => {
    const sourceWallet = wallets.find(w => w.id === sourceWalletId);
    const destWallet = wallets.find(w => w.id === destinationWalletId);
    
    if (!sourceWallet || !destWallet || !amount) {
      setError('Please fill in all fields');
      return;
    }

    setExchangeLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const result = await ExchangeService.crossCurrencyTransfer({
        sourceWalletId,
        destinationWalletId,
        amount: parseFloat(amount),
        sourceCurrency: sourceWallet.currency,
        targetCurrency: destWallet.currency,
      });
      
      setSuccess(
        `Successfully exchanged ${formatCurrency(result.sourceAmount, result.sourceCurrency)} ` +
        `to ${formatCurrency(result.convertedAmount, result.targetCurrency)}`
      );
      
      // Reset form
      setAmount('');
      setConvertedAmount(null);
      setExchangeRate(null);
      
      // Refresh wallets
      const updatedWallets = await WalletService.getAll();
      setWallets(updatedWallets);
    } catch {
      setError('Exchange failed. Please try again.');
    } finally {
      setExchangeLoading(false);
    }
  };

  // Swap source and destination
  const handleSwap = () => {
    setSourceWalletId(destinationWalletId);
    setDestinationWalletId(sourceWalletId);
  };

  const sourceWallet = wallets.find(w => w.id === sourceWalletId);
  const destWallet = wallets.find(w => w.id === destinationWalletId);

  if (loading) {
    return (
      <Layout>
        <div className="flex flex-col items-center justify-center min-h-100 gap-4">
          <Spinner size="lg" />
          <p className="text-slate-400">Loading exchange data...</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      {/* Header */}
      <header className="mb-8">
        <h1 className="text-2xl font-bold text-white mb-2">Currency Exchange</h1>
        <p className="text-slate-400">Convert between currencies at competitive rates</p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Exchange Form */}
        <div className="lg:col-span-2">
          <Card className="p-6">
            <h2 className="text-lg font-semibold text-white mb-6 flex items-center gap-2">
              <ArrowRightLeft className="w-5 h-5 text-indigo-400" />
              Exchange Currency
            </h2>

            {/* Alerts */}
            {error && (
              <div className="mb-6 p-4 bg-rose-500/10 border border-rose-500/20 rounded-xl flex items-center gap-3 text-rose-400">
                <AlertCircle className="w-5 h-5 shrink-0" />
                <span>{error}</span>
              </div>
            )}
            
            {success && (
              <div className="mb-6 p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-center gap-3 text-emerald-400">
                <CheckCircle className="w-5 h-5 shrink-0" />
                <span>{success}</span>
              </div>
            )}

            {/* From Section */}
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
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
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
                  Amount to Exchange
                </label>
                <div className="relative">
                  <Input
                    type="number"
                    placeholder="0.00"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    min="0"
                    step="0.01"
                  />
                  {sourceWallet && (
                    <span className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 font-medium">
                      {sourceWallet.currency}
                    </span>
                  )}
                </div>
                {sourceWallet && (
                  <p className="mt-2 text-sm text-slate-500">
                    Available: {formatCurrency(sourceWallet.balance, sourceWallet.currency)}
                  </p>
                )}
              </div>

              {/* Swap Button */}
              <div className="flex justify-center">
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={handleSwap}
                  disabled={!sourceWalletId || !destinationWalletId}
                  className="rounded-full p-3"
                >
                  <RefreshCw className="w-5 h-5" />
                </Button>
              </div>

              {/* To Section */}
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
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

              {/* Conversion Preview */}
              {convertedAmount !== null && exchangeRate !== null && (
                <div className="p-4 bg-slate-800/50 rounded-xl border border-slate-700">
                  <div className="flex justify-between items-center mb-3">
                    <span className="text-slate-400">You will receive</span>
                    <span className="text-2xl font-bold text-white">
                      {destWallet && formatCurrency(convertedAmount, destWallet.currency)}
                    </span>
                  </div>
                  <div className="flex justify-between items-center text-sm">
                    <span className="text-slate-500">Exchange Rate</span>
                    <span className="text-slate-400">
                      1 {sourceWallet?.currency} = {exchangeRate.toFixed(4)} {destWallet?.currency}
                    </span>
                  </div>
                </div>
              )}

              {/* Submit Button */}
              <Button
                variant="primary"
                size="lg"
                className="w-full"
                onClick={handleExchange}
                isLoading={exchangeLoading}
                disabled={!sourceWalletId || !destinationWalletId || !amount || parseFloat(amount) <= 0}
              >
                <ArrowRightLeft className="w-5 h-5 mr-2" />
                Exchange Currency
              </Button>
            </div>
          </Card>
        </div>

        {/* Exchange Rates Panel */}
        <div className="space-y-6">
          {/* Live Rates */}
          <Card className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-white">Live Rates</h3>
              <button
                onClick={() => ExchangeService.getRates('USD').then(setRates)}
                className="p-2 text-slate-400 hover:text-white transition-colors"
              >
                <RefreshCw className="w-4 h-4" />
              </button>
            </div>
            
            {rates && (
              <>
                <p className="text-xs text-slate-500 mb-4 flex items-center gap-1">
                  <Clock className="w-3 h-3" />
                  Updated {formatDateTime(rates.timestamp)}
                </p>
                
                <div className="space-y-3">
                  {Object.entries(rates.rates).slice(0, 6).map(([currency, rate]) => (
                    <div
                      key={currency}
                      className="flex items-center justify-between py-2 border-b border-slate-800 last:border-0"
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-slate-800 flex items-center justify-center text-xs font-bold text-slate-400">
                          {currency.slice(0, 2)}
                        </div>
                        <div>
                          <p className="text-white font-medium">{currency}</p>
                          <p className="text-xs text-slate-500">
                            {CURRENCY_INFO[currency]?.name || currency}
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-white font-medium">{rate.toFixed(4)}</p>
                        <span className="inline-flex items-center gap-1 text-xs text-emerald-400">
                          <TrendingUp className="w-3 h-3" />
                          0.12%
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </>
            )}
          </Card>

          {/* Supported Currencies */}
          <Card className="p-6">
            <h3 className="text-lg font-semibold text-white mb-4">
              Supported Currencies
            </h3>
            <div className="flex flex-wrap gap-2">
              {supportedCurrencies.map(currency => (
                <Badge key={currency} variant="default">
                  {currency}
                </Badge>
              ))}
            </div>
          </Card>
        </div>
      </div>
    </Layout>
  );
}
