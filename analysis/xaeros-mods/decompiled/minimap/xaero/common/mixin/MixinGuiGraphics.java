/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11246
 *  net.minecraft.class_332
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package xaero.common.mixin;

import net.minecraft.class_11246;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.IGuiGraphics;

@Mixin(value={class_332.class})
public class MixinGuiGraphics
implements IGuiGraphics {
    @Shadow
    private class_11246 field_59826;

    @Override
    public class_11246 xaero_mm_getGuiRenderState() {
        return this.field_59826;
    }
}

