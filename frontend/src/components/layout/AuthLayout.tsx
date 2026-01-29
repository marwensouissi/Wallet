import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { Logo } from '../common/Logo';

interface AuthLayoutProps {
    children: ReactNode;
    title: string;
    subtitle: string;
}

export const AuthLayout = ({ children, title, subtitle }: AuthLayoutProps) => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-black flex items-center justify-center p-6 relative overflow-hidden">
            {/* Background Elements */}
            <div className="absolute top-[-20%] left-[-10%] w-[60%] h-[60%] bg-indigo-600/20 rounded-full blur-[120px] animate-pulse" />
            <div className="absolute bottom-[-20%] right-[-10%] w-[60%] h-[60%] bg-purple-600/20 rounded-full blur-[120px] animate-pulse delay-1000" />

            {/* Grid Pattern */}
            <div className="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-20" />

            <div className="w-full max-w-md relative z-10 animate-fade-in-up">
                {/* Header content */}
                <div className="text-center mb-8 cursor-pointer" onClick={() => navigate('/')}>
                    <div className="inline-block mb-6 hover:scale-105 transition-transform duration-300">
                        <Logo />
                    </div>
                    <h2 className="text-3xl font-bold text-white mb-2 tracking-tight">{title}</h2>
                    <p className="text-gray-400">{subtitle}</p>
                </div>

                {/* content */}
                {children}
            </div>
        </div>
    );
};
