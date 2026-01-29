// ============================================
// Sidebar Component - Main Navigation
// ============================================

import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Wallet,
  ArrowRightLeft,
  Clock,
  BarChart3,
  RefreshCw,
  Settings,
  LogOut,
  HelpCircle,
} from 'lucide-react';
import { cn } from '../../lib/utils';

interface NavItem {
  icon: typeof LayoutDashboard;
  label: string;
  path: string;
}

const mainNavItems: NavItem[] = [
  { icon: LayoutDashboard, label: 'Dashboard', path: '/dashboard' },
  { icon: Wallet, label: 'Wallets', path: '/wallets' },
  { icon: ArrowRightLeft, label: 'Transactions', path: '/transactions' },
  { icon: RefreshCw, label: 'Exchange', path: '/exchange' },
  { icon: Clock, label: 'Scheduled', path: '/scheduled-payments' },
  { icon: BarChart3, label: 'Reports', path: '/reports' },
];

const bottomNavItems: NavItem[] = [
  { icon: Settings, label: 'Settings', path: '/settings' },
  { icon: HelpCircle, label: 'Help', path: '/help' },
];

export function Sidebar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    navigate('/login');
  };

  return (
    <div className="w-64 h-screen bg-slate-900 border-r border-slate-800 flex flex-col fixed left-0 top-0">
      {/* Logo */}
      <div className="p-6">
        <NavLink to="/dashboard" className="flex items-center gap-3 mb-8">
          <div className="w-10 h-10 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center shadow-lg shadow-indigo-500/25">
            <Wallet className="w-5 h-5 text-white" />
          </div>
          <div>
            <span className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-slate-400">
              FinWallet
            </span>
            <p className="text-xs text-slate-500">Pro Banking</p>
          </div>
        </NavLink>

        {/* Main Navigation */}
        <nav className="space-y-1">
          <p className="text-xs font-medium text-slate-500 uppercase tracking-wider mb-3 px-4">
            Menu
          </p>
          {mainNavItems.map((item) => (
            <SidebarLink key={item.path} item={item} />
          ))}
        </nav>
      </div>

      {/* Bottom Section */}
      <div className="mt-auto p-6 space-y-1 border-t border-slate-800">
        <p className="text-xs font-medium text-slate-500 uppercase tracking-wider mb-3 px-4">
          Support
        </p>
        {bottomNavItems.map((item) => (
          <SidebarLink key={item.path} item={item} />
        ))}
        
        {/* Logout Button */}
        <button
          onClick={handleLogout}
          className="w-full flex items-center gap-3 px-4 py-3 mt-4 text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 rounded-xl transition-colors"
        >
          <LogOut className="w-5 h-5" />
          <span className="font-medium">Logout</span>
        </button>
      </div>
    </div>
  );
}

interface SidebarLinkProps {
  item: NavItem;
}

function SidebarLink({ item }: SidebarLinkProps) {
  return (
    <NavLink
      to={item.path}
      className={({ isActive }) =>
        cn(
          'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group',
          isActive
            ? 'bg-accent-primary/10 text-accent-primary'
            : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
        )
      }
    >
      {({ isActive }) => (
        <>
          <item.icon
            className={cn(
              'w-5 h-5',
              isActive ? 'text-accent-primary' : 'text-slate-500 group-hover:text-white'
            )}
          />
          <span className="font-medium">{item.label}</span>
          {isActive && (
            <div className="ml-auto w-1.5 h-1.5 rounded-full bg-accent-primary shadow-[0_0_8px_rgba(99,102,241,0.5)]" />
          )}
        </>
      )}
    </NavLink>
  );
}
