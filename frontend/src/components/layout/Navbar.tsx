import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../common/Button';
import { Logo } from '../common/Logo';

export const Navbar = () => {
    const navigate = useNavigate();

    return (
        <nav className="fixed top-0 w-full z-50 backdrop-blur-lg border-b border-white/10 bg-black/50">
            <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
                <div className="cursor-pointer" onClick={() => navigate('/')}>
                    <Logo />
                </div>

                <div className="flex items-center gap-4">
                    <Button
                        variant="ghost"
                        onClick={() => navigate('/login')}
                    >
                        Sign In
                    </Button>
                    <Button
                        variant="primary"
                        onClick={() => navigate('/login')}
                        className="hidden sm:flex"
                    >
                        Get Started
                    </Button>
                </div>
            </div>
        </nav>
    );
};
