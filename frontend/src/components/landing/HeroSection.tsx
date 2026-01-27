import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowRight, PlayCircle } from 'lucide-react';
import { Button } from '../common/Button';
import { GlassCard } from '../common/GlassCard';
import HeroImage from '../../assets/hero-dashboard.png'; // Placeholder path, we will map this

const HeroSection = () => {
    const navigate = useNavigate();

    return (
        <section className="relative pt-32 pb-20 px-6 min-h-screen flex items-center">
            {/* Background gradients */}
            <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-indigo-900/40 via-black to-black pointer-events-none" />

            <div className="max-w-7xl mx-auto w-full grid lg:grid-cols-2 gap-12 items-center relative z-10">

                {/* Left Content */}
                <div className="text-left animate-fade-in-up">
                    <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/5 border border-white/10 text-sm text-indigo-300 mb-8 backdrop-blur-md">
                        <span className="relative flex h-2 w-2">
                            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-indigo-400 opacity-75"></span>
                            <span className="relative inline-flex rounded-full h-2 w-2 bg-indigo-500"></span>
                        </span>
                        Now live: Global Transactions
                    </div>

                    <h1 className="text-5xl md:text-7xl font-bold tracking-tight mb-8 leading-tight">
                        The Future of <br />
                        <span className="bg-clip-text text-transparent bg-gradient-to-r from-indigo-400 via-purple-400 to-pink-400 animate-gradient">
                            Digital Finance
                        </span>
                    </h1>

                    <p className="text-xl text-gray-400 max-w-xl mb-10 leading-relaxed">
                        Experience banking without borders. Instant transfers, real-time analytics,
                        and military-grade security in one beautiful interface.
                    </p>

                    <div className="flex flex-col sm:flex-row gap-4">
                        <Button
                            size="lg"
                            onClick={() => navigate('/login')}
                            className="group"
                        >
                            Explore Dashboard
                            <ArrowRight className="w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform" />
                        </Button>
                        <Button
                            size="lg"
                            variant="outline"
                            className="group"
                        >
                            <PlayCircle className="w-4 h-4 mr-2" />
                            Watch Demo
                        </Button>
                    </div>

                    <div className="mt-12 flex items-center gap-6 text-gray-500 text-sm">
                        <div className="flex -space-x-3">
                            {[1, 2, 3, 4].map((i) => (
                                <div key={i} className="w-10 h-10 rounded-full border-2 border-black bg-gray-600 flex items-center justify-center text-xs text-white">
                                    User
                                </div>
                            ))}
                        </div>
                        <p>Trusted by 10,000+ users worldwide</p>
                    </div>
                </div>

                {/* Right Content - Hero Image */}
                <div className="relative animate-fade-in-up delay-200 hidden lg:block">
                    {/* Abstract blurs behind image */}
                    <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[120%] h-[120%] bg-indigo-500/10 rounded-full blur-[100px]" />

                    <GlassCard className="transform rotate-y-12 rotate-x-6 hover:rotate-0 transition-transform duration-700 perspective-1000 p-2 bg-white/5 border-white/20">
                        <div className="rounded-xl overflow-hidden shadow-2xl relative bg-black/50 aspect-[4/3]">
                            {/* 
                  NOTE: In a real scenario, we would use the generated image here. 
                  For now, we use a placeholder styling since we can't easily reference the absolute path in the browser without setup.
                  Ideally, we could move the generated image to public/assets. 
                */}
                            <img
                                src="/hero_dashboard.png"
                                alt="App Dashboard"
                                className="w-full h-full object-cover"
                                onError={(e) => {
                                    (e.target as HTMLImageElement).src = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?q=80&w=2670&auto=format&fit=crop";
                                }}
                            />

                            {/* Overlay UI Mockups (CSS only fallback if image fails) */}
                            <div className="absolute inset-0 bg-gradient-to-t from-black/80 to-transparent flex items-end p-8">
                                <div className="w-full">
                                    <div className="flex justify-between items-end mb-2">
                                        <div>
                                            <p className="text-gray-400 text-sm">Total Balance</p>
                                            <h3 className="text-3xl font-bold text-white">$124,500.00</h3>
                                        </div>
                                        <div className="text-green-400 text-sm font-medium bg-green-400/10 px-2 py-1 rounded">
                                            +2.4%
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </GlassCard>

                    {/* Floating Cards */}
                    <GlassCard className="absolute -bottom-10 -left-10 p-4 w-64 animate-float">
                        <div className="flex items-center justify-between mb-3">
                            <span className="text-xs text-gray-400">Income</span>
                            <ArrowRight className="w-3 h-3 text-green-400 -rotate-45" />
                        </div>
                        <div className="h-2 w-full bg-gray-700 rounded-full overflow-hidden">
                            <div className="h-full w-[70%] bg-gradient-to-r from-green-400 to-emerald-500" />
                        </div>
                    </GlassCard>
                </div>
            </div>
        </section>
    );
};

export default HeroSection;
