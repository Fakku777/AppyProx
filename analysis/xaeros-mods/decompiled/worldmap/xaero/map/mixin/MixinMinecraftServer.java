/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  org.spongepowered.asm.mixin.Mixin
 */
package xaero.map.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import xaero.map.server.IMinecraftServer;
import xaero.map.server.MinecraftServerData;

@Mixin(value={MinecraftServer.class})
public class MixinMinecraftServer
implements IMinecraftServer {
    private MinecraftServerData xaeroWorldMapServerData;

    @Override
    public MinecraftServerData getXaeroWorldMapServerData() {
        return this.xaeroWorldMapServerData;
    }

    @Override
    public void setXaeroWorldMapServerData(MinecraftServerData data) {
        this.xaeroWorldMapServerData = data;
    }
}

