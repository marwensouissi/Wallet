// ============================================
// App.tsx - Main Application with Routing
// ============================================

import { BrowserRouter, Routes, Route } from 'react-router-dom';
import {
  LandingPage,
  LoginPage,
  RegisterPage,
  DashboardPage,
  WalletsPage,
  WalletDetailPage,
  TransactionsPage,
  TransferPage,
  ExchangePage,
  ScheduledPaymentsPage,
  ReportsPage,
  SettingsPage,
  HelpPage,
  NotFoundPage,
} from './pages';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        {/* Protected Routes - Dashboard & Main Features */}
        <Route path="/dashboard" element={<DashboardPage />} />
        
        {/* Wallets */}
        <Route path="/wallets" element={<WalletsPage />} />
        <Route path="/wallets/:walletId" element={<WalletDetailPage />} />
        
        {/* Transactions */}
        <Route path="/transactions" element={<TransactionsPage />} />
        <Route path="/transfer" element={<TransferPage />} />
        
        {/* Exchange */}
        <Route path="/exchange" element={<ExchangePage />} />
        
        {/* Scheduled Payments */}
        <Route path="/scheduled-payments" element={<ScheduledPaymentsPage />} />
        
        {/* Reports */}
        <Route path="/reports" element={<ReportsPage />} />
        
        {/* Settings & Help */}
        <Route path="/settings" element={<SettingsPage />} />
        <Route path="/help" element={<HelpPage />} />
        
        {/* 404 */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
