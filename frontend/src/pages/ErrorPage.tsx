// ============================================
// Error Page - Generic Error Handler
// ============================================

import { useRouteError, isRouteErrorResponse, Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';
import { Home, RefreshCw, AlertTriangle } from 'lucide-react';

export default function ErrorPage() {
  const error = useRouteError();
  
  let title = 'Something went wrong';
  let message = 'An unexpected error occurred. Please try again.';
  let statusCode = 500;

  if (isRouteErrorResponse(error)) {
    statusCode = error.status;
    if (error.status === 404) {
      title = 'Page not found';
      message = "The page you're looking for doesn't exist.";
    } else if (error.status === 401) {
      title = 'Unauthorized';
      message = 'You need to be logged in to access this page.';
    } else if (error.status === 403) {
      title = 'Access denied';
      message = "You don't have permission to view this page.";
    } else if (error.status === 500) {
      title = 'Server error';
      message = 'Our servers are having trouble. Please try again later.';
    }
  }

  return (
    <div className="min-h-screen bg-bg-dark flex items-center justify-center px-4">
      <div className="text-center max-w-md">
        {/* Error Icon */}
        <div className="mb-8 flex justify-center">
          <div className="w-24 h-24 rounded-full bg-rose-500/20 flex items-center justify-center">
            <AlertTriangle className="w-12 h-12 text-rose-400" />
          </div>
        </div>

        {/* Status Code */}
        <div className="text-6xl font-bold text-slate-700 mb-4">
          {statusCode}
        </div>

        {/* Message */}
        <h1 className="text-2xl font-bold text-white mb-4">
          {title}
        </h1>
        <p className="text-slate-400 mb-8">
          {message}
        </p>

        {/* Actions */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Button
            variant="primary"
            icon={RefreshCw}
            onClick={() => window.location.reload()}
          >
            Try Again
          </Button>
          <Button
            variant="outline"
            icon={Home}
            onClick={() => window.location.href = '/dashboard'}
          >
            Go to Dashboard
          </Button>
        </div>

        {/* Help Link */}
        <div className="mt-8 pt-8 border-t border-slate-800">
          <p className="text-sm text-slate-500">
            If this problem persists,{' '}
            <Link to="/help" className="text-indigo-400 hover:text-indigo-300 transition-colors">
              contact our support team
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
