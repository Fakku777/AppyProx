/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3222
 *  org.spongepowered.asm.mixin.Mixin
 */
package xaero.common.mixin;

import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import xaero.common.server.player.IServerPlayer;
import xaero.common.server.player.ServerPlayerData;

@Mixin(value={class_3222.class})
public class MixinServerPlayer
implements IServerPlayer {
    private ServerPlayerData xaeroMinimapPlayerData;

    @Override
    public ServerPlayerData getXaeroMinimapPlayerData() {
        return this.xaeroMinimapPlayerData;
    }

    @Override
    public void setXaeroMinimapPlayerData(ServerPlayerData data) {
        this.xaeroMinimapPlayerData = data;
    }
}

