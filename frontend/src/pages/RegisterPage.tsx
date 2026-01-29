import { AuthLayout } from '../components/layout/AuthLayout';
import { RegisterForm } from '../components/auth/RegisterForm';

const RegisterPage = () => {
    return (
        <AuthLayout
            title="Create Account"
            subtitle="Join us and start managing your finances"
        >
            <RegisterForm />
        </AuthLayout>
    );
};

export default RegisterPage;
