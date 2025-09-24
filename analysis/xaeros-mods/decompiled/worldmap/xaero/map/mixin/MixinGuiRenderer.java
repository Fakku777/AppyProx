/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11228
 *  net.minecraft.class_11278
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package xaero.map.mixin;

import net.minecraft.class_11228;
import net.minecraft.class_11278;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.map.core.IGuiRenderer;

@Mixin(value={class_11228.class})
public class MixinGuiRenderer
implements IGuiRenderer {
    @Shadow
    private class_11278 field_60040;

    @Override
    public class_11278 xaero_wm_getGuiProjectionMatrixBuffer() {
        return this.field_60040;
    }
}

