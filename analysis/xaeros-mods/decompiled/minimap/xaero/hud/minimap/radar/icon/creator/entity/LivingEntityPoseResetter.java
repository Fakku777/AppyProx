/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10034
 *  net.minecraft.class_10042
 */
package xaero.hud.minimap.radar.icon.creator.entity;

import net.minecraft.class_10034;
import net.minecraft.class_10042;

public class LivingEntityPoseResetter {
    public void resetValues(class_10042 livingEntityRenderState) {
        livingEntityRenderState.field_53451 = 0.0f;
        livingEntityRenderState.field_53447 = 0.0f;
        livingEntityRenderState.field_53448 = 0.0f;
        livingEntityRenderState.field_53446 = 0.0f;
        livingEntityRenderState.field_53328 = 10.0f;
        if (livingEntityRenderState instanceof class_10034) {
            class_10034 humanoidRenderState = (class_10034)livingEntityRenderState;
            humanoidRenderState.field_53404 = 0.0f;
            humanoidRenderState.field_53403 = 0.0f;
            humanoidRenderState.field_53410 = false;
            humanoidRenderState.field_53411 = false;
            humanoidRenderState.field_53412 = false;
            humanoidRenderState.field_53407 = 0;
        }
    }
}

