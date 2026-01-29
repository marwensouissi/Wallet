import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Mail, Lock, User, AlertCircle } from 'lucide-react';
import { Input } from '../common/Input';
import { Button } from '../common/Button';
import { useAuth } from '../../hooks/useAuth';

export const RegisterForm = () => {
    const navigate = useNavigate();
    const { register, isLoading } = useAuth();
    const [error, setError] = useState('');

    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (formData.password.length < 8) {
            setError('Password must be at least 8 characters');
            return;
        }

        try {
            await register(formData.email, formData.password, formData.firstName, formData.lastName);
            navigate('/dashboard');
        } catch {
            setError('Registration failed. Please try again.');
        }
    };

    return (
        <div className="backdrop-blur-xl bg-white/10 border border-white/20 p-8 rounded-3xl shadow-2xl">
            {error && (
                <div className="bg-red-500/10 border border-red-500/20 rounded-lg p-3 mb-6 flex items-center gap-2 text-red-200 text-sm">
                    <AlertCircle className="w-4 h-4" />
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-5">
                <div className="grid grid-cols-2 gap-4">
                    <Input
                        label="First Name"
                        type="text"
                        placeholder="John"
                        icon={<User className="w-5 h-5" />}
                        value={formData.firstName}
                        onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                        required
                    />
                    <Input
                        label="Last Name"
                        type="text"
                        placeholder="Doe"
                        icon={<User className="w-5 h-5" />}
                        value={formData.lastName}
                        onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                        required
                    />
                </div>

                <Input
                    label="Email Address"
                    type="email"
                    placeholder="name@example.com"
                    icon={<Mail className="w-5 h-5" />}
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    required
                />

                <Input
                    label="Password"
                    type="password"
                    placeholder="••••••••"
                    icon={<Lock className="w-5 h-5" />}
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    required
                />

                <Input
                    label="Confirm Password"
                    type="password"
                    placeholder="••••••••"
                    icon={<Lock className="w-5 h-5" />}
                    value={formData.confirmPassword}
                    onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                    required
                />

                <div className="flex items-start gap-2 text-sm">
                    <input 
                        type="checkbox" 
                        required
                        className="mt-1 rounded border-gray-600 bg-gray-700 text-indigo-500 focus:ring-indigo-500" 
                    />
                    <span className="text-gray-400">
                        I agree to the{' '}
                        <button type="button" className="text-indigo-400 hover:text-indigo-300 transition-colors">
                            Terms of Service
                        </button>{' '}
                        and{' '}
                        <button type="button" className="text-indigo-400 hover:text-indigo-300 transition-colors">
                            Privacy Policy
                        </button>
                    </span>
                </div>

                <Button
                    type="submit"
                    isLoading={isLoading}
                    className="w-full"
                >
                    Create Account
                </Button>
            </form>

            <p className="text-center mt-8 text-gray-400 text-sm">
                Already have an account?{' '}
                <button onClick={() => navigate('/login')} className="text-indigo-400 hover:text-indigo-300 font-medium transition-colors">
                    Sign In
                </button>
            </p>
        </div>
    );
};
