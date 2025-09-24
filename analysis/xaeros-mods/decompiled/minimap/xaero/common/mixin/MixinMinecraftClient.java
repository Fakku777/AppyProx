/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package xaero.common.mixin;

import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.IXaeroMinimapMinecraftClient;

@Mixin(value={class_310.class})
public class MixinMinecraftClient
implements IXaeroMinimapMinecraftClient {
    @Shadow
    private static int field_1738;

    @Override
    public int getXaeroMinimap_fps() {
        return field_1738;
    }
}

