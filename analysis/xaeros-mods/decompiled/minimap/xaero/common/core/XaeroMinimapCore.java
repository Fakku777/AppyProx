/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_1937
 *  net.minecraft.class_2556$class_7602
 *  net.minecraft.class_2561
 *  net.minecraft.class_2626
 *  net.minecraft.class_2637
 *  net.minecraft.class_2666
 *  net.minecraft.class_2672
 *  net.minecraft.class_2676
 *  net.minecraft.class_2678
 *  net.minecraft.class_2759
 *  net.minecraft.class_2818
 *  net.minecraft.class_310
 *  net.minecraft.class_32$class_5143
 *  net.minecraft.class_332
 *  net.minecraft.class_3879
 *  net.minecraft.class_4588
 *  net.minecraft.class_4666
 *  net.minecraft.class_5218
 *  net.minecraft.class_630
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_6603
 *  net.minecraft.class_9779
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package xaero.common.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_1937;
import net.minecraft.class_2556;
import net.minecraft.class_2561;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2666;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_2678;
import net.minecraft.class_2759;
import net.minecraft.class_2818;
import net.minecraft.class_310;
import net.minecraft.class_32;
import net.minecraft.class_332;
import net.minecraft.class_3879;
import net.minecraft.class_4588;
import net.minecraft.class_4666;
import net.minecraft.class_5218;
import net.minecraft.class_630;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_6603;
import net.minecraft.class_9779;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IBufferSource;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.core.IXaeroMinimapSMultiBlockChangePacket;
import xaero.common.misc.Misc;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.icon.creator.render.trace.EntityRenderTracer;
import xaero.hud.minimap.world.container.MinimapWorldContainerUtil;
import xaero.hud.pushbox.BuiltInPushBoxes;
import xaero.hud.pushbox.boss.IBossHealthPushBox;
import xaero.hud.pushbox.effect.IPotionEffectsPushBox;

public class XaeroMinimapCore {
    public static IXaeroMinimap modMain;
    public static Field chunkCleanField;
    public static XaeroMinimapSession currentSession;
    public static Matrix4f waypointsProjection;
    public static Matrix4f waypointModelView;

    public static void ensureField() {
        if (chunkCleanField == null) {
            try {
                chunkCleanField = class_2818.class.getDeclaredField("xaero_chunkClean");
            }
            catch (NoSuchFieldException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void chunkUpdateCallback(int chunkX, int chunkZ) {
        XaeroMinimapCore.ensureField();
        class_638 world = class_310.method_1551().field_1687;
        if (world != null) {
            try {
                for (int x = chunkX - 1; x < chunkX + 2; ++x) {
                    for (int z = chunkZ - 1; z < chunkZ + 2; ++z) {
                        class_2818 chunk = world.method_8497(x, z);
                        if (chunk == null) continue;
                        chunkCleanField.set(chunk, false);
                    }
                }
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void onChunkData(int x, int z, class_6603 packetIn) {
        XaeroMinimapCore.chunkUpdateCallback(x, z);
    }

    private static void onChunkLightData(int x, int z) {
        XaeroMinimapCore.chunkUpdateCallback(x, z);
    }

    public static void onHandleLevelChunkWithLight(class_2672 packet) {
        XaeroMinimapCore.onChunkLightData(packet.method_11523(), packet.method_11524());
    }

    public static void onHandleLightUpdatePacket(class_2676 packet) {
        XaeroMinimapCore.onChunkLightData(packet.method_11558(), packet.method_11554());
    }

    public static void onQueueLightRemoval(class_2666 packet) {
        XaeroMinimapCore.onChunkLightData(packet.comp_1726().field_9181, packet.comp_1726().field_9180);
    }

    public static void onBlockChange(class_2626 packetIn) {
        XaeroMinimapCore.chunkUpdateCallback(packetIn.method_11309().method_10263() >> 4, packetIn.method_11309().method_10260() >> 4);
    }

    public static void onMultiBlockChange(class_2637 packetIn) {
        IXaeroMinimapSMultiBlockChangePacket packetAccess = (IXaeroMinimapSMultiBlockChangePacket)packetIn;
        XaeroMinimapCore.chunkUpdateCallback(packetAccess.xaero_mm_getSectionPos().method_10263(), packetAccess.xaero_mm_getSectionPos().method_10260());
    }

    public static void onPlayNetHandler(class_634 netHandler, class_2678 packet) {
        if (HudMod.INSTANCE != null) {
            HudMod.INSTANCE.tryLoadLater();
        }
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (modMain.getMinimap().getCrashedWith() != null) {
            return;
        }
        try {
            XaeroMinimapSession minimapSession;
            IXaeroMinimapClientPlayNetHandler netHandlerAccess = (IXaeroMinimapClientPlayNetHandler)netHandler;
            if (netHandlerAccess.getXaero_minimapSession() != null) {
                return;
            }
            if (currentSession != null) {
                MinimapLogs.LOGGER.info("Previous hud session still active. Probably using MenuMobs. Forcing it to end...");
                XaeroMinimapCore.cleanupCurrentSession();
            }
            currentSession = minimapSession = modMain.createSession();
            minimapSession.init(netHandler);
            netHandlerAccess.setXaero_minimapSession(minimapSession);
        }
        catch (Throwable e) {
            if (currentSession != null) {
                XaeroMinimapCore.cleanupCurrentSession();
            }
            RuntimeException wrappedException = new RuntimeException("Exception initializing Xaero's Minimap! ", e);
            modMain.getMinimap().setCrashedWith(wrappedException);
        }
    }

    private static void cleanupCurrentSession() {
        try {
            currentSession.tryCleanup();
        }
        catch (Throwable supressed) {
            MinimapLogs.LOGGER.error("suppressed exception", supressed);
        }
        finally {
            currentSession = null;
        }
    }

    public static void onPlayNetHandlerCleanup(class_634 netHandler) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        try {
            XaeroMinimapSession netHandlerSession = ((IXaeroMinimapClientPlayNetHandler)netHandler).getXaero_minimapSession();
            if (netHandlerSession == null) {
                return;
            }
            try {
                netHandlerSession.tryCleanup();
            }
            finally {
                if (netHandlerSession == currentSession) {
                    currentSession = null;
                }
                ((IXaeroMinimapClientPlayNetHandler)netHandler).setXaero_minimapSession(null);
            }
        }
        catch (Throwable e) {
            RuntimeException wrappedException = new RuntimeException("Exception finalizing Xaero's Minimap! ", e);
            modMain.getMinimap().setCrashedWith(wrappedException);
        }
    }

    public static void beforeRespawn(class_1657 player) {
        MinimapSession minimapSession;
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (player == class_310.method_1551().field_1724 && (minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession()) != null) {
            minimapSession.getWaypointSession().getDeathpointHandler().createDeathpoint(player);
        }
    }

    public static void onProjectionMatrix(Matrix4f matrixIn) {
        waypointsProjection.identity();
        waypointsProjection.mul((Matrix4fc)matrixIn);
    }

    public static void onWorldModelViewMatrix(Matrix4f matrix) {
        waypointModelView.identity();
        waypointModelView.mul((Matrix4fc)matrix);
    }

    public static void onRenderLevelMatrices(Matrix4f worldModelViewMatrix, Matrix4f projectionMatrix) {
        XaeroMinimapCore.onProjectionMatrix(projectionMatrix);
        XaeroMinimapCore.onWorldModelViewMatrix(worldModelViewMatrix);
    }

    public static void beforeIngameGuiRender(class_332 guiGraphics, class_9779 deltaTracker) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getEvents().handleRenderGameOverlayEventPre(guiGraphics, deltaTracker.method_60637(true));
    }

    public static void afterIngameGuiRender(class_332 guiGraphics, class_9779 deltaTracker) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getEvents().handleRenderGameOverlayEventPost();
    }

    public static void onRenderStatusEffectOverlayPost(class_332 guiGraphics) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        IPotionEffectsPushBox potionEffectsPushBox = BuiltInPushBoxes.getPotionEffectPushBox(modMain);
        if (potionEffectsPushBox != null) {
            potionEffectsPushBox.setActive(true);
        }
    }

    public static void onBossHealthRender(int h) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        IBossHealthPushBox bossHealthPushBox = BuiltInPushBoxes.getBossHealthPushBox(modMain);
        if (bossHealthPushBox != null) {
            bossHealthPushBox.setActive(true);
            bossHealthPushBox.setLastBossHealthHeight(h);
        }
    }

    public static void onEntityIconsModelRenderDetection(class_3879 model, class_4588 vertexConsumer, int color) {
        if (!EntityRenderTracer.TRACING_MODEL_RENDERS) {
            return;
        }
        modMain.getMinimap().getMinimapFBORenderer().onRadarIconModelRenderTrace(model, vertexConsumer, color);
    }

    public static void onEntityIconsModelPartRenderDetection(class_630 modelRenderer, int color) {
        if (!EntityRenderTracer.TRACING_MODEL_RENDERS) {
            return;
        }
        modMain.getMinimap().getMinimapFBORenderer().onEntityIconModelPartRenderTrace(modelRenderer, color);
    }

    public static void onDeleteWorld(class_32.class_5143 levelStorageAccess) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        String worldFolder = levelStorageAccess.method_27010(class_5218.field_24188).getParent().getFileName().toString();
        if (!worldFolder.isEmpty()) {
            String minimapWorldFolder = MinimapWorldContainerUtil.convertWorldFolderToContainerNode(worldFolder);
            Path minimapWorldFolderPath = HudMod.INSTANCE.getMinimapFolder().resolve(minimapWorldFolder);
            if (minimapWorldFolderPath.toFile().exists()) {
                try {
                    Misc.deleteFile(minimapWorldFolderPath, 20);
                    MinimapLogs.LOGGER.info(String.format("Deleted minimap world data at %s", minimapWorldFolderPath));
                }
                catch (IOException e) {
                    MinimapLogs.LOGGER.error(String.format("Failed to delete minimap world data at %s!", minimapWorldFolderPath), (Throwable)e);
                }
            }
        }
    }

    public static void onSpawn(class_2759 packetIn) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        modMain.getEvents().handlePlayerSetSpawnEvent(packetIn.method_11870(), (class_1937)class_310.method_1551().field_1687);
    }

    public static boolean onLocalPlayerCommand(String command) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        return modMain.getEvents().handleClientSendChatEvent(command);
    }

    public static boolean onSystemChat(class_2561 component) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        return modMain.getEvents().handleClientSystemChatReceivedEvent(component);
    }

    public static boolean onHandleDisguisedChatMessage(class_2556.class_7602 chatType, class_2561 component) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return true;
        }
        return !modMain.getEvents().handleClientPlayerChatReceivedEvent(chatType, component, null);
    }

    public static boolean isModLoaded() {
        return modMain != null && modMain.isLoadedClient();
    }

    public static boolean onRenderStatusEffectOverlay(class_332 guiGraphics) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        return modMain.getEvents().handleRenderStatusEffectOverlay(guiGraphics);
    }

    public static boolean onRenderCrosshair(class_332 guiGraphics) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        return modMain.getEvents().handleRenderCrosshairOverlay(guiGraphics);
    }

    public static void handleRenderModOverlay(class_332 guiGraphics, class_9779 deltaTracker) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        modMain.getModClientEvents().handleRenderModOverlay(guiGraphics, deltaTracker);
    }

    public static void onBufferSourceGetBuffer(IBufferSource mixinBufferSource, class_1921 renderType) {
        if (!EntityRenderTracer.TRACING_MODEL_RENDERS) {
            return;
        }
        mixinBufferSource.setXaero_lastRenderType(renderType);
    }

    public static boolean onToggleKeyIsDown(class_4666 keyMapping) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return false;
        }
        if (class_310.method_1551().field_1690 == null) {
            return false;
        }
        return modMain.getEvents().handleForceToggleKeyMapping(keyMapping);
    }

    static {
        chunkCleanField = null;
        waypointsProjection = new Matrix4f();
        waypointModelView = new Matrix4f();
    }
}

