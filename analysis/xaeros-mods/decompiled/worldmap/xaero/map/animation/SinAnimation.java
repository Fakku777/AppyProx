/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.animation;

import xaero.map.animation.Animation;

public class SinAnimation
extends Animation {
    public SinAnimation(double from, double to, long time) {
        super(from, to, time);
    }

    @Override
    public double getCurrent() {
        double passed = Math.min(1.0, (double)(System.currentTimeMillis() - this.start) / (double)this.time);
        double angle = 1.5707963267948966 * passed;
        return this.from + this.off * Math.sin(angle);
    }
}

