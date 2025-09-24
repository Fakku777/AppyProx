/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3218
 *  org.spongepowered.asm.mixin.Mixin
 */
package xaero.map.mixin;

import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import xaero.map.capabilities.ServerWorldCapabilities;
import xaero.map.core.IWorldMapServerLevel;

@Mixin(value={class_3218.class})
public class MixinServerWorld
implements IWorldMapServerLevel {
    public ServerWorldCapabilities xaero_wm_capabilities;

    @Override
    public ServerWorldCapabilities getXaero_wm_capabilities() {
        return this.xaero_wm_capabilities;
    }

    @Override
    public void setXaero_wm_capabilities(ServerWorldCapabilities capabilities) {
        this.xaero_wm_capabilities = capabilities;
    }
}

