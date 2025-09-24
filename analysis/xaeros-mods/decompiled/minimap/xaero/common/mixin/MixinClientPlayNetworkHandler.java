/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2626
 *  net.minecraft.class_2637
 *  net.minecraft.class_2666
 *  net.minecraft.class_2672
 *  net.minecraft.class_2676
 *  net.minecraft.class_2678
 *  net.minecraft.class_2759
 *  net.minecraft.class_437
 *  net.minecraft.class_634
 *  net.minecraft.class_6603
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.common.mixin;

import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2666;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_2678;
import net.minecraft.class_2759;
import net.minecraft.class_437;
import net.minecraft.class_634;
import net.minecraft.class_6603;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={class_634.class})
public class MixinClientPlayNetworkHandler
implements IXaeroMinimapClientPlayNetHandler {
    XaeroMinimapSession xaero_minimapSession;

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleChunkBlocksUpdate"})
    public void onOnChunkDeltaUpdate(class_2637 packet, CallbackInfo info) {
        XaeroMinimapCore.onMultiBlockChange(packet);
    }

    @Inject(at={@At(value="HEAD")}, method={"updateLevelChunk"})
    public void onOnChunkData(int x, int z, class_6603 packet, CallbackInfo info) {
        XaeroMinimapCore.onChunkData(x, z, packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleLevelChunkWithLight"})
    public void onHandleLevelChunkWithLight(class_2672 packet, CallbackInfo info) {
        XaeroMinimapCore.onHandleLevelChunkWithLight(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleLightUpdatePacket"})
    public void onHandleLightUpdatePacket(class_2676 packet, CallbackInfo info) {
        XaeroMinimapCore.onHandleLightUpdatePacket(packet);
    }

    @Inject(at={@At(value="HEAD")}, method={"queueLightRemoval"})
    public void onQueueLightRemoval(class_2666 packet, CallbackInfo info) {
        XaeroMinimapCore.onQueueLightRemoval(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleBlockUpdate"})
    public void onOnBlockUpdate(class_2626 packet, CallbackInfo info) {
        XaeroMinimapCore.onBlockChange(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleSetSpawn"})
    public void onOnPlayerSpawnPosition(class_2759 packet, CallbackInfo info) {
        XaeroMinimapCore.onSpawn(packet);
    }

    @Override
    public XaeroMinimapSession getXaero_minimapSession() {
        return this.xaero_minimapSession;
    }

    @Override
    public void setXaero_minimapSession(XaeroMinimapSession session) {
        this.xaero_minimapSession = session;
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleLogin"})
    public void onOnGameJoin(class_2678 packet, CallbackInfo info) {
        XaeroMinimapCore.onPlayNetHandler((class_634)this, packet);
    }

    @Inject(at={@At(value="HEAD")}, method={"close"})
    public void onClose(CallbackInfo info) {
        XaeroMinimapCore.onPlayNetHandlerCleanup((class_634)this);
    }

    @Inject(at={@At(value="HEAD")}, method={"sendCommand(Ljava/lang/String;)V"}, cancellable=true)
    public void onSendCommand(String string_1, CallbackInfo info) {
        if (XaeroMinimapCore.onLocalPlayerCommand(string_1)) {
            info.cancel();
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"sendUnattendedCommand(Ljava/lang/String;Lnet/minecraft/client/gui/screens/Screen;)V"}, cancellable=true)
    public void onSendUnattendedCommand(String string_1, class_437 screen, CallbackInfo info) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (HudMod.INSTANCE.getEvents().handleClientSendChatEvent(string_1)) {
            info.cancel();
        }
    }
}

