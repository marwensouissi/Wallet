import React from 'react';
import { AuthLayout } from '../components/layout/AuthLayout';
import { LoginForm } from '../components/auth/LoginForm';

const LoginPage = () => {
    return (
        <AuthLayout
            title="Welcome Back"
            subtitle="Enter your credentials to access your wallet"
        >
            <LoginForm />
        </AuthLayout>
    );
};

export default LoginPage;
