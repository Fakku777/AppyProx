/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3222
 *  xaero.common.server.XaeroMinimapServer
 *  xaero.common.server.player.ServerPlayerData
 */
package xaero.map.server.mods;

import net.minecraft.class_3222;
import xaero.common.server.XaeroMinimapServer;
import xaero.common.server.player.ServerPlayerData;

public class SupportMinimapServer {
    private final int compatibilityVersion;

    public SupportMinimapServer() {
        int compatibilityVersion = 0;
        try {
            compatibilityVersion = XaeroMinimapServer.SERVER_COMPATIBILITY;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.compatibilityVersion = compatibilityVersion;
    }

    public boolean supportsTrackedPlayers() {
        return this.compatibilityVersion >= 1;
    }

    public boolean playerSupportsTrackedPlayers(class_3222 player) {
        ServerPlayerData playerData = ServerPlayerData.get((class_3222)player);
        return playerData.hasMod();
    }
}

