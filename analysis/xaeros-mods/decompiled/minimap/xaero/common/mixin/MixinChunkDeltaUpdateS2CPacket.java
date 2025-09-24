/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2637
 *  net.minecraft.class_4076
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package xaero.common.mixin;

import net.minecraft.class_2637;
import net.minecraft.class_4076;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.IXaeroMinimapSMultiBlockChangePacket;

@Mixin(value={class_2637.class})
public class MixinChunkDeltaUpdateS2CPacket
implements IXaeroMinimapSMultiBlockChangePacket {
    @Shadow
    class_4076 field_26345;

    @Override
    public class_4076 xaero_mm_getSectionPos() {
        return this.field_26345;
    }
}

