/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking$PlayPayloadHandler
 *  net.minecraft.class_2378
 *  net.minecraft.class_2960
 *  net.minecraft.class_7923
 *  net.minecraft.class_9139
 */
package xaero.common;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import net.minecraft.class_9139;
import xaero.common.IXaeroMinimap;
import xaero.common.PlatformContextLoaderCommon;
import xaero.common.effect.EffectsRegister;
import xaero.common.events.CommonEventsFabric;
import xaero.common.message.MinimapMessageHandlerFabric;
import xaero.common.message.payload.MinimapMessagePayload;
import xaero.common.message.payload.MinimapMessagePayloadCodec;
import xaero.common.message.server.MinimapPayloadServerHandler;

public class PlatformContextLoaderCommonFabric
extends PlatformContextLoaderCommon {
    @Override
    public void setup(IXaeroMinimap modMain) {
        MinimapMessagePayloadCodec minimapMessagePayloadCodec = new MinimapMessagePayloadCodec((MinimapMessageHandlerFabric)modMain.getMessageHandler());
        PayloadTypeRegistry.playS2C().register(MinimapMessagePayload.TYPE, (class_9139)minimapMessagePayloadCodec);
        PayloadTypeRegistry.playC2S().register(MinimapMessagePayload.TYPE, (class_9139)minimapMessagePayloadCodec);
        ServerPlayNetworking.registerGlobalReceiver(MinimapMessagePayload.TYPE, (ServerPlayNetworking.PlayPayloadHandler)new MinimapPayloadServerHandler());
        ((CommonEventsFabric)modMain.getCommonEvents()).register();
        if (modMain.getCommonConfig().registerStatusEffects) {
            new EffectsRegister().registerEffects(effect -> class_2378.method_47985((class_2378)class_7923.field_41174, (class_2960)effect.getRegistryName(), (Object)effect));
        }
    }
}

