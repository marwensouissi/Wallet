// ============================================
// Help Page - FAQ & Support
// ============================================

import { useState } from 'react';
import { Layout } from '../components/layout/Layout';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { 
  HelpCircle,
  Search,
  ChevronDown,
  ChevronUp,
  MessageCircle,
  Mail,
  Phone,
  Book,
  FileText,
  Shield,
  CreditCard,
  ArrowRightLeft,
  Wallet,
  ExternalLink
} from 'lucide-react';

interface FAQItem {
  question: string;
  answer: string;
  category: string;
}

const FAQ_ITEMS: FAQItem[] = [
  {
    category: 'Getting Started',
    question: 'How do I create a new wallet?',
    answer: 'To create a new wallet, navigate to the Wallets page and click "Create Wallet". Select your preferred currency and confirm. Your new wallet will be instantly available for deposits and transfers.',
  },
  {
    category: 'Getting Started',
    question: 'What currencies are supported?',
    answer: 'We support major currencies including USD, EUR, GBP, JPY, CHF, CAD, AUD, and CNY. You can hold multiple wallets in different currencies and exchange between them.',
  },
  {
    category: 'Transactions',
    question: 'How do I transfer money between wallets?',
    answer: 'Go to the Transactions page and click "Transfer". Select your source and destination wallets, enter the amount, and confirm. For cross-currency transfers, the exchange rate will be shown before confirmation.',
  },
  {
    category: 'Transactions',
    question: 'How long do transfers take?',
    answer: 'Internal transfers between your own wallets are instant. Transfers to other users typically complete within a few seconds to a few minutes depending on network conditions.',
  },
  {
    category: 'Exchange',
    question: 'How does currency exchange work?',
    answer: 'Our currency exchange uses real-time market rates. Navigate to the Exchange page, select your source and destination currencies, enter the amount, and you\'ll see the converted amount before confirming.',
  },
  {
    category: 'Exchange',
    question: 'Are there fees for currency exchange?',
    answer: 'We offer competitive rates with a small spread built into the exchange rate. There are no hidden fees. The exact amount you\'ll receive is always shown before you confirm the exchange.',
  },
  {
    category: 'Scheduled Payments',
    question: 'How do I set up a recurring payment?',
    answer: 'Go to Scheduled Payments and click "New Schedule". Select the source and destination wallets, amount, and frequency (daily, weekly, monthly, etc.). You can also set an end date or maximum number of executions.',
  },
  {
    category: 'Scheduled Payments',
    question: 'Can I pause or cancel a scheduled payment?',
    answer: 'Yes, you can pause, resume, or cancel any scheduled payment from the Scheduled Payments page. Paused payments can be resumed at any time, while cancelled payments are permanently stopped.',
  },
  {
    category: 'Security',
    question: 'How is my account secured?',
    answer: 'We use bank-level encryption for all data transmission and storage. We offer two-factor authentication (2FA), session management, and regular security audits to protect your account.',
  },
  {
    category: 'Security',
    question: 'What should I do if I notice suspicious activity?',
    answer: 'Immediately change your password and enable 2FA if not already active. Contact our support team right away. We have 24/7 fraud monitoring to help protect your account.',
  },
];

const CATEGORIES = [
  { id: 'all', label: 'All Topics', icon: Book },
  { id: 'Getting Started', label: 'Getting Started', icon: Wallet },
  { id: 'Transactions', label: 'Transactions', icon: ArrowRightLeft },
  { id: 'Exchange', label: 'Exchange', icon: CreditCard },
  { id: 'Scheduled Payments', label: 'Scheduled Payments', icon: FileText },
  { id: 'Security', label: 'Security', icon: Shield },
];

export default function HelpPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [expandedItems, setExpandedItems] = useState<number[]>([]);

  const toggleItem = (index: number) => {
    setExpandedItems(prev =>
      prev.includes(index)
        ? prev.filter(i => i !== index)
        : [...prev, index]
    );
  };

  // Filter FAQs
  const filteredFAQs = FAQ_ITEMS.filter(item => {
    const matchesSearch = searchQuery === '' ||
      item.question.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.answer.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = selectedCategory === 'all' || item.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <Layout>
      {/* Header */}
      <header className="mb-8">
        <h1 className="text-2xl font-bold text-white mb-2">Help & Support</h1>
        <p className="text-slate-400">Find answers to common questions or contact our support team</p>
      </header>

      {/* Search */}
      <Card className="p-6 mb-8">
        <div className="relative max-w-xl mx-auto">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
          <Input
            placeholder="Search for help..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-12 text-lg py-4"
          />
        </div>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
        {/* Categories */}
        <div className="lg:col-span-1">
          <nav className="space-y-1 mb-8">
            {CATEGORIES.map(category => (
              <button
                key={category.id}
                onClick={() => setSelectedCategory(category.id)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors ${
                  selectedCategory === category.id
                    ? 'bg-indigo-500/10 text-indigo-400'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
                }`}
              >
                <category.icon className="w-5 h-5" />
                <span className="font-medium">{category.label}</span>
              </button>
            ))}
          </nav>

          {/* Contact Support */}
          <Card className="p-6">
            <h3 className="text-lg font-semibold text-white mb-4">Need More Help?</h3>
            <p className="text-sm text-slate-400 mb-4">
              Our support team is available 24/7 to assist you.
            </p>
            <div className="space-y-3">
              <Button variant="outline" size="sm" className="w-full justify-start">
                <MessageCircle className="w-4 h-4 mr-2" />
                Live Chat
              </Button>
              <Button variant="outline" size="sm" className="w-full justify-start">
                <Mail className="w-4 h-4 mr-2" />
                Email Support
              </Button>
              <Button variant="outline" size="sm" className="w-full justify-start">
                <Phone className="w-4 h-4 mr-2" />
                Call Us
              </Button>
            </div>
          </Card>
        </div>

        {/* FAQ List */}
        <div className="lg:col-span-3">
          <div className="space-y-4">
            {filteredFAQs.length === 0 ? (
              <Card className="p-8 text-center">
                <HelpCircle className="w-12 h-12 text-slate-600 mx-auto mb-4" />
                <h3 className="text-lg font-semibold text-white mb-2">No results found</h3>
                <p className="text-slate-400">
                  Try adjusting your search or browse by category.
                </p>
              </Card>
            ) : (
              filteredFAQs.map((item, index) => {
                const isExpanded = expandedItems.includes(index);
                return (
                  <Card key={index} className="overflow-hidden">
                    <button
                      onClick={() => toggleItem(index)}
                      className="w-full flex items-center justify-between p-6 text-left hover:bg-slate-800/30 transition-colors"
                    >
                      <div className="flex items-start gap-4">
                        <div className="w-8 h-8 rounded-full bg-indigo-500/20 flex items-center justify-center shrink-0 mt-0.5">
                          <HelpCircle className="w-4 h-4 text-indigo-400" />
                        </div>
                        <div>
                          <span className="text-xs text-indigo-400 font-medium mb-1 block">
                            {item.category}
                          </span>
                          <h3 className="text-white font-medium">{item.question}</h3>
                        </div>
                      </div>
                      {isExpanded ? (
                        <ChevronUp className="w-5 h-5 text-slate-400 shrink-0" />
                      ) : (
                        <ChevronDown className="w-5 h-5 text-slate-400 shrink-0" />
                      )}
                    </button>
                    {isExpanded && (
                      <div className="px-6 pb-6 pl-18">
                        <div className="pl-12 text-slate-400 leading-relaxed">
                          {item.answer}
                        </div>
                      </div>
                    )}
                  </Card>
                );
              })
            )}
          </div>

          {/* Additional Resources */}
          <div className="mt-8 grid grid-cols-1 md:grid-cols-2 gap-4">
            <Card className="p-6 hover:bg-slate-800/30 transition-colors cursor-pointer group">
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 rounded-xl bg-indigo-500/20 flex items-center justify-center">
                  <Book className="w-6 h-6 text-indigo-400" />
                </div>
                <div className="flex-1">
                  <h3 className="text-white font-semibold group-hover:text-indigo-400 transition-colors">
                    Documentation
                  </h3>
                  <p className="text-sm text-slate-400">Detailed guides and tutorials</p>
                </div>
                <ExternalLink className="w-5 h-5 text-slate-500 group-hover:text-indigo-400 transition-colors" />
              </div>
            </Card>

            <Card className="p-6 hover:bg-slate-800/30 transition-colors cursor-pointer group">
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 rounded-xl bg-emerald-500/20 flex items-center justify-center">
                  <FileText className="w-6 h-6 text-emerald-400" />
                </div>
                <div className="flex-1">
                  <h3 className="text-white font-semibold group-hover:text-emerald-400 transition-colors">
                    API Reference
                  </h3>
                  <p className="text-sm text-slate-400">For developers and integrations</p>
                </div>
                <ExternalLink className="w-5 h-5 text-slate-500 group-hover:text-emerald-400 transition-colors" />
              </div>
            </Card>
          </div>
        </div>
      </div>
    </Layout>
  );
}
