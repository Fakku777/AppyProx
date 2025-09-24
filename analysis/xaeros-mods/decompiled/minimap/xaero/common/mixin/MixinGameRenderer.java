/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11228
 *  net.minecraft.class_757
 *  net.minecraft.class_758
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package xaero.common.mixin;

import net.minecraft.class_11228;
import net.minecraft.class_757;
import net.minecraft.class_758;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.IGameRenderer;

@Mixin(value={class_757.class})
public class MixinGameRenderer
implements IGameRenderer {
    @Shadow
    private class_11228 field_59965;
    @Shadow
    private class_758 field_60793;

    @Override
    public class_11228 xaero_mm_getGuiRenderer() {
        return this.field_59965;
    }

    @Override
    public class_758 xaero_mm_getFogRenderer() {
        return this.field_60793;
    }
}

