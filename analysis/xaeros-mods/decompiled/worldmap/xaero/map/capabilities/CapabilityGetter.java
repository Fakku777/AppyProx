/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3218
 */
package xaero.map.capabilities;

import net.minecraft.class_3218;
import xaero.map.capabilities.ServerWorldCapabilities;
import xaero.map.core.IWorldMapServerLevel;

public class CapabilityGetter {
    public static ServerWorldCapabilities getServerWorldCapabilities(class_3218 level) {
        IWorldMapServerLevel serverLevel = (IWorldMapServerLevel)level;
        ServerWorldCapabilities result = serverLevel.getXaero_wm_capabilities();
        if (result == null) {
            result = new ServerWorldCapabilities();
            serverLevel.setXaero_wm_capabilities(result);
        }
        return result;
    }
}

