import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Mail, Lock, AlertCircle } from 'lucide-react';
import { Input } from '../common/Input';
import { Button } from '../common/Button';
import { useAuth } from '../../hooks/useAuth';

export const LoginForm = () => {
    const navigate = useNavigate();
    const { login, isLoading } = useAuth();
    const [error, setError] = useState('');

    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        try {
            await login(formData.email, formData.password);
            navigate('/dashboard');
        } catch (err) {
            setError('Invalid email or password');
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

            <form onSubmit={handleSubmit} className="space-y-6">
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

                <div className="flex items-center justify-between text-sm">
                    <label className="flex items-center gap-2 text-gray-400 cursor-pointer hover:text-gray-300">
                        <input type="checkbox" className="rounded border-gray-600 bg-gray-700 text-indigo-500 focus:ring-indigo-500" />
                        Remember me
                    </label>
                    <button type="button" className="text-indigo-400 hover:text-indigo-300 transition-colors">
                        Forgot password?
                    </button>
                </div>

                <Button
                    type="submit"
                    isLoading={isLoading}
                    className="w-full"
                >
                    Sign In
                </Button>
            </form>

            <p className="text-center mt-8 text-gray-400 text-sm">
                Don't have an account?{' '}
                <button onClick={() => navigate('/register')} className="text-indigo-400 hover:text-indigo-300 font-medium transition-colors">
                    Create Account
                </button>
            </p>
        </div>
    );
};
