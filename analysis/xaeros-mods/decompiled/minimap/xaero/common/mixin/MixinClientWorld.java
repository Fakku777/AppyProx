/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_638
 *  org.spongepowered.asm.mixin.Mixin
 */
package xaero.common.mixin;

import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import xaero.common.minimap.mcworld.IXaeroMinimapClientWorld;
import xaero.common.minimap.mcworld.MinimapClientWorldData;

@Mixin(value={class_638.class})
public class MixinClientWorld
implements IXaeroMinimapClientWorld {
    private MinimapClientWorldData xaero_minimapData;

    @Override
    public MinimapClientWorldData getXaero_minimapData() {
        return this.xaero_minimapData;
    }

    @Override
    public void setXaero_minimapData(MinimapClientWorldData xaero_minimapData) {
        this.xaero_minimapData = xaero_minimapData;
    }
}

