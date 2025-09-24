/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.class_10366
 *  net.minecraft.class_1041
 *  net.minecraft.class_11278
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_8030
 */
package xaero.common.minimap;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_10366;
import net.minecraft.class_1041;
import net.minecraft.class_11278;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_8030;
import xaero.common.IXaeroMinimap;
import xaero.common.anim.MultiplyAnimationHelper;
import xaero.common.core.IGameRenderer;
import xaero.common.core.IGuiRenderer;
import xaero.common.graphics.CustomVertexConsumers;
import xaero.common.minimap.mcworld.MinimapClientWorldData;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.common.minimap.render.MinimapDepthTraceListener;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.write.MinimapWriter;
import xaero.common.misc.Misc;
import xaero.common.settings.ModOptions;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.player.tracker.synced.ClientSyncedTrackedPlayerManager;
import xaero.hud.minimap.radar.RadarSession;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.render.util.GuiDepthSkipper;
import xaero.hud.render.util.GuiDepthTracer;

public class MinimapProcessor {
    public static final boolean DEBUG = false;
    public static final int FRAME = 9;
    private IXaeroMinimap modMain;
    private MinimapSession minimapSession;
    private MinimapWriter minimapWriter;
    private RadarSession radarSession;
    private Minimap minimap;
    private EntityRadarCategoryManager entityCategoryManager;
    private ClientSyncedTrackedPlayerManager syncedTrackedPlayerManager;
    private double minimapZoom;
    private boolean toResetImage;
    private boolean enlargedMap;
    private boolean manualCaveMode;
    private boolean noMinimapMessageReceived;
    private boolean fairPlayOnlyMessageReceived;
    private double lastMapDimensionScale = 1.0;
    private class_5321<class_1937> lastMapDimension;
    private double lastPlayerDimDiv = 1.0;
    private final MinimapDepthTraceListener depthTraceListener;
    private final GuiDepthTracer depthTracer;
    private final GuiDepthSkipper depthSkipper;

    public MinimapProcessor(IXaeroMinimap modMain, MinimapSession minimapSession, MinimapWriter minimapWriter, RadarSession radarSession, ClientSyncedTrackedPlayerManager syncedTrackedPlayerManager) {
        this.modMain = modMain;
        this.minimapSession = minimapSession;
        this.minimapWriter = minimapWriter;
        this.radarSession = radarSession;
        this.minimapZoom = 1.0;
        this.toResetImage = true;
        this.minimap = modMain.getMinimap();
        this.syncedTrackedPlayerManager = syncedTrackedPlayerManager;
        this.depthTraceListener = new MinimapDepthTraceListener(this);
        class_8030 maxRectangle = new class_8030(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.depthTracer = new GuiDepthTracer(this.depthTraceListener, maxRectangle);
        this.depthSkipper = new GuiDepthSkipper(maxRectangle);
    }

    public int getMinimapSize() {
        return this.enlargedMap ? 500 : this.modMain.getSettings().getMinimapSize() * 2;
    }

    public int getMinimapBufferSize(int minimapSize) {
        int bufferSize = 128 * (int)Math.pow(2.0, Math.ceil(Math.log((double)minimapSize / 128.0) / Math.log(2.0)));
        if (bufferSize < 128) {
            return 128;
        }
        if (bufferSize > 512) {
            return 512;
        }
        return bufferSize;
    }

    public boolean isEnlargedMap() {
        return this.enlargedMap;
    }

    public void setEnlargedMap(boolean enlargedMap) {
        this.enlargedMap = enlargedMap;
    }

    public double getMinimapZoom() {
        return this.minimapZoom;
    }

    public boolean isCaveModeDisplayed() {
        return this.minimapWriter.getLoadedCaving() != Integer.MAX_VALUE;
    }

    public double getTargetZoom() {
        this.modMain.getSettings();
        float settingsZoom = ModSettings.zooms[this.modMain.getSettings().zoom];
        if (this.enlargedMap && this.modMain.getSettings().zoomOnEnlarged > 0) {
            settingsZoom = this.modMain.getSettings().zoomOnEnlarged;
        }
        float target = settingsZoom * (this.modMain.getSettings().caveZoom > 0 && this.isCaveModeDisplayed() ? (float)(1 + this.modMain.getSettings().caveZoom) : 1.0f);
        this.modMain.getSettings();
        this.modMain.getSettings();
        if (target > ModSettings.zooms[ModSettings.zooms.length - 1]) {
            this.modMain.getSettings();
            this.modMain.getSettings();
            target = ModSettings.zooms[ModSettings.zooms.length - 1];
        }
        return target;
    }

    public void instantZoom() {
        this.minimapZoom = this.getTargetZoom();
    }

    public void updateZoom() {
        double target = this.getTargetZoom();
        double off = target - this.minimapZoom;
        off = off > 0.01 || off < -0.01 ? (double)((float)MultiplyAnimationHelper.animate(off, 0.8)) : 0.0;
        this.minimapZoom = target - off;
    }

    public MinimapWriter getMinimapWriter() {
        return this.minimapWriter;
    }

    public boolean canUseFrameBuffer() {
        return true;
    }

    public int getFBOBufferSize() {
        return 512;
    }

    public void onClientTick() {
        class_1937 world = null;
        class_746 player = class_310.method_1551().field_1724;
        if (player != null && player.method_37908() instanceof class_638) {
            world = player.method_37908();
        }
        class_1297 renderEntity = class_310.method_1551().method_1560();
        this.radarSession.update((class_638)world, renderEntity, (class_1657)player);
    }

    public void onPlayerTick() {
    }

    public void checkFBO() {
        if (this.minimap.getMinimapFBORenderer().isLoadedFBO() && !this.canUseFrameBuffer()) {
            this.minimap.getMinimapFBORenderer().setLoadedFBO(false);
            this.minimap.getMinimapFBORenderer().deleteFramebuffers();
            this.toResetImage = true;
        }
        if (!(this.minimap.getMinimapFBORenderer().isLoadedFBO() || this.modMain.getSettings().mapSafeMode || this.minimap.getMinimapFBORenderer().isTriedFBO())) {
            if (class_310.method_1551().method_18506() != null) {
                return;
            }
            this.minimap.getMinimapFBORenderer().loadFrameBuffer(this);
        }
    }

    public void onRender(int x, int y, int width, int height, double scale, int size, int boxSize, float partial, CustomVertexConsumers cvc, float depth) {
        MinimapRendererHelper.restoreDefaultShaderBlendState();
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().identity();
        RenderSystem.getModelViewStack().translate(0.0f, 0.0f, -11000.0f);
        try {
            if (this.enlargedMap && this.modMain.getSettings().centeredEnlarged) {
                x = (width - boxSize) / 2;
                y = (height - boxSize) / 2;
            }
            class_310 mc = class_310.method_1551();
            class_1041 window = mc.method_22683();
            IGuiRenderer guiRenderer = (IGuiRenderer)((IGameRenderer)mc.field_1773).xaero_mm_getGuiRenderer();
            class_11278 orthoProjectionCache = guiRenderer.xaero_mm_getGuiProjectionMatrixBuffer();
            RenderSystem.setProjectionMatrix((GpuBufferSlice)orthoProjectionCache.method_71092((float)window.method_4489() / (float)window.method_4495(), (float)window.method_4506() / (float)window.method_4495()), (class_10366)class_10366.field_54954);
            this.minimap.getMatrixStack().method_22903();
            this.minimap.getMatrixStack().method_46416(0.0f, 0.0f, depth);
            if (this.minimap.usingFBO()) {
                this.minimap.getMinimapFBORenderer().renderMinimap(this.minimapSession, this, x, y, width, height, scale, size, partial, cvc);
            } else {
                this.minimap.getMinimapSafeModeRenderer().renderMinimap(this.minimapSession, this, x, y, width, height, scale, size, partial, cvc);
            }
            this.minimap.getMatrixStack().method_22909();
        }
        catch (Throwable e) {
            this.minimap.setCrashedWith(e);
        }
        RenderSystem.getModelViewStack().popMatrix();
        MinimapRendererHelper.restoreDefaultShaderBlendState();
    }

    public static boolean hasMinimapItem(class_1657 player) {
        return Misc.hasItem(player, ModSettings.minimapItem);
    }

    public boolean isToResetImage() {
        return this.toResetImage;
    }

    public void setToResetImage(boolean toResetImage) {
        this.toResetImage = toResetImage;
    }

    public RadarSession getRadarSession() {
        return this.radarSession;
    }

    public void cleanup() {
        this.minimapWriter.cleanup();
    }

    public boolean isManualCaveMode() {
        return this.manualCaveMode || this.modMain.getSettings().usesWorldMapScreenValue(ModOptions.MANUAL_CAVE_MODE_START) && this.modMain.getSupportMods().worldmapSupport.getManualCaveStart() != Integer.MAX_VALUE;
    }

    public void toggleManualCaveMode() {
        this.manualCaveMode = !this.isManualCaveMode();
    }

    public Minimap getMinimap() {
        return this.minimap;
    }

    public boolean getNoMinimapMessageReceived() {
        return this.noMinimapMessageReceived;
    }

    public void setNoMinimapMessageReceived(boolean noMinimapMessageReceived) {
        this.noMinimapMessageReceived = noMinimapMessageReceived;
    }

    public boolean getForcedFairPlay() {
        return this.fairPlayOnlyMessageReceived;
    }

    public void setFairPlayOnlyMessageReceived(boolean fairPlayOnlyMessageReceived) {
        this.fairPlayOnlyMessageReceived = fairPlayOnlyMessageReceived;
    }

    public ClientSyncedTrackedPlayerManager getSyncedTrackedPlayerManager() {
        return this.syncedTrackedPlayerManager;
    }

    public boolean serverHasMod() {
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
        return worldData != null && worldData.serverLevelId != null;
    }

    public void setServerModNetworkVersion(int networkVersion) {
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
        if (worldData == null) {
            return;
        }
        worldData.setServerModNetworkVersion(networkVersion);
    }

    public int getServerModNetworkVersion() {
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
        if (worldData == null) {
            return 0;
        }
        return worldData.getServerModNetworkVersion();
    }

    public double getLastMapDimensionScale() {
        return this.lastMapDimensionScale;
    }

    public void setLastMapDimensionScale(double lastMapDimensionScale) {
        this.lastMapDimensionScale = lastMapDimensionScale;
    }

    public class_5321<class_1937> getLastMapDimension() {
        return this.lastMapDimension;
    }

    public void setLastMapDimension(class_5321<class_1937> lastMapDimension) {
        this.lastMapDimension = lastMapDimension;
    }

    public MinimapSession getSession() {
        return this.minimapSession;
    }

    public MinimapDepthTraceListener getDepthTraceListener() {
        return this.depthTraceListener;
    }

    public GuiDepthTracer getDepthTracer() {
        return this.depthTracer;
    }

    public GuiDepthSkipper getDepthSkipper() {
        return this.depthSkipper;
    }
}

