/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking$PlayPayloadHandler
 */
package xaero.common;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xaero.common.IXaeroMinimap;
import xaero.common.PlatformContextLoaderClientOnly;
import xaero.common.message.client.MinimapPayloadClientHandler;
import xaero.common.message.payload.MinimapMessagePayload;

public class PlatformContextLoaderClientOnlyFabric
extends PlatformContextLoaderClientOnly {
    @Override
    public void preInit(String modId, IXaeroMinimap modMain) {
        ClientPlayNetworking.registerGlobalReceiver(MinimapMessagePayload.TYPE, (ClientPlayNetworking.PlayPayloadHandler)new MinimapPayloadClientHandler());
        modMain.ensureControlsRegister();
        modMain.getControlsRegister().registerKeybindings(KeyBindingHelper::registerKeyBinding);
    }
}

