// ============================================
// Reports Service - Analytics & Reporting API
// ============================================

import { apiClient } from './client';
import type { AccountStatement, MonthlySummary } from '../../types';

const REPORTS_ENDPOINT = '/reports/wallets';

/**
 * Reports Service - Handles statements and analytics
 */
export const ReportsService = {
  /**
   * Get account statement for a wallet
   */
  async getStatement(
    walletId: string,
    startDate: string,
    endDate: string
  ): Promise<AccountStatement> {
    const response = await apiClient.get<AccountStatement>(
      `${REPORTS_ENDPOINT}/${walletId}/statement`,
      { params: { startDate, endDate } }
    );
    return response.data;
  },

  /**
   * Get monthly summary for a wallet
   */
  async getMonthlySummary(
    walletId: string,
    year: number,
    month: number
  ): Promise<MonthlySummary> {
    const response = await apiClient.get<MonthlySummary>(
      `${REPORTS_ENDPOINT}/${walletId}/monthly-summary`,
      { params: { year, month } }
    );
    return response.data;
  },

  /**
   * Export statement as PDF or CSV
   */
  async exportStatement(
    walletId: string,
    startDate: string,
    endDate: string,
    format: 'pdf' | 'csv'
  ): Promise<Blob> {
    const response = await apiClient.get(
      `${REPORTS_ENDPOINT}/${walletId}/export`,
      {
        params: { startDate, endDate, format },
        responseType: 'blob',
      }
    );
    return response.data;
  },

  /**
   * Download exported file
   */
  downloadFile(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },
};
