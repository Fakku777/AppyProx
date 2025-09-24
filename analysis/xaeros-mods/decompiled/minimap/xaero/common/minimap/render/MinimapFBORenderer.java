/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  net.minecraft.class_10366
 *  net.minecraft.class_1044
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_308$class_11274
 *  net.minecraft.class_310
 *  net.minecraft.class_3879
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5321
 *  net.minecraft.class_630
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Vector3fc
 */
package xaero.common.minimap.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.class_10366;
import net.minecraft.class_1044;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_3879;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_5321;
import net.minecraft.class_630;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3fc;
import xaero.common.IXaeroMinimap;
import xaero.common.effect.Effects;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.CustomVertexConsumers;
import xaero.common.graphics.GpuTextureAndView;
import xaero.common.graphics.ImprovedFramebuffer;
import xaero.common.graphics.OpenGlHelper;
import xaero.common.graphics.TextureUtils;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.graphics.shader.FramebufferLinesShaderHelper;
import xaero.common.graphics.shader.PositionTexAlphaTestShaderHelper;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.region.MinimapChunk;
import xaero.common.minimap.render.MinimapRenderer;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.write.MinimapWriter;
import xaero.common.misc.Misc;
import xaero.common.misc.OptimizedMath;
import xaero.common.settings.ModSettings;
import xaero.hud.compat.mods.ImmediatelyFastHelper;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.compass.render.CompassRenderer;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.map.MinimapElementMapRendererHandler;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.icon.RadarIconManager;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.render.element.RadarRenderer;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderer;
import xaero.hud.render.TextureLocations;
import xaero.hud.render.util.ImmediateRenderUtil;
import xaero.hud.render.util.MultiTextureRenderUtil;
import xaero.hud.render.util.RenderBufferUtil;

public class MinimapFBORenderer
extends MinimapRenderer {
    private ImprovedFramebuffer scalingFramebuffer;
    private ImprovedFramebuffer rotationFramebuffer;
    private MinimapElementMapRendererHandler minimapElementMapRendererHandler;
    private RadarRenderer entityRadarRenderer;
    private RadarIconManager radarIconManager;
    private boolean triedFBO;
    private boolean loadedFBO;

    public MinimapFBORenderer(IXaeroMinimap modMain, class_310 mc, WaypointMapRenderer waypointMapRenderer, Minimap minimap, CompassRenderer compassRenderer, class_4587 matrixStack) {
        super(modMain, mc, waypointMapRenderer, minimap, compassRenderer, matrixStack);
    }

    public void loadFrameBuffer(MinimapProcessor minimapProcessor) {
        if (!minimapProcessor.canUseFrameBuffer()) {
            MinimapLogs.LOGGER.info("FBO mode not supported! Using minimap safe mode.");
        } else {
            this.scalingFramebuffer = new ImprovedFramebuffer(512, 512, true);
            this.rotationFramebuffer = new ImprovedFramebuffer(512, 512, true);
            this.rotationFramebuffer.method_58226(FilterMode.LINEAR);
            this.radarIconManager = new RadarIconManager(new RadarIconCreator());
            this.loadedFBO = this.scalingFramebuffer.method_30277() != null;
            this.minimapElementMapRendererHandler = ((MinimapElementMapRendererHandler.Builder)MinimapElementMapRendererHandler.Builder.begin().setPoseStack(this.matrixStack)).build();
            this.entityRadarRenderer = RadarRenderer.Builder.begin().setRadarIconManager(this.radarIconManager).setMinimap(this.minimap).build();
            this.minimapElementMapRendererHandler.add(this.entityRadarRenderer);
            this.minimap.getOverMapRendererHandler().add(this.entityRadarRenderer);
            if (this.modMain.getSupportMods().worldmap()) {
                this.modMain.getSupportMods().worldmapSupport.createRadarRenderWrapper(this.entityRadarRenderer);
            }
        }
        this.triedFBO = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void renderChunks(MinimapSession minimapSession, MinimapProcessor minimap, class_243 renderPos, class_5321<class_1937> mapDimension, double mapDimensionScale, int mapSize, int bufferSize, float sizeFix, float partial, int lightLevel, boolean useWorldMap, boolean lockedNorth, int shape, double ps, double pc, boolean cave, boolean circle, ModSettings settings, CustomVertexConsumers cvc) {
        MinimapWriter minimapWriter = minimap.getMinimapWriter();
        synchronized (minimapWriter) {
            try {
                this.renderChunksToFBO(minimapSession, this.matrixStack, minimap, renderPos, mapDimension, mapDimensionScale, mapSize, partial, lightLevel, useWorldMap, lockedNorth, shape, ps, pc, cave, cvc);
            }
            catch (Throwable t) {
                ImprovedFramebuffer.restoreMainRenderTarget();
                throw t;
            }
        }
        this.scalingFramebuffer.bindDefaultFramebuffer(class_310.method_1551());
        this.rotationFramebuffer.bindRead();
    }

    public void renderChunksToFBO(MinimapSession minimapSession, class_4587 matrixStack, MinimapProcessor minimap, class_243 renderPos, class_5321<class_1937> mapDimension, double mapDimensionScale, int viewW, float partial, int level, boolean useWorldMap, boolean lockedNorth, int shape, double ps, double pc, boolean cave, CustomVertexConsumers cvc) {
        double zInsidePixel;
        GpuBufferSlice projectionMatrixBackup = RenderSystem.getProjectionMatrixBuffer();
        class_10366 projectionTypeBackup = RenderSystem.getProjectionType();
        matrixStack.method_22903();
        matrixStack.method_34426();
        MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers = minimapSession.getMultiTextureRenderTypeRenderers();
        double maxVisibleLength = lockedNorth || shape == 1 ? (double)viewW : (double)viewW * Math.sqrt(2.0);
        double halfMaxVisibleLength = maxVisibleLength / 2.0;
        double radiusBlocks = maxVisibleLength / 2.0 / this.zoom;
        int xFloored = OptimizedMath.myFloor(renderPos.field_1352);
        int zFloored = OptimizedMath.myFloor(renderPos.field_1350);
        int playerChunkX = xFloored >> 6;
        int playerChunkZ = zFloored >> 6;
        int offsetX = xFloored & 0x3F;
        int offsetZ = zFloored & 0x3F;
        boolean zooming = (double)((int)this.zoom) != this.zoom;
        ImmediatelyFastHelper.triggerBatchingBuffersFlush(matrixStack);
        this.scalingFramebuffer.bindAsMainTarget(true);
        TextureUtils.clearRenderTarget((class_276)this.scalingFramebuffer, 0, 1.0f);
        this.mc.field_1773.method_71114().method_71034(class_308.class_11274.field_60026);
        long before = System.currentTimeMillis();
        this.helper.defaultOrtho((class_276)this.scalingFramebuffer);
        Matrix4fStack shaderMatrixStack = RenderSystem.getModelViewStack();
        shaderMatrixStack.pushMatrix();
        shaderMatrixStack.identity();
        before = System.currentTimeMillis();
        double xInsidePixel = renderPos.field_1352 - (double)xFloored;
        if (xInsidePixel < 0.0) {
            xInsidePixel += 1.0;
        }
        if ((zInsidePixel = renderPos.field_1350 - (double)zFloored) < 0.0) {
            zInsidePixel += 1.0;
        }
        float halfWView = (float)viewW / 2.0f;
        float angle = (float)(90.0 - this.getRenderAngle(lockedNorth));
        shaderMatrixStack.translate(256.0f, 256.0f, -2000.0f);
        shaderMatrixStack.scale((float)this.zoom, (float)this.zoom, 1.0f);
        this.helper.drawMyColoredRect(matrixStack.method_23760().method_23761(), -256.0f, -256.0f, 256.0f, 256.0f, -16777216);
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        class_4597.class_4598 renderTypeBuffers = cvc.getBetterPVPRenderTypeBuffers();
        class_4588 overlayBufferBuilder = renderTypeBuffers.getBuffer(CustomRenderTypes.MAP_CHUNK_OVERLAY);
        float chunkGridAlphaMultiplier = 1.0f;
        int minX = playerChunkX + (int)Math.floor(((double)offsetX - radiusBlocks) / 64.0);
        int minZ = playerChunkZ + (int)Math.floor(((double)offsetZ - radiusBlocks) / 64.0);
        int maxX = playerChunkX + (int)Math.floor(((double)(offsetX + 1) + radiusBlocks) / 64.0);
        int maxZ = playerChunkZ + (int)Math.floor(((double)(offsetZ + 1) + radiusBlocks) / 64.0);
        if (!cave || !Misc.hasEffect((class_1657)this.mc.field_1724, Effects.NO_CAVE_MAPS) && !Misc.hasEffect((class_1657)this.mc.field_1724, Effects.NO_CAVE_MAPS_HARMFUL)) {
            if (useWorldMap) {
                chunkGridAlphaMultiplier = this.modMain.getSupportMods().worldmapSupport.getMinimapBrightness();
                this.modMain.getSupportMods().worldmapSupport.drawMinimap(minimapSession, matrixStack, this.getHelper(), xFloored, zFloored, minX, minZ, maxX, maxZ, zooming, this.zoom, mapDimensionScale, overlayBufferBuilder, multiTextureRenderTypeRenderers);
            } else if (minimap.getMinimapWriter().getLoadedBlocks() != null && level >= 0) {
                int loadedLevels = minimap.getMinimapWriter().getLoadedLevels();
                chunkGridAlphaMultiplier = loadedLevels <= 1 ? 1.0f : 0.375f + 0.625f * (1.0f - (float)level / (float)(loadedLevels - 1));
                int loadedMapChunkX = minimap.getMinimapWriter().getLoadedMapChunkX();
                int loadedMapChunkZ = minimap.getMinimapWriter().getLoadedMapChunkZ();
                int loadedWidth = minimap.getMinimapWriter().getLoadedBlocks().length;
                boolean slimeChunks = this.modMain.getSettings().getSlimeChunks(minimapSession);
                minX = Math.max(minX, loadedMapChunkX);
                minZ = Math.max(minZ, loadedMapChunkZ);
                maxX = Math.min(maxX, loadedMapChunkX + loadedWidth - 1);
                maxZ = Math.min(maxZ, loadedMapChunkZ + loadedWidth - 1);
                class_1044 guiTextures = this.mc.method_1531().method_4619(TextureLocations.GUI_TEXTURES);
                guiTextures.method_4527(true, false);
                MultiTextureRenderTypeRenderer multiTextureRenderTypeRenderer = multiTextureRenderTypeRenderers.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.GUI);
                MinimapRendererHelper helper = this.getHelper();
                for (int X = minX; X <= maxX; ++X) {
                    int canvasX = X - minimap.getMinimapWriter().getLoadedMapChunkX();
                    for (int Z = minZ; Z <= maxZ; ++Z) {
                        int canvasZ = Z - minimap.getMinimapWriter().getLoadedMapChunkZ();
                        MinimapChunk mchunk = minimap.getMinimapWriter().getLoadedBlocks()[canvasX][canvasZ];
                        if (mchunk == null) continue;
                        GpuTextureAndView texture = mchunk.bindTexture(level);
                        if (!mchunk.isHasSomething() || level >= mchunk.getLevelsBuffered() || texture == null) continue;
                        if (!zooming) {
                            texture.texture.setTextureFilter(FilterMode.NEAREST, false);
                        } else {
                            texture.texture.setTextureFilter(FilterMode.LINEAR, false);
                        }
                        int drawX = (X - playerChunkX) * 64 - offsetX;
                        int drawZ = (Z - playerChunkZ) * 64 - offsetZ;
                        MultiTextureRenderUtil.prepareTexturedColoredRect(matrixStack.method_23760().method_23761(), (float)drawX, (float)drawZ, 0, 64, 64.0f, 64.0f, -64.0f, 64.0f, 1.0f, 1.0f, 1.0f, 1.0f, texture.texture, multiTextureRenderTypeRenderer);
                        if (!slimeChunks) continue;
                        for (int t = 0; t < 16; ++t) {
                            if (mchunk.getTile(t % 4, t / 4) == null || !mchunk.getTile(t % 4, t / 4).isSlimeChunk()) continue;
                            int slimeDrawX = drawX + 16 * (t % 4);
                            int slimeDrawZ = drawZ + 16 * (t / 4);
                            RenderBufferUtil.addColoredRect(matrixStack.method_23760().method_23761(), overlayBufferBuilder, slimeDrawX, slimeDrawZ, 16, 16, -2142047936);
                        }
                    }
                }
                multiTextureRenderTypeRenderers.draw(multiTextureRenderTypeRenderer);
                guiTextures.method_4527(false, false);
            }
        }
        if (this.modMain.getSettings().chunkGrid > -1) {
            int i;
            class_4588 lineBufferBuilder = renderTypeBuffers.getBuffer(CustomRenderTypes.MAP_LINES);
            int grid = ModSettings.COLORS[this.modMain.getSettings().chunkGrid];
            int r = grid >> 16 & 0xFF;
            int g = grid >> 8 & 0xFF;
            int b = grid & 0xFF;
            FramebufferLinesShaderHelper.setFrameSize(this.scalingFramebuffer.field_1480, this.scalingFramebuffer.field_1477);
            float red = (float)r / 255.0f;
            float green = (float)g / 255.0f;
            float blue = (float)b / 255.0f;
            float alpha = 0.8f;
            float colorMultiplier = chunkGridAlphaMultiplier;
            red *= colorMultiplier;
            green *= colorMultiplier;
            blue *= colorMultiplier;
            RenderSystem.lineWidth((float)this.modMain.getSettings().chunkGridLineWidth);
            boolean bias = true;
            class_4587.class_4665 matrices = matrixStack.method_23760();
            for (int X = minX; X <= maxX; ++X) {
                int drawX = (X - playerChunkX + 1) * 64 - offsetX;
                for (i = 0; i < 4; ++i) {
                    float lineX = (float)drawX + (float)(-16 * i);
                    this.helper.addColoredLineToExistingBuffer(matrices, lineBufferBuilder, lineX, -((float)halfMaxVisibleLength), lineX, (float)halfMaxVisibleLength + (float)bias, red, green, blue, alpha);
                }
            }
            for (int Z = minZ; Z <= maxZ; ++Z) {
                int drawZ = (Z - playerChunkZ + 1) * 64 - offsetZ;
                for (i = 0; i < 4; ++i) {
                    float lineZ = (float)drawZ + (float)((double)(-16 * i) - 1.0 / this.zoom);
                    this.helper.addColoredLineToExistingBuffer(matrices, lineBufferBuilder, -((float)halfMaxVisibleLength), lineZ, (float)halfMaxVisibleLength + (float)bias, lineZ, red, green, blue, alpha);
                }
            }
        }
        renderTypeBuffers.method_22993();
        this.rotationFramebuffer.bindAsMainTarget(false);
        TextureUtils.clearRenderTarget((class_276)this.rotationFramebuffer, 0, 1.0f);
        this.scalingFramebuffer.bindRead();
        shaderMatrixStack.identity();
        if (this.modMain.getSettings().getAntiAliasing()) {
            this.scalingFramebuffer.method_30277().setTextureFilter(FilterMode.LINEAR, false);
        } else {
            this.scalingFramebuffer.method_30277().setTextureFilter(FilterMode.NEAREST, false);
        }
        shaderMatrixStack.translate(halfWView, halfWView, -2980.0f);
        shaderMatrixStack.pushMatrix();
        if (!lockedNorth) {
            OptimizedMath.rotateMatrix((Matrix4f)shaderMatrixStack, -angle, (Vector3fc)OptimizedMath.ZP);
        }
        shaderMatrixStack.translate((float)(-xInsidePixel * this.zoom), (float)(-zInsidePixel * this.zoom), 0.0f);
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, (float)(this.modMain.getSettings().minimapOpacity / 100.0));
        PositionTexAlphaTestShaderHelper.setDiscardAlpha(0.0f);
        ImmediateRenderUtil.texturedRect(matrixStack, -256.0f, -256.0f, 0, 0, 512.0f, 512.0f, 512.0f, 512.0f, CustomRenderTypes.RP_POSITION_TEX_ALPHA_REPLACE);
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        shaderMatrixStack.popMatrix();
        before = System.currentTimeMillis();
        OpenGlHelper.fixOtherMods();
        this.minimapElementMapRendererHandler.prepareRender(ps, pc, this.zoom, halfWView);
        this.minimapElementMapRendererHandler.render(renderPos, partial, (class_276)this.rotationFramebuffer, mapDimensionScale, mapDimension);
        renderTypeBuffers.method_22993();
        ImmediatelyFastHelper.triggerBatchingBuffersFlush(matrixStack);
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionMatrixBackup, (class_10366)projectionTypeBackup);
        shaderMatrixStack.popMatrix();
        matrixStack.method_22909();
    }

    public void deleteFramebuffers() {
        this.scalingFramebuffer.method_1238();
        this.rotationFramebuffer.method_1238();
        if (this.radarIconManager != null) {
            this.radarIconManager.reset();
        }
    }

    public boolean isLoadedFBO() {
        return this.loadedFBO;
    }

    public void setLoadedFBO(boolean loadedFBO) {
        this.loadedFBO = loadedFBO;
    }

    public boolean isTriedFBO() {
        return this.triedFBO;
    }

    public void resetEntityIcons() {
        if (this.radarIconManager != null) {
            this.radarIconManager.reset();
        }
    }

    public void resetEntityIconsResources() {
        if (this.radarIconManager != null) {
            this.radarIconManager.resetResources();
        }
    }

    public void onRadarIconModelRenderTrace(class_3879 model, class_4588 vertexConsumer, int color) {
        this.radarIconManager.onModelRenderTrace(model, vertexConsumer, color);
    }

    public void onEntityIconModelPartRenderTrace(class_630 modelRenderer, int color) {
        this.radarIconManager.onModelPartRenderTrace(modelRenderer, color);
    }

    public void renderMainEntityDot(class_1297 renderEntity, boolean cave, class_4597.class_4598 renderTypeBuffers) {
        MinimapElementGraphics guiGraphics = this.minimapElementMapRendererHandler.getGuiGraphics();
        guiGraphics.pose().method_22903();
        this.entityRadarRenderer.renderSingleEntity(renderEntity, cave, false, 2.0f, false, false, MinimapElementRenderLocation.OVER_MINIMAP, null, guiGraphics);
        guiGraphics.flush();
        guiGraphics.pose().method_22909();
    }

    public RadarRenderer getEntityRadarRenderer() {
        return this.entityRadarRenderer;
    }
}

