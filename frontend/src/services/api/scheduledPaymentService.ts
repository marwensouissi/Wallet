// ============================================
// Scheduled Payment Service - API Integration
// ============================================

import { apiClient } from './client';
import type { ScheduledPayment, CreateScheduledPaymentRequest } from '../../types';

const SCHEDULED_PAYMENTS_ENDPOINT = '/scheduled-payments';

/**
 * Scheduled Payment Service - Handles recurring and scheduled payments
 */
export const ScheduledPaymentService = {
  /**
   * Create a new scheduled payment
   */
  async create(request: CreateScheduledPaymentRequest): Promise<ScheduledPayment> {
    const response = await apiClient.post<ScheduledPayment>(
      SCHEDULED_PAYMENTS_ENDPOINT,
      request
    );
    return response.data;
  },

  /**
   * Get scheduled payment by ID
   */
  async getById(paymentId: string): Promise<ScheduledPayment> {
    const response = await apiClient.get<ScheduledPayment>(
      `${SCHEDULED_PAYMENTS_ENDPOINT}/${paymentId}`
    );
    return response.data;
  },

  /**
   * Get all scheduled payments for a wallet
   */
  async getByWallet(walletId: string): Promise<ScheduledPayment[]> {
    const response = await apiClient.get<ScheduledPayment[]>(
      `${SCHEDULED_PAYMENTS_ENDPOINT}/wallet/${walletId}`
    );
    return response.data;
  },

  /**
   * Get all scheduled payments
   */
  async getAll(): Promise<ScheduledPayment[]> {
    const response = await apiClient.get<ScheduledPayment[]>(SCHEDULED_PAYMENTS_ENDPOINT);
    return response.data;
  },

  /**
   * Pause a scheduled payment
   */
  async pause(paymentId: string): Promise<ScheduledPayment> {
    const response = await apiClient.post<ScheduledPayment>(
      `${SCHEDULED_PAYMENTS_ENDPOINT}/${paymentId}/pause`
    );
    return response.data;
  },

  /**
   * Resume a paused scheduled payment
   */
  async resume(paymentId: string): Promise<ScheduledPayment> {
    const response = await apiClient.post<ScheduledPayment>(
      `${SCHEDULED_PAYMENTS_ENDPOINT}/${paymentId}/resume`
    );
    return response.data;
  },

  /**
   * Cancel a scheduled payment
   */
  async cancel(paymentId: string): Promise<ScheduledPayment> {
    const response = await apiClient.post<ScheduledPayment>(
      `${SCHEDULED_PAYMENTS_ENDPOINT}/${paymentId}/cancel`
    );
    return response.data;
  },
};
