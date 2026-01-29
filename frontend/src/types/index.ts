// ============================================
// Domain Types - FinTech Wallet Application
// ============================================

// ============ Value Objects ============
export interface Money {
  amount: number;
  currency: string;
}

// ============ Wallet Domain ============
export interface Wallet {
  id: string;
  currency: string;
  balance: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateWalletRequest {
  currency: string;
  ownerName?: string;
}

// ============ Transaction Domain ============
export type TransactionType = 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER';
export type TransactionStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'REVERSED';

export interface Transaction {
  id: string;
  type: TransactionType;
  amount: number;
  currency: string;
  status: TransactionStatus;
  description?: string;
  sourceWalletId?: string;
  destinationWalletId?: string;
  createdAt: string;
}

export interface DepositRequest {
  walletId: string;
  amount: number;
  description?: string;
}

export interface WithdrawRequest {
  walletId: string;
  amount: number;
  description?: string;
}

export interface TransferRequest {
  sourceWalletId: string;
  destinationWalletId: string;
  amount: number;
  description?: string;
}

export interface TransferResponse {
  transactionId: string;
  sourceWalletId: string;
  destinationWalletId: string;
  amount: number;
  currency: string;
  status: TransactionStatus;
  timestamp: string;
}

// ============ Exchange Domain ============
export interface ExchangeRates {
  baseCurrency: string;
  rates: Record<string, number>;
  timestamp: string;
}

export interface CrossCurrencyTransferRequest {
  sourceWalletId: string;
  destinationWalletId: string;
  amount: number;
  sourceCurrency: string;
  targetCurrency: string;
  description?: string;
}

export interface CrossCurrencyTransferResponse {
  transactionId: string;
  sourceAmount: number;
  sourceCurrency: string;
  convertedAmount: number;
  targetCurrency: string;
  exchangeRate: number;
  timestamp: string;
}

// ============ Scheduled Payment Domain ============
export type RecurrencePattern = 
  | 'ONCE' 
  | 'DAILY' 
  | 'WEEKLY' 
  | 'BIWEEKLY' 
  | 'MONTHLY' 
  | 'QUARTERLY' 
  | 'YEARLY';

export type ScheduledPaymentStatus = 
  | 'ACTIVE' 
  | 'PAUSED' 
  | 'COMPLETED' 
  | 'CANCELLED' 
  | 'FAILED';

export interface ScheduledPayment {
  id: string;
  sourceWalletId: string;
  destinationWalletId: string;
  amount: number;
  currency: string;
  description?: string;
  recurrencePattern: RecurrencePattern;
  startDate: string;
  endDate?: string;
  nextExecutionDate: string;
  executionCount: number;
  maxExecutions: number;
  status: ScheduledPaymentStatus;
  createdAt: string;
  lastModifiedAt: string;
}

export interface CreateScheduledPaymentRequest {
  sourceWalletId: string;
  destinationWalletId: string;
  amount: number;
  currency: string;
  description?: string;
  recurrencePattern: RecurrencePattern;
  startDate: string;
  endDate?: string;
  maxExecutions?: number;
}

// ============ Reports Domain ============
export interface AccountStatement {
  walletId: string;
  startDate: string;
  endDate: string;
  openingBalance: number;
  closingBalance: number;
  currency: string;
  transactions: StatementTransaction[];
}

export interface StatementTransaction {
  date: string;
  type: 'CREDIT' | 'DEBIT';
  amount: number;
  description?: string;
  balance: number;
}

export interface MonthlySummary {
  walletId: string;
  year: number;
  month: number;
  totalIncome: number;
  totalExpenses: number;
  netChange: number;
  currency: string;
  transactionCount: number;
}

// ============ API Response Types ============
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: FieldError[];
}

export interface FieldError {
  field: string;
  message: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
