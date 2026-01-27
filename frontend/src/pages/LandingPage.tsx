import React from 'react';
import { Navbar } from '../components/layout/Navbar';
import { Footer } from '../components/layout/Footer';
import HeroSection from '../components/landing/HeroSection';
import FeaturesSection from '../components/landing/FeaturesSection';

const LandingPage = () => {
    return (
        <div className="min-h-screen bg-black text-white selection:bg-indigo-500 selection:text-white overflow-x-hidden">
            <Navbar />
            <HeroSection />
            <FeaturesSection />
            <Footer />
        </div>
    );
};

export default LandingPage;
