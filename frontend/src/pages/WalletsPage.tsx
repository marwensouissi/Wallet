// ============================================
// Wallets Page - List all wallets
// ============================================

import { useState, useEffect } from 'react';
import { Plus, Wallet as WalletIcon } from 'lucide-react';
import { Layout } from '../components/layout/Layout';
import { Button } from '../components/ui/Button';
import { Card } from '../components/ui/Card';
import { Modal } from '../components/ui/Modal';
import { Select } from '../components/ui/Select';
import { CardSkeleton } from '../components/ui/Loading';
import { EmptyState } from '../components/ui/EmptyState';
import { WalletCard } from '../components/wallet/WalletCard';
import { useWallets } from '../hooks';
import { useToast } from '../components/ui/Toast';
import { useNavigate } from 'react-router-dom';

const CURRENCY_OPTIONS = [
  { value: 'USD', label: 'US Dollar (USD)' },
  { value: 'EUR', label: 'Euro (EUR)' },
  { value: 'GBP', label: 'British Pound (GBP)' },
  { value: 'JPY', label: 'Japanese Yen (JPY)' },
  { value: 'CHF', label: 'Swiss Franc (CHF)' },
  { value: 'CAD', label: 'Canadian Dollar (CAD)' },
  { value: 'AUD', label: 'Australian Dollar (AUD)' },
];

export default function WalletsPage() {
  const navigate = useNavigate();
  const { wallets, loading, error, createWallet, fetchWallets } = useWallets();
  const { success, error: showError } = useToast();
  
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [newCurrency, setNewCurrency] = useState('USD');
  const [isCreating, setIsCreating] = useState(false);

  useEffect(() => {
    fetchWallets();
  }, []);

  const handleCreateWallet = async () => {
    setIsCreating(true);
    const wallet = await createWallet({ currency: newCurrency });
    setIsCreating(false);
    
    if (wallet) {
      success('Wallet Created', `Your ${newCurrency} wallet is ready to use.`);
      setIsCreateModalOpen(false);
      setNewCurrency('USD');
    } else if (error) {
      showError('Failed to Create Wallet', error);
    }
  };

  const handleWalletClick = (walletId: string) => {
    navigate(`/wallets/${walletId}`);
  };

  return (
    <Layout>
      {/* Header */}
      <header className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-2xl font-bold text-white mb-1">My Wallets</h1>
          <p className="text-slate-400">Manage your currency wallets</p>
        </div>
        <Button
          icon={Plus}
          variant="primary"
          onClick={() => setIsCreateModalOpen(true)}
        >
          Create Wallet
        </Button>
      </header>

      {/* Wallets Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[1, 2, 3].map((i) => (
            <CardSkeleton key={i} />
          ))}
        </div>
      ) : wallets.length === 0 ? (
        <Card padding="lg">
          <EmptyState
            icon={WalletIcon}
            title="No Wallets Yet"
            description="Create your first wallet to start managing your finances."
            actionLabel="Create Wallet"
            onAction={() => setIsCreateModalOpen(true)}
          />
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {wallets.map((wallet) => (
            <WalletCard
              key={wallet.id}
              wallet={wallet}
              onClick={() => handleWalletClick(wallet.id)}
              showActions
            />
          ))}
        </div>
      )}

      {/* Create Wallet Modal */}
      <Modal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        title="Create New Wallet"
        description="Choose a currency for your new wallet."
      >
        <div className="space-y-6">
          <Select
            label="Currency"
            options={CURRENCY_OPTIONS}
            value={newCurrency}
            onChange={(e) => setNewCurrency(e.target.value)}
          />
          
          <div className="flex gap-3">
            <Button
              variant="outline"
              className="flex-1"
              onClick={() => setIsCreateModalOpen(false)}
            >
              Cancel
            </Button>
            <Button
              variant="primary"
              className="flex-1"
              onClick={handleCreateWallet}
              isLoading={isCreating}
            >
              Create Wallet
            </Button>
          </div>
        </div>
      </Modal>
    </Layout>
  );
}
