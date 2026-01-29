import type { HTMLAttributes } from 'react';
import { cn } from './Button';

interface GlassCardProps extends HTMLAttributes<HTMLDivElement> {
    hoverEffect?: boolean;
}

export const GlassCard = ({ children, className, hoverEffect = false, ...props }: GlassCardProps) => {
    return (
        <div
            className={cn(
                "backdrop-blur-xl bg-white/5 border border-white/10 rounded-2xl shadow-xl",
                hoverEffect && "hover:bg-white/10 hover:border-white/20 hover:scale-[1.02] transition-all duration-300",
                className
            )}
            {...props}
        >
            {children}
        </div>
    );
};
