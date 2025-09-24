/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package xaero.map.mixin;

import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.map.core.IWorldMapMinecraftClient;

@Mixin(value={class_310.class})
public class MixinMinecraftClient
implements IWorldMapMinecraftClient {
    @Shadow
    private static int field_1738;

    @Override
    public int getXaeroWorldMap_fps() {
        return field_1738;
    }
}

