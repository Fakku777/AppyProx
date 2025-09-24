/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3222
 *  org.spongepowered.asm.mixin.Mixin
 */
package xaero.map.mixin;

import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import xaero.map.server.player.IServerPlayer;
import xaero.map.server.player.ServerPlayerData;

@Mixin(value={class_3222.class})
public class MixinServerPlayer
implements IServerPlayer {
    private ServerPlayerData xaeroWorldMapPlayerData;

    @Override
    public ServerPlayerData getXaeroWorldMapPlayerData() {
        return this.xaeroWorldMapPlayerData;
    }

    @Override
    public void setXaeroWorldMapPlayerData(ServerPlayerData data) {
        this.xaeroWorldMapPlayerData = data;
    }
}

