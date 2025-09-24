/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  xaero.map.WorldMap
 */
package xaero.common.server.mods;

import xaero.map.WorldMap;

public class SupportWorldMapServer {
    private final int compatibilityVersion = WorldMap.MINIMAP_COMPATIBILITY_VERSION;

    public boolean supportsTrackedPlayers() {
        return this.compatibilityVersion >= 22;
    }
}

