// ============================================
// useScheduledPayments Hook - Scheduled payment management
// ============================================

import { useState, useCallback } from 'react';
import { ScheduledPaymentService } from '../services/api';
import { getErrorMessage } from '../services/api/client';
import type { ScheduledPayment, CreateScheduledPaymentRequest } from '../types';

interface UseScheduledPaymentsReturn {
  payments: ScheduledPayment[];
  selectedPayment: ScheduledPayment | null;
  loading: boolean;
  error: string | null;
  fetchPayments: () => Promise<void>;
  fetchByWallet: (walletId: string) => Promise<void>;
  createPayment: (request: CreateScheduledPaymentRequest) => Promise<ScheduledPayment | null>;
  pausePayment: (paymentId: string) => Promise<boolean>;
  resumePayment: (paymentId: string) => Promise<boolean>;
  cancelPayment: (paymentId: string) => Promise<boolean>;
  selectPayment: (payment: ScheduledPayment | null) => void;
}

/**
 * Hook for managing scheduled payments
 */
export function useScheduledPayments(): UseScheduledPaymentsReturn {
  const [payments, setPayments] = useState<ScheduledPayment[]>([]);
  const [selectedPayment, setSelectedPayment] = useState<ScheduledPayment | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchPayments = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await ScheduledPaymentService.getAll();
      setPayments(data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchByWallet = useCallback(async (walletId: string) => {
    setLoading(true);
    setError(null);
    try {
      const data = await ScheduledPaymentService.getByWallet(walletId);
      setPayments(data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  const createPayment = useCallback(async (
    request: CreateScheduledPaymentRequest
  ): Promise<ScheduledPayment | null> => {
    setLoading(true);
    setError(null);
    try {
      const newPayment = await ScheduledPaymentService.create(request);
      setPayments((prev) => [...prev, newPayment]);
      return newPayment;
    } catch (err) {
      setError(getErrorMessage(err));
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const updatePaymentInState = useCallback((updatedPayment: ScheduledPayment) => {
    setPayments((prev) =>
      prev.map((p) => (p.id === updatedPayment.id ? updatedPayment : p))
    );
    if (selectedPayment?.id === updatedPayment.id) {
      setSelectedPayment(updatedPayment);
    }
  }, [selectedPayment?.id]);

  const pausePayment = useCallback(async (paymentId: string): Promise<boolean> => {
    setLoading(true);
    setError(null);
    try {
      const updated = await ScheduledPaymentService.pause(paymentId);
      updatePaymentInState(updated);
      return true;
    } catch (err) {
      setError(getErrorMessage(err));
      return false;
    } finally {
      setLoading(false);
    }
  }, [updatePaymentInState]);

  const resumePayment = useCallback(async (paymentId: string): Promise<boolean> => {
    setLoading(true);
    setError(null);
    try {
      const updated = await ScheduledPaymentService.resume(paymentId);
      updatePaymentInState(updated);
      return true;
    } catch (err) {
      setError(getErrorMessage(err));
      return false;
    } finally {
      setLoading(false);
    }
  }, [updatePaymentInState]);

  const cancelPayment = useCallback(async (paymentId: string): Promise<boolean> => {
    setLoading(true);
    setError(null);
    try {
      const updated = await ScheduledPaymentService.cancel(paymentId);
      updatePaymentInState(updated);
      return true;
    } catch (err) {
      setError(getErrorMessage(err));
      return false;
    } finally {
      setLoading(false);
    }
  }, [updatePaymentInState]);

  const selectPayment = useCallback((payment: ScheduledPayment | null) => {
    setSelectedPayment(payment);
  }, []);

  return {
    payments,
    selectedPayment,
    loading,
    error,
    fetchPayments,
    fetchByWallet,
    createPayment,
    pausePayment,
    resumePayment,
    cancelPayment,
    selectPayment,
  };
}
