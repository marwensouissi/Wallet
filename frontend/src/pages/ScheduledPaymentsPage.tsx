// ============================================
// Scheduled Payments Page - Recurring Payments Management
// ============================================

import { useState, useEffect } from 'react';
import { Layout } from '../components/layout/Layout';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { Modal } from '../components/ui/Modal';
import { Badge, getStatusBadgeVariant } from '../components/ui/Badge';
import { EmptyState } from '../components/ui/EmptyState';
import { Spinner } from '../components/ui/Loading';
import { 
  Clock,
  Plus,
  Pause,
  Play,
  XCircle,
  Calendar,
  ArrowRight,
  AlertCircle,
  CheckCircle,
  Wallet
} from 'lucide-react';
import { ScheduledPaymentService, WalletService } from '../services/api';
import type { ScheduledPayment, Wallet as WalletType, RecurrencePattern, CreateScheduledPaymentRequest } from '../types';
import { formatCurrency, formatDate } from '../lib/formatters';

const RECURRENCE_OPTIONS: { value: RecurrencePattern; label: string }[] = [
  { value: 'ONCE', label: 'One Time' },
  { value: 'DAILY', label: 'Daily' },
  { value: 'WEEKLY', label: 'Weekly' },
  { value: 'BIWEEKLY', label: 'Bi-Weekly' },
  { value: 'MONTHLY', label: 'Monthly' },
  { value: 'QUARTERLY', label: 'Quarterly' },
  { value: 'YEARLY', label: 'Yearly' },
];

export default function ScheduledPaymentsPage() {
  // State
  const [payments, setPayments] = useState<ScheduledPayment[]>([]);
  const [wallets, setWallets] = useState<WalletType[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  
  // Modal state
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [createLoading, setCreateLoading] = useState(false);
  
  // Form state
  const [formData, setFormData] = useState<CreateScheduledPaymentRequest>({
    sourceWalletId: '',
    destinationWalletId: '',
    amount: 0,
    currency: 'USD',
    description: '',
    recurrencePattern: 'MONTHLY',
    startDate: new Date().toISOString().split('T')[0],
  });

  // Filter state
  const [statusFilter, setStatusFilter] = useState<string>('all');

  // Load data
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [paymentsData, walletsData] = await Promise.all([
        ScheduledPaymentService.getAll(),
        WalletService.getAll(),
      ]);
      setPayments(paymentsData);
      setWallets(walletsData);
    } catch {
      setError('Failed to load scheduled payments');
    } finally {
      setLoading(false);
    }
  };

  // Create payment
  const handleCreate = async () => {
    if (!formData.sourceWalletId || !formData.destinationWalletId || formData.amount <= 0) {
      setError('Please fill in all required fields');
      return;
    }

    setCreateLoading(true);
    setError(null);

    try {
      const newPayment = await ScheduledPaymentService.create(formData);
      setPayments([...payments, newPayment]);
      setSuccess('Scheduled payment created successfully');
      setShowCreateModal(false);
      resetForm();
    } catch {
      setError('Failed to create scheduled payment');
    } finally {
      setCreateLoading(false);
    }
  };

  // Pause payment
  const handlePause = async (paymentId: string) => {
    setActionLoading(paymentId);
    try {
      const updated = await ScheduledPaymentService.pause(paymentId);
      setPayments(payments.map(p => p.id === paymentId ? updated : p));
      setSuccess('Payment paused');
    } catch {
      setError('Failed to pause payment');
    } finally {
      setActionLoading(null);
    }
  };

  // Resume payment
  const handleResume = async (paymentId: string) => {
    setActionLoading(paymentId);
    try {
      const updated = await ScheduledPaymentService.resume(paymentId);
      setPayments(payments.map(p => p.id === paymentId ? updated : p));
      setSuccess('Payment resumed');
    } catch {
      setError('Failed to resume payment');
    } finally {
      setActionLoading(null);
    }
  };

  // Cancel payment
  const handleCancel = async (paymentId: string) => {
    if (!confirm('Are you sure you want to cancel this scheduled payment?')) {
      return;
    }
    
    setActionLoading(paymentId);
    try {
      const updated = await ScheduledPaymentService.cancel(paymentId);
      setPayments(payments.map(p => p.id === paymentId ? updated : p));
      setSuccess('Payment cancelled');
    } catch {
      setError('Failed to cancel payment');
    } finally {
      setActionLoading(null);
    }
  };

  const resetForm = () => {
    setFormData({
      sourceWalletId: '',
      destinationWalletId: '',
      amount: 0,
      currency: 'USD',
      description: '',
      recurrencePattern: 'MONTHLY',
      startDate: new Date().toISOString().split('T')[0],
    });
  };

  // Filter payments
  const filteredPayments = statusFilter === 'all'
    ? payments
    : payments.filter(p => p.status === statusFilter);

  // Get wallet by ID
  const getWallet = (walletId: string) => wallets.find(w => w.id === walletId);

  if (loading) {
    return (
      <Layout>
        <div className="flex flex-col items-center justify-center min-h-100 gap-4">
          <Spinner size="lg" />
          <p className="text-slate-400">Loading scheduled payments...</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      {/* Header */}
      <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
        <div>
          <h1 className="text-2xl font-bold text-white mb-2">Scheduled Payments</h1>
          <p className="text-slate-400">Manage your recurring and scheduled transfers</p>
        </div>
        <Button variant="primary" icon={Plus} onClick={() => setShowCreateModal(true)}>
          New Schedule
        </Button>
      </header>

      {/* Alerts */}
      {error && (
        <div className="mb-6 p-4 bg-rose-500/10 border border-rose-500/20 rounded-xl flex items-center gap-3 text-rose-400">
          <AlertCircle className="w-5 h-5 shrink-0" />
          <span>{error}</span>
          <button onClick={() => setError(null)} className="ml-auto text-rose-400 hover:text-rose-300">
            <XCircle className="w-4 h-4" />
          </button>
        </div>
      )}
      
      {success && (
        <div className="mb-6 p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-center gap-3 text-emerald-400">
          <CheckCircle className="w-5 h-5 shrink-0" />
          <span>{success}</span>
          <button onClick={() => setSuccess(null)} className="ml-auto text-emerald-400 hover:text-emerald-300">
            <XCircle className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Filters */}
      <div className="flex gap-2 mb-6 overflow-x-auto pb-2">
        {['all', 'ACTIVE', 'PAUSED', 'COMPLETED', 'CANCELLED'].map(status => (
          <button
            key={status}
            onClick={() => setStatusFilter(status)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors whitespace-nowrap ${
              statusFilter === status
                ? 'bg-indigo-500/20 text-indigo-400 border border-indigo-500/30'
                : 'bg-slate-800/50 text-slate-400 border border-slate-700 hover:bg-slate-700/50'
            }`}
          >
            {status === 'all' ? 'All' : status.charAt(0) + status.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      {/* Payments List */}
      {filteredPayments.length === 0 ? (
        <EmptyState
          icon={Clock}
          title="No scheduled payments"
          description="Create your first scheduled payment to automate your transfers"
          actionLabel="Create Schedule"
          onAction={() => setShowCreateModal(true)}
        />
      ) : (
        <div className="grid gap-4">
          {filteredPayments.map(payment => {
            const sourceWallet = getWallet(payment.sourceWalletId);
            const destWallet = getWallet(payment.destinationWalletId);
            const isLoading = actionLoading === payment.id;

            return (
              <Card key={payment.id} className="p-6">
                <div className="flex flex-col lg:flex-row lg:items-center gap-4">
                  {/* Payment Info */}
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-3 mb-2">
                      <div className="w-10 h-10 rounded-full bg-indigo-500/20 flex items-center justify-center">
                        <Clock className="w-5 h-5 text-indigo-400" />
                      </div>
                      <div>
                        <p className="text-white font-semibold">
                          {formatCurrency(payment.amount, payment.currency)}
                        </p>
                        <p className="text-sm text-slate-400">
                          {RECURRENCE_OPTIONS.find(r => r.value === payment.recurrencePattern)?.label}
                        </p>
                      </div>
                    </div>

                    {/* From/To */}
                    <div className="flex items-center gap-2 text-sm text-slate-400 mt-3">
                      <div className="flex items-center gap-1">
                        <Wallet className="w-4 h-4" />
                        <span>{sourceWallet?.currency || 'Unknown'}</span>
                      </div>
                      <ArrowRight className="w-4 h-4 text-slate-600" />
                      <div className="flex items-center gap-1">
                        <Wallet className="w-4 h-4" />
                        <span>{destWallet?.currency || 'Unknown'}</span>
                      </div>
                    </div>

                    {payment.description && (
                      <p className="text-sm text-slate-500 mt-2 truncate">
                        {payment.description}
                      </p>
                    )}
                  </div>

                  {/* Status and Dates */}
                  <div className="flex flex-col items-start lg:items-end gap-2">
                    <Badge variant={getStatusBadgeVariant(payment.status)}>
                      {payment.status}
                    </Badge>
                    
                    <div className="text-xs text-slate-500 space-y-1">
                      <div className="flex items-center gap-1">
                        <Calendar className="w-3 h-3" />
                        <span>Next: {formatDate(payment.nextExecutionDate)}</span>
                      </div>
                      <div>
                        Executed {payment.executionCount} 
                        {payment.maxExecutions > 0 && ` of ${payment.maxExecutions}`} times
                      </div>
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="flex gap-2 pt-4 lg:pt-0 border-t lg:border-0 border-slate-800">
                    {payment.status === 'ACTIVE' && (
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handlePause(payment.id)}
                        disabled={isLoading}
                      >
                        {isLoading ? <Spinner size="sm" /> : <Pause className="w-4 h-4" />}
                        <span className="ml-1">Pause</span>
                      </Button>
                    )}
                    
                    {payment.status === 'PAUSED' && (
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleResume(payment.id)}
                        disabled={isLoading}
                      >
                        {isLoading ? <Spinner size="sm" /> : <Play className="w-4 h-4" />}
                        <span className="ml-1">Resume</span>
                      </Button>
                    )}
                    
                    {(payment.status === 'ACTIVE' || payment.status === 'PAUSED') && (
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleCancel(payment.id)}
                        disabled={isLoading}
                        className="text-rose-400 hover:text-rose-300 hover:bg-rose-500/10"
                      >
                        <XCircle className="w-4 h-4" />
                        <span className="ml-1">Cancel</span>
                      </Button>
                    )}
                  </div>
                </div>
              </Card>
            );
          })}
        </div>
      )}

      {/* Create Modal */}
      <Modal
        isOpen={showCreateModal}
        onClose={() => {
          setShowCreateModal(false);
          resetForm();
        }}
        title="Create Scheduled Payment"
        description="Set up a new recurring or one-time scheduled payment"
        size="lg"
      >
        <div className="space-y-4">
          {/* Source Wallet */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              From Wallet *
            </label>
            <Select
              value={formData.sourceWalletId}
              onChange={(e) => {
                const wallet = wallets.find(w => w.id === e.target.value);
                setFormData({
                  ...formData,
                  sourceWalletId: e.target.value,
                  currency: wallet?.currency || formData.currency,
                });
              }}
              options={[
                { value: '', label: 'Select source wallet' },
                ...wallets
                  .filter(w => w.id !== formData.destinationWalletId)
                  .map(w => ({
                    value: w.id,
                    label: `${w.currency} - ${formatCurrency(w.balance, w.currency)}`
                  }))
              ]}
            />
          </div>

          {/* Destination Wallet */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              To Wallet *
            </label>
            <Select
              value={formData.destinationWalletId}
              onChange={(e) => setFormData({ ...formData, destinationWalletId: e.target.value })}
              options={[
                { value: '', label: 'Select destination wallet' },
                ...wallets
                  .filter(w => w.id !== formData.sourceWalletId)
                  .map(w => ({
                    value: w.id,
                    label: `${w.currency} - ${formatCurrency(w.balance, w.currency)}`
                  }))
              ]}
            />
          </div>

          {/* Amount */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              Amount *
            </label>
            <Input
              type="number"
              placeholder="0.00"
              value={formData.amount || ''}
              onChange={(e) => setFormData({ ...formData, amount: parseFloat(e.target.value) || 0 })}
              min="0"
              step="0.01"
            />
          </div>

          {/* Recurrence */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              Frequency *
            </label>
            <Select
              value={formData.recurrencePattern}
              onChange={(e) => setFormData({ 
                ...formData, 
                recurrencePattern: e.target.value as RecurrencePattern 
              })}
              options={RECURRENCE_OPTIONS.map(r => ({ value: r.value, label: r.label }))}
            />
          </div>

          {/* Start Date */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              Start Date *
            </label>
            <Input
              type="date"
              value={formData.startDate}
              onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
            />
          </div>

          {/* End Date */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              End Date (optional)
            </label>
            <Input
              type="date"
              value={formData.endDate || ''}
              onChange={(e) => setFormData({ ...formData, endDate: e.target.value || undefined })}
            />
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">
              Description (optional)
            </label>
            <Input
              placeholder="e.g., Monthly savings transfer"
              value={formData.description || ''}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            />
          </div>

          {/* Actions */}
          <div className="flex gap-3 pt-4">
            <Button
              variant="ghost"
              className="flex-1"
              onClick={() => {
                setShowCreateModal(false);
                resetForm();
              }}
            >
              Cancel
            </Button>
            <Button
              variant="primary"
              className="flex-1"
              onClick={handleCreate}
              isLoading={createLoading}
              disabled={!formData.sourceWalletId || !formData.destinationWalletId || formData.amount <= 0}
            >
              Create Schedule
            </Button>
          </div>
        </div>
      </Modal>
    </Layout>
  );
}
