// ============================================
// Not Found Page - 404 Error
// ============================================

import { Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';
import { Home, ArrowLeft } from 'lucide-react';

export default function NotFoundPage() {
  return (
    <div className="min-h-screen bg-bg-dark flex items-center justify-center px-4">
      <div className="text-center max-w-md">
        {/* 404 Illustration */}
        <div className="mb-8">
          <div className="text-9xl font-bold bg-linear-to-r from-indigo-500 to-purple-600 bg-clip-text text-transparent">
            404
          </div>
        </div>

        {/* Message */}
        <h1 className="text-2xl font-bold text-white mb-4">
          Page Not Found
        </h1>
        <p className="text-slate-400 mb-8">
          The page you're looking for doesn't exist or has been moved. 
          Let's get you back on track.
        </p>

        {/* Actions */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Button
            variant="primary"
            icon={Home}
            onClick={() => window.location.href = '/dashboard'}
          >
            Go to Dashboard
          </Button>
          <Button
            variant="outline"
            icon={ArrowLeft}
            onClick={() => window.history.back()}
          >
            Go Back
          </Button>
        </div>

        {/* Help Link */}
        <div className="mt-8 pt-8 border-t border-slate-800">
          <p className="text-sm text-slate-500">
            Need help?{' '}
            <Link to="/help" className="text-indigo-400 hover:text-indigo-300 transition-colors">
              Visit our Help Center
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
