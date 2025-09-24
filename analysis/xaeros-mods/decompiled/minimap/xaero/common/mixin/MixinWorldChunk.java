/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2818
 *  org.spongepowered.asm.mixin.Mixin
 */
package xaero.common.mixin;

import net.minecraft.class_2818;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={class_2818.class})
public class MixinWorldChunk {
    public boolean xaero_chunkClean = false;
}

