/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking$PlayPayloadHandler
 */
package xaero.map;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xaero.map.WorldMapClientOnly;
import xaero.map.message.client.WorldMapPayloadClientHandler;
import xaero.map.message.payload.WorldMapMessagePayload;

public class WorldMapClientOnlyFabric
extends WorldMapClientOnly {
    @Override
    public void preInit(String modId) {
        super.preInit(modId);
        ClientPlayNetworking.registerGlobalReceiver(WorldMapMessagePayload.TYPE, (ClientPlayNetworking.PlayPayloadHandler)new WorldMapPayloadClientHandler());
    }
}

