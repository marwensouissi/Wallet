import React from 'react';
import { Shield, Globe, Wallet, Zap, PieChart, Lock } from 'lucide-react';
import { GlassCard } from '../common/GlassCard';

const features = [
    {
        icon: <Shield className="w-6 h-6 text-indigo-400" />,
        title: "Bank-Grade Security",
        description: "Your assets are protected by state-of-the-art encryption and biometric verification."
    },
    {
        icon: <Globe className="w-6 h-6 text-purple-400" />,
        title: "Global Access",
        description: "Send money anywhere in the world instantly with zero hidden fees and real exchange rates."
    },
    {
        icon: <Wallet className="w-6 h-6 text-pink-400" />,
        title: "Smart Insights",
        description: "Visualize your spending habits with AI-powered analytics and detailed monthly reports."
    },
    {
        icon: <Zap className="w-6 h-6 text-yellow-400" />,
        title: "Instant Transfers",
        description: "Lightning fast transactions between users. Money arrives in seconds, not days."
    },
    {
        icon: <PieChart className="w-6 h-6 text-cyan-400" />,
        title: "Portfolio Management",
        description: "Track all your assets, stocks, and crypto in one unified dashboard."
    },
    {
        icon: <Lock className="w-6 h-6 text-emerald-400" />,
        title: "Privacy First",
        description: "We never sell your data. Your financial privacy is our top priority."
    }
];

const FeaturesSection = () => {
    return (
        <section className="py-24 px-6 bg-black relative">
            {/* Decorative */}
            <div className="absolute top-0 left-0 w-full h-px bg-gradient-to-r from-transparent via-white/10 to-transparent" />

            <div className="max-w-7xl mx-auto">
                <div className="text-center mb-16">
                    <h2 className="text-3xl md:text-5xl font-bold mb-4">
                        Everything you need <br />
                        <span className="text-gray-500">to manage your wealth</span>
                    </h2>
                    <p className="text-gray-400 max-w-2xl mx-auto">
                        Powerful features designed for both personal and business needs.
                    </p>
                </div>

                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {features.map((feature, idx) => (
                        <GlassCard key={idx} hoverEffect className="p-8 group bg-white/[0.02]">
                            <div className="w-12 h-12 rounded-xl bg-white/5 flex items-center justify-center mb-6 group-hover:scale-110 transition-transform duration-300 group-hover:bg-white/10">
                                {feature.icon}
                            </div>
                            <h3 className="text-xl font-semibold mb-3 text-white group-hover:text-indigo-300 transition-colors">
                                {feature.title}
                            </h3>
                            <p className="text-gray-400 leading-relaxed text-sm">
                                {feature.description}
                            </p>
                        </GlassCard>
                    ))}
                </div>
            </div>
        </section>
    );
};

export default FeaturesSection;
