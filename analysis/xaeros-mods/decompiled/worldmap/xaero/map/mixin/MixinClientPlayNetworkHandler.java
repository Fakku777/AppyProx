/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2626
 *  net.minecraft.class_2637
 *  net.minecraft.class_2666
 *  net.minecraft.class_2672
 *  net.minecraft.class_2676
 *  net.minecraft.class_2759
 *  net.minecraft.class_634
 *  net.minecraft.class_6603
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.map.mixin;

import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2666;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_2759;
import net.minecraft.class_634;
import net.minecraft.class_6603;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.WorldMapSession;
import xaero.map.core.IWorldMapClientPlayNetHandler;
import xaero.map.core.XaeroWorldMapCore;

@Mixin(value={class_634.class})
public class MixinClientPlayNetworkHandler
implements IWorldMapClientPlayNetHandler {
    WorldMapSession xaero_worldmapSession;

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleChunkBlocksUpdate"})
    public void onOnChunkDeltaUpdate(class_2637 packet, CallbackInfo info) {
        XaeroWorldMapCore.onMultiBlockChange(packet);
    }

    @Inject(at={@At(value="HEAD")}, method={"updateLevelChunk"})
    public void onOnChunkData(int x, int z, class_6603 packet, CallbackInfo info) {
        XaeroWorldMapCore.onChunkData(x, z, packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleLevelChunkWithLight"})
    public void onHandleLevelChunkWithLight(class_2672 packet, CallbackInfo info) {
        XaeroWorldMapCore.onHandleLevelChunkWithLight(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleLightUpdatePacket"})
    public void onHandleLightUpdatePacket(class_2676 packet, CallbackInfo info) {
        XaeroWorldMapCore.onHandleLightUpdatePacket(packet);
    }

    @Inject(at={@At(value="HEAD")}, method={"queueLightRemoval"})
    public void onQueueLightRemoval(class_2666 packet, CallbackInfo info) {
        XaeroWorldMapCore.onQueueLightRemoval(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleBlockUpdate"})
    public void onOnBlockUpdate(class_2626 packet, CallbackInfo info) {
        XaeroWorldMapCore.onBlockChange(packet);
    }

    @Inject(at={@At(value="INVOKE", shift=At.Shift.AFTER, target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V")}, method={"handleSetSpawn"})
    public void onOnPlayerSpawnPosition(class_2759 packet, CallbackInfo info) {
        XaeroWorldMapCore.handlePlayerSetSpawnPacket(packet);
    }

    @Override
    public WorldMapSession getXaero_worldmapSession() {
        return this.xaero_worldmapSession;
    }

    @Override
    public void setXaero_worldmapSession(WorldMapSession session) {
        this.xaero_worldmapSession = session;
    }

    @Inject(at={@At(value="HEAD")}, method={"close"})
    public void onCleanup(CallbackInfo info) {
        XaeroWorldMapCore.onPlayNetHandlerCleanup((class_634)this);
    }
}

