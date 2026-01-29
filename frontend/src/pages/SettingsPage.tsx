// ============================================
// Settings Page - User Settings & Preferences
// ============================================

import { useState } from 'react';
import { Layout } from '../components/layout/Layout';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select';
import { 
  Settings,
  User,
  Bell,
  Shield,
  Globe,
  CreditCard,
  CheckCircle,
  Save
} from 'lucide-react';

interface SettingsSection {
  id: string;
  label: string;
  icon: typeof Settings;
}

const SETTINGS_SECTIONS: SettingsSection[] = [
  { id: 'profile', label: 'Profile', icon: User },
  { id: 'notifications', label: 'Notifications', icon: Bell },
  { id: 'security', label: 'Security', icon: Shield },
  { id: 'preferences', label: 'Preferences', icon: Globe },
  { id: 'billing', label: 'Billing', icon: CreditCard },
];

export default function SettingsPage() {
  const [activeSection, setActiveSection] = useState('profile');
  const [saveStatus, setSaveStatus] = useState<'idle' | 'saving' | 'saved'>('idle');

  // Profile settings
  const [profile, setProfile] = useState({
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    phone: '+1 (555) 123-4567',
  });

  // Notification settings
  const [notifications, setNotifications] = useState({
    emailNotifications: true,
    pushNotifications: true,
    transactionAlerts: true,
    marketingEmails: false,
    weeklyReports: true,
  });

  // Preferences
  const [preferences, setPreferences] = useState({
    currency: 'USD',
    language: 'en',
    theme: 'dark',
    timezone: 'America/New_York',
  });

  const handleSave = async () => {
    setSaveStatus('saving');
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    setSaveStatus('saved');
    setTimeout(() => setSaveStatus('idle'), 2000);
  };

  const renderSection = () => {
    switch (activeSection) {
      case 'profile':
        return (
          <div className="space-y-6">
            <div>
              <h3 className="text-lg font-semibold text-white mb-4">Profile Information</h3>
              <p className="text-sm text-slate-400 mb-6">
                Update your personal information and contact details.
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
                  First Name
                </label>
                <Input
                  value={profile.firstName}
                  onChange={(e) => setProfile({ ...profile, firstName: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
                  Last Name
                </label>
                <Input
                  value={profile.lastName}
                  onChange={(e) => setProfile({ ...profile, lastName: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
                  Email Address
                </label>
                <Input
                  type="email"
                  value={profile.email}
                  onChange={(e) => setProfile({ ...profile, email: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
                  Phone Number
                </label>
                <Input
                  type="tel"
                  value={profile.phone}
                  onChange={(e) => setProfile({ ...profile, phone: e.target.value })}
                />
              </div>
            </div>
          </div>
        );

      case 'notifications':
        return (
          <div className="space-y-6">
            <div>
              <h3 className="text-lg font-semibold text-white mb-4">Notification Preferences</h3>
              <p className="text-sm text-slate-400 mb-6">
                Choose how you want to receive updates and alerts.
              </p>
            </div>

            <div className="space-y-4">
              {[
                { key: 'emailNotifications', label: 'Email Notifications', description: 'Receive notifications via email' },
                { key: 'pushNotifications', label: 'Push Notifications', description: 'Receive push notifications on your devices' },
                { key: 'transactionAlerts', label: 'Transaction Alerts', description: 'Get notified for every transaction' },
                { key: 'weeklyReports', label: 'Weekly Reports', description: 'Receive weekly summary of your account' },
                { key: 'marketingEmails', label: 'Marketing Emails', description: 'Receive promotional offers and updates' },
              ].map(item => (
                <div key={item.key} className="flex items-center justify-between p-4 bg-slate-800/50 rounded-xl">
                  <div>
                    <p className="text-white font-medium">{item.label}</p>
                    <p className="text-sm text-slate-400">{item.description}</p>
                  </div>
                  <button
                    onClick={() => setNotifications({
                      ...notifications,
                      [item.key]: !notifications[item.key as keyof typeof notifications]
                    })}
                    className={`relative w-12 h-6 rounded-full transition-colors ${
                      notifications[item.key as keyof typeof notifications]
                        ? 'bg-indigo-500'
                        : 'bg-slate-600'
                    }`}
                  >
                    <div className={`absolute top-1 w-4 h-4 rounded-full bg-white transition-transform ${
                      notifications[item.key as keyof typeof notifications]
                        ? 'translate-x-7'
                        : 'translate-x-1'
                    }`} />
                  </button>
                </div>
              ))}
            </div>
          </div>
        );

      case 'security':
        return (
          <div className="space-y-6">
            <div>
              <h3 className="text-lg font-semibold text-white mb-4">Security Settings</h3>
              <p className="text-sm text-slate-400 mb-6">
                Manage your password and security preferences.
              </p>
            </div>

            <div className="space-y-6">
              <Card className="p-4 border border-slate-700">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-white font-medium">Password</p>
                    <p className="text-sm text-slate-400">Last changed 30 days ago</p>
                  </div>
                  <Button variant="outline" size="sm">Change Password</Button>
                </div>
              </Card>

              <Card className="p-4 border border-slate-700">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-white font-medium">Two-Factor Authentication</p>
                    <p className="text-sm text-slate-400">Add an extra layer of security</p>
                  </div>
                  <Button variant="outline" size="sm">Enable 2FA</Button>
                </div>
              </Card>

              <Card className="p-4 border border-slate-700">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-white font-medium">Active Sessions</p>
                    <p className="text-sm text-slate-400">Manage your active sessions</p>
                  </div>
                  <Button variant="outline" size="sm">View Sessions</Button>
                </div>
              </Card>
            </div>
          </div>
        );

      case 'preferences':
        return (
          <div className="space-y-6">
            <div>
              <h3 className="text-lg font-semibold text-white mb-4">App Preferences</h3>
              <p className="text-sm text-slate-400 mb-6">
                Customize your application experience.
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
                  Default Currency
                </label>
                <Select
                  value={preferences.currency}
                  onChange={(e) => setPreferences({ ...preferences, currency: e.target.value })}
                  options={[
                    { value: 'USD', label: 'US Dollar (USD)' },
                    { value: 'EUR', label: 'Euro (EUR)' },
                    { value: 'GBP', label: 'British Pound (GBP)' },
                    { value: 'JPY', label: 'Japanese Yen (JPY)' },
                  ]}
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
                  Language
                </label>
                <Select
                  value={preferences.language}
                  onChange={(e) => setPreferences({ ...preferences, language: e.target.value })}
                  options={[
                    { value: 'en', label: 'English' },
                    { value: 'es', label: 'Spanish' },
                    { value: 'fr', label: 'French' },
                    { value: 'de', label: 'German' },
                  ]}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
                  Theme
                </label>
                <Select
                  value={preferences.theme}
                  onChange={(e) => setPreferences({ ...preferences, theme: e.target.value })}
                  options={[
                    { value: 'dark', label: 'Dark Mode' },
                    { value: 'light', label: 'Light Mode' },
                    { value: 'system', label: 'System Default' },
                  ]}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">
                  Timezone
                </label>
                <Select
                  value={preferences.timezone}
                  onChange={(e) => setPreferences({ ...preferences, timezone: e.target.value })}
                  options={[
                    { value: 'America/New_York', label: 'Eastern Time (ET)' },
                    { value: 'America/Chicago', label: 'Central Time (CT)' },
                    { value: 'America/Denver', label: 'Mountain Time (MT)' },
                    { value: 'America/Los_Angeles', label: 'Pacific Time (PT)' },
                    { value: 'Europe/London', label: 'London (GMT)' },
                    { value: 'Europe/Paris', label: 'Paris (CET)' },
                  ]}
                />
              </div>
            </div>
          </div>
        );

      case 'billing':
        return (
          <div className="space-y-6">
            <div>
              <h3 className="text-lg font-semibold text-white mb-4">Billing & Subscription</h3>
              <p className="text-sm text-slate-400 mb-6">
                Manage your subscription and payment methods.
              </p>
            </div>

            <Card className="p-6 bg-linear-to-br from-indigo-600/20 to-purple-600/20 border border-indigo-500/30">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <p className="text-indigo-400 text-sm font-medium">Current Plan</p>
                  <h4 className="text-2xl font-bold text-white">Pro Plan</h4>
                </div>
                <div className="px-3 py-1 bg-indigo-500/20 rounded-full text-indigo-400 text-sm">
                  Active
                </div>
              </div>
              <p className="text-slate-400 text-sm mb-4">
                Unlimited wallets, priority support, and advanced analytics.
              </p>
              <div className="flex gap-3">
                <Button variant="outline" size="sm">Change Plan</Button>
                <Button variant="ghost" size="sm" className="text-rose-400 hover:text-rose-300">
                  Cancel Subscription
                </Button>
              </div>
            </Card>

            <Card className="p-4 border border-slate-700">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="w-12 h-8 bg-slate-700 rounded flex items-center justify-center">
                    <CreditCard className="w-5 h-5 text-slate-400" />
                  </div>
                  <div>
                    <p className="text-white font-medium">•••• •••• •••• 4242</p>
                    <p className="text-sm text-slate-400">Expires 12/25</p>
                  </div>
                </div>
                <Button variant="ghost" size="sm">Update</Button>
              </div>
            </Card>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <Layout>
      {/* Header */}
      <header className="mb-8">
        <h1 className="text-2xl font-bold text-white mb-2">Settings</h1>
        <p className="text-slate-400">Manage your account settings and preferences</p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
        {/* Settings Navigation */}
        <div className="lg:col-span-1">
          <nav className="space-y-1">
            {SETTINGS_SECTIONS.map(section => (
              <button
                key={section.id}
                onClick={() => setActiveSection(section.id)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors ${
                  activeSection === section.id
                    ? 'bg-indigo-500/10 text-indigo-400'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
                }`}
              >
                <section.icon className="w-5 h-5" />
                <span className="font-medium">{section.label}</span>
              </button>
            ))}
          </nav>
        </div>

        {/* Settings Content */}
        <div className="lg:col-span-3">
          <Card className="p-6">
            {renderSection()}

            {/* Save Button */}
            <div className="mt-8 pt-6 border-t border-slate-800 flex justify-end">
              <Button
                variant="primary"
                onClick={handleSave}
                isLoading={saveStatus === 'saving'}
                icon={saveStatus === 'saved' ? CheckCircle : Save}
              >
                {saveStatus === 'saved' ? 'Saved!' : 'Save Changes'}
              </Button>
            </div>
          </Card>
        </div>
      </div>
    </Layout>
  );
}
