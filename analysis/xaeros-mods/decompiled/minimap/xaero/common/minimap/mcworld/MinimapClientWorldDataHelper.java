/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 */
package xaero.common.minimap.mcworld;

import net.minecraft.class_310;
import net.minecraft.class_638;
import xaero.common.minimap.mcworld.IXaeroMinimapClientWorld;
import xaero.common.minimap.mcworld.MinimapClientWorldData;

public class MinimapClientWorldDataHelper {
    public static MinimapClientWorldData getCurrentWorldData() {
        return MinimapClientWorldDataHelper.getWorldData(class_310.method_1551().field_1687);
    }

    public static MinimapClientWorldData getWorldData(class_638 clientWorld) {
        IXaeroMinimapClientWorld inter = (IXaeroMinimapClientWorld)clientWorld;
        MinimapClientWorldData minimapWorldData = inter.getXaero_minimapData();
        if (minimapWorldData == null) {
            minimapWorldData = new MinimapClientWorldData(clientWorld);
            inter.setXaero_minimapData(minimapWorldData);
        }
        return minimapWorldData;
    }
}

