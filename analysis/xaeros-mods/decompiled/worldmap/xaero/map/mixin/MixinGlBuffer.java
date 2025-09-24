/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10859
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package xaero.map.mixin;

import net.minecraft.class_10859;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.map.core.IWorldMapGlBuffer;

@Mixin(value={class_10859.class})
public class MixinGlBuffer
implements IWorldMapGlBuffer {
    @Shadow
    private int field_57842;

    @Override
    public int xaero_wm_getHandle() {
        return this.field_57842;
    }
}

