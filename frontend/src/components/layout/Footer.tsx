import React from 'react';
import { Logo } from '../common/Logo';

export const Footer = () => {
    return (
        <footer className="py-12 border-t border-white/10 bg-black">
            <div className="max-w-7xl mx-auto px-6">
                <div className="flex flex-col md:flex-row justify-between items-center gap-6">
                    <div className="flex flex-col items-center md:items-start gap-4">
                        <Logo />
                        <p className="text-gray-500 text-sm max-w-xs text-center md:text-left">
                            The next generation of digital payments. Secure, fast, and beautiful.
                        </p>
                    </div>

                    <div className="flex gap-8 text-sm text-gray-400">
                        <a href="#" className="hover:text-white transition-colors">Privacy</a>
                        <a href="#" className="hover:text-white transition-colors">Terms</a>
                        <a href="#" className="hover:text-white transition-colors">Contact</a>
                    </div>
                </div>

                <div className="mt-8 pt-8 border-t border-white/5 text-center text-gray-600 text-sm">
                    <p>&copy; {new Date().getFullYear()} Digital Wallet. All rights reserved.</p>
                </div>
            </div>
        </footer>
    );
};
