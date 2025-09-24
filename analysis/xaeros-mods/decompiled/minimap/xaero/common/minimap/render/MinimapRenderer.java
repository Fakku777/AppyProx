/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_1044
 *  net.minecraft.class_1297
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_308$class_11274
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  org.joml.Matrix4f
 *  org.joml.Vector3fc
 */
package xaero.common.minimap.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.class_1044;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.CustomVertexConsumers;
import xaero.common.graphics.GuiHelper;
import xaero.common.graphics.OpenGlHelper;
import xaero.common.graphics.TextureUtils;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.render.MinimapSafeModeRenderer;
import xaero.common.misc.OptimizedMath;
import xaero.common.settings.ModSettings;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.compass.render.CompassRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.RadarSession;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.color.RadarColor;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderer;
import xaero.hud.render.TextureLocations;
import xaero.hud.render.util.ImmediateRenderUtil;

public abstract class MinimapRenderer {
    public static final int black = -16777216;
    public static final int slime = -2142047936;
    protected IXaeroMinimap modMain;
    protected class_310 mc;
    protected Minimap minimap;
    protected MinimapRendererHelper helper;
    protected WaypointMapRenderer waypointMapRenderer;
    private int lastMinimapSize;
    protected double zoom = 1.0;
    private class_2338.class_2339 mutableBlockPos;
    protected final CompassRenderer compassRenderer;
    protected final class_4587 matrixStack;

    public MinimapRenderer(IXaeroMinimap modMain, class_310 mc, WaypointMapRenderer waypointMapRenderer, Minimap minimap, CompassRenderer compassRenderer, class_4587 matrixStack) {
        this.modMain = modMain;
        this.mc = mc;
        this.waypointMapRenderer = waypointMapRenderer;
        this.minimap = minimap;
        this.matrixStack = matrixStack;
        this.helper = new MinimapRendererHelper();
        this.mutableBlockPos = new class_2338.class_2339();
        this.compassRenderer = compassRenderer;
    }

    public double getRenderAngle(boolean lockedNorth) {
        if (lockedNorth) {
            return 90.0;
        }
        return this.getActualAngle();
    }

    private double getActualAngle() {
        double rotation = this.mc.field_1773.method_19418().method_19330();
        return -90.0 - rotation;
    }

    protected abstract void renderChunks(MinimapSession var1, MinimapProcessor var2, class_243 var3, class_5321<class_1937> var4, double var5, int var7, int var8, float var9, float var10, int var11, boolean var12, boolean var13, int var14, double var15, double var17, boolean var19, boolean var20, ModSettings var21, CustomVertexConsumers var22);

    public void renderMinimap(MinimapSession minimapSession, MinimapProcessor minimap, int x, int y, int width, int height, double scale, int size, float partial, CustomVertexConsumers cvc) {
        boolean crosshairDisplayed;
        int specW;
        int frameType;
        boolean renderFrame;
        ModSettings settings = this.modMain.getSettings();
        if (settings.getMinimapSize() != this.lastMinimapSize) {
            this.lastMinimapSize = settings.getMinimapSize();
            minimap.setToResetImage(true);
        }
        minimap.getRadarSession().getStateUpdater().setLastRenderViewEntity(this.mc.method_1560());
        int mapSize = minimapSession.getProcessor().getMinimapSize();
        int bufferSize = minimapSession.getProcessor().getMinimapBufferSize(mapSize);
        if (this.minimap.usingFBO()) {
            bufferSize = minimap.getFBOBufferSize();
        }
        float minimapScale = settings.getMinimapScale();
        float mapScale = (float)(scale / (double)minimapScale);
        minimap.updateZoom();
        this.zoom = minimap.getMinimapZoom();
        this.mc.field_1773.method_71114().method_71034(class_308.class_11274.field_60026);
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        OpenGlHelper.resetPixelStore();
        float sizeFix = (float)bufferSize / 512.0f;
        int shape = settings.minimapShape;
        boolean lockedNorth = settings.getLockNorth(mapSize / 2, shape);
        double angle = Math.toRadians(this.getRenderAngle(lockedNorth));
        double ps = Math.sin(Math.PI - angle);
        double pc = Math.cos(Math.PI - angle);
        boolean useWorldMap = this.modMain.getSupportMods().shouldUseWorldMapChunks() && !minimap.getMinimapWriter().isLoadedNonWorldMap();
        int lightLevel = (int)((1.0f - Math.min(1.0f, this.getSunBrightness(minimap, settings.getLighting()))) * (float)(minimap.getMinimapWriter().getLoadedLevels() - 1));
        boolean cave = minimap.isCaveModeDisplayed();
        boolean circleShape = shape == 1;
        double playerX = EntityUtils.getEntityX(this.mc.method_1560(), partial);
        double playerY = EntityUtils.getEntityY(this.mc.method_1560(), partial);
        double playerZ = EntityUtils.getEntityZ(this.mc.method_1560(), partial);
        double renderX = playerX;
        double renderZ = playerZ;
        double mapDimensionScale = this.mc.field_1687.method_8597().comp_646();
        class_5321<class_1937> mapDimension = this.mc.field_1687.method_27983();
        double playerDimDiv = 1.0;
        if (useWorldMap) {
            double playerCoordinateScale = mapDimensionScale;
            mapDimensionScale = this.modMain.getSupportMods().worldmapSupport.getMapDimensionScale();
            mapDimension = this.modMain.getSupportMods().worldmapSupport.getMapDimension();
            if (mapDimensionScale == 0.0) {
                mapDimensionScale = minimap.getLastMapDimensionScale();
                mapDimension = minimap.getLastMapDimension();
            }
            playerDimDiv = mapDimensionScale / playerCoordinateScale;
            renderX /= playerDimDiv;
            renderZ /= playerDimDiv;
        }
        minimap.setLastMapDimensionScale(mapDimensionScale);
        minimap.setLastMapDimension(mapDimension);
        class_243 renderPos = new class_243(renderX, playerY, renderZ);
        this.matrixStack.method_22903();
        this.renderChunks(minimapSession, minimap, renderPos, mapDimension, mapDimensionScale, mapSize, bufferSize, sizeFix, partial, lightLevel, useWorldMap, lockedNorth, shape, ps, pc, cave, circleShape, settings, cvc);
        if (this.minimap.usingFBO()) {
            sizeFix = 1.0f;
        }
        this.matrixStack.method_22905(1.0f / mapScale, 1.0f / mapScale, 1.0f);
        int scaledX = (int)((float)x * mapScale);
        int scaledY = (int)((float)y * mapScale);
        int minimapFrameSize = (int)((float)(mapSize / 2) / sizeFix);
        int circleSides = Math.max(32, (int)Math.ceil(Math.PI * (double)(minimapFrameSize + 8) / 8.0 / 4.0) * 4);
        double circleSeamAngle = -0.7853981633974483;
        int circleSeamWidth = 32;
        int circleFrameThickness = 4;
        double circleStartAngle = 0.0;
        if (!circleShape) {
            this.getHelper().drawMyTexturedModalRect(this.matrixStack, (int)((float)(scaledX + 9) / sizeFix), (int)((float)(scaledY + 9) / sizeFix), 0, 256 - minimapFrameSize, minimapFrameSize, minimapFrameSize, minimapFrameSize, 256.0f);
        } else {
            float outerRadius = mapSize / 4 + circleFrameThickness;
            circleStartAngle = circleSeamAngle - (double)((float)(circleSeamWidth / 2) / outerRadius);
            this.getHelper().drawTexturedElipseInsideRectangle(this.matrixStack, circleStartAngle, circleSides, (int)((float)(scaledX + 9) / sizeFix), (int)((float)(scaledY + 9) / sizeFix), 0, 256 - minimapFrameSize, minimapFrameSize, 256.0f);
        }
        if (!this.minimap.usingFBO()) {
            this.matrixStack.method_22905(1.0f / sizeFix, 1.0f / sizeFix, 1.0f);
            ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        boolean bl = renderFrame = (frameType = settings.minimapFrame) < ModSettings.FRAME_OPTIONS.length - 1;
        if (frameType > 0) {
            int frameColor = ModSettings.COLORS[settings.minimapFrameColor];
            ImmediateRenderUtil.setShaderColor((float)(frameColor >> 16 & 0xFF) / 255.0f, (float)(frameColor >> 8 & 0xFF) / 255.0f, (float)(frameColor & 0xFF) / 255.0f, 1.0f);
        }
        MinimapRendererHelper helper = this.getHelper();
        if (renderFrame) {
            TextureUtils.setTexture(0, TextureLocations.MINIMAP_FRAME_TEXTURES);
        }
        if (renderFrame && !circleShape) {
            int rightCornerStartX = scaledX + 9 + mapSize / 2 + 4 - 16;
            int bottomCornerStartY = scaledY + 9 + mapSize / 2 + 4 - 16;
            class_289 tessellator = class_289.method_1348();
            class_287 vertexBuffer = tessellator.method_60827(VertexFormat.class_5596.field_27382, class_290.field_1585);
            Matrix4f matrix = this.matrixStack.method_23760().method_23761();
            int cornerTextureX = frameType == 0 ? 192 : (frameType == 1 ? 208 : 224);
            helper.addTexturedRectToExistingBuffer(matrix, (class_4588)vertexBuffer, scaledX + 9 - 4, scaledY + 9 - 4, cornerTextureX, 97, 16, 16);
            helper.addTexturedRectToExistingBuffer(matrix, (class_4588)vertexBuffer, rightCornerStartX, scaledY + 9 - 4, cornerTextureX, 113, 16, 16);
            helper.addTexturedRectToExistingBuffer(matrix, (class_4588)vertexBuffer, scaledX + 9 - 4, bottomCornerStartY, cornerTextureX, 129, 16, 16);
            helper.addTexturedRectToExistingBuffer(matrix, (class_4588)vertexBuffer, rightCornerStartX, bottomCornerStartY, cornerTextureX, 145, 16, 16);
            int horLineStartX = scaledX + 9 - 4 + 16;
            int horLineWidth = rightCornerStartX - horLineStartX;
            int horPieceTextureY = frameType == 0 ? 0 : (frameType == 1 ? 32 : 64);
            int horPieceWidth = 226;
            int horLineLength = (int)Math.ceil((double)horLineWidth / (double)horPieceWidth);
            for (int i = 0; i < horLineLength; ++i) {
                int pieceX = scaledX + 9 - 4 + 16 + i * horPieceWidth;
                int pieceW = horPieceWidth;
                if (i == horLineLength - 1 && pieceX + pieceW > rightCornerStartX) {
                    pieceW = rightCornerStartX - pieceX;
                }
                helper.addTexturedRectToExistingBuffer(matrix, (class_4588)vertexBuffer, pieceX, scaledY + 9 - 4, 0, horPieceTextureY, pieceW, 16);
                helper.addTexturedRectToExistingBuffer(matrix, (class_4588)vertexBuffer, pieceX, scaledY + 9 + mapSize / 2 - 12, 0, horPieceTextureY + 16, pieceW, 16);
            }
            int verLineStartY = scaledY + 9 - 4 + 16;
            int verLineHeight = bottomCornerStartY - verLineStartY;
            int verPieceTextureX = frameType == 0 ? 0 : (frameType == 1 ? 64 : 128);
            int verPieceHeight = 113;
            int vertLineLength = (int)Math.ceil((double)verLineHeight / (double)verPieceHeight);
            for (int i = 0; i < vertLineLength; ++i) {
                int pieceY = scaledY + 9 - 4 + 16 + i * verPieceHeight;
                int pieceU = verPieceTextureX + 32 * (i & 1);
                int pieceH = verPieceHeight;
                if (i == vertLineLength - 1 && pieceY + pieceH > bottomCornerStartY) {
                    pieceH = bottomCornerStartY - pieceY;
                }
                helper.addTexturedRectToExistingBuffer(matrix, (class_4588)vertexBuffer, scaledX + 9 - 4, pieceY, pieceU, 97, 16, pieceH);
                helper.addTexturedRectToExistingBuffer(matrix, (class_4588)vertexBuffer, scaledX + 9 + mapSize / 2 - 12, pieceY, pieceU + 16, 97, 16, pieceH);
            }
            ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.method_60794(), CustomRenderTypes.RP_POSITION_TEX_NO_ALPHA);
        } else if (renderFrame) {
            int frameTextureY = frameType == 0 ? 210 : (frameType == 1 ? 214 : 218);
            double shadeStartAngle = 0.7853981633974483 - circleStartAngle;
            int shadeStartIndex = (int)(shadeStartAngle / 2.0 / Math.PI * (double)circleSides);
            int circleLeftX = scaledX + 9;
            int circleTopY = scaledY + 9;
            int innerCircleDiameter = mapSize / 2;
            helper.drawTexturedElipseInsideRectangleFrame(this.matrixStack, false, false, circleStartAngle, 0, shadeStartIndex, circleSides, circleFrameThickness, circleLeftX, circleTopY, 0, frameTextureY, innerCircleDiameter, 73.0f, circleFrameThickness, circleSeamWidth, 256.0f);
            helper.drawTexturedElipseInsideRectangleFrame(this.matrixStack, true, false, circleStartAngle, shadeStartIndex, shadeStartIndex + circleSides / 4, circleSides, circleFrameThickness, circleLeftX, circleTopY, 138, frameTextureY, innerCircleDiameter, 68.0f, circleFrameThickness, 20, 256.0f);
            helper.drawTexturedElipseInsideRectangleFrame(this.matrixStack, true, true, circleStartAngle, shadeStartIndex + circleSides / 4, shadeStartIndex + circleSides / 2, circleSides, circleFrameThickness, circleLeftX, circleTopY, 138, frameTextureY, innerCircleDiameter, 68.0f, circleFrameThickness, 20, 256.0f);
            helper.drawTexturedElipseInsideRectangleFrame(this.matrixStack, false, false, circleStartAngle, shadeStartIndex + circleSides / 2, circleSides, circleSides, circleFrameThickness, circleLeftX, circleTopY, 0, frameTextureY, innerCircleDiameter, 73.0f, circleFrameThickness, circleSeamWidth, 256.0f);
        }
        TextureUtils.setTexture(0, TextureLocations.GUI_TEXTURES);
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.matrixStack.method_22903();
        this.matrixStack.method_46416((float)(scaledX + 9), (float)(scaledY + 9), 0.0f);
        this.matrixStack.method_22905(1.0f / minimapScale, 1.0f / minimapScale, 1.0f);
        int halfFrame = (int)((float)mapSize * minimapScale / 2.0f / 2.0f);
        this.matrixStack.method_22904((double)halfFrame, (double)halfFrame, 0.5);
        int specH = specW = halfFrame + (int)(3.0f * minimapScale);
        boolean safeMode = this instanceof MinimapSafeModeRenderer;
        class_4597.class_4598 renderTypeBuffers = this.modMain.getHudRenderer().getCustomVertexConsumers().getBetterPVPRenderTypeBuffers();
        MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers = minimapSession.getMultiTextureRenderTypeRenderers();
        double scaledZoom = this.zoom * (double)minimapScale / 2.0;
        if (!this.modMain.getSettings().compassOverEverything) {
            this.renderCompass(this.matrixStack, settings, renderTypeBuffers, specW, specH, halfFrame, ps, pc, circleShape, minimapScale);
        }
        this.minimap.getOverMapRendererHandler().prepareRender(ps, pc, scaledZoom, specW, specH, halfFrame, halfFrame, circleShape, minimapScale);
        this.minimap.getOverMapRendererHandler().render(renderPos, partial, null, mapDimensionScale, mapDimension);
        if (this.modMain.getSettings().compassOverEverything) {
            this.renderCompass(this.matrixStack, settings, renderTypeBuffers, specW, specH, halfFrame, ps, pc, circleShape, minimapScale);
        }
        renderTypeBuffers.method_22993();
        this.matrixStack.method_22909();
        int depthClearerX = scaledX - 25;
        int depthClearerY = scaledY - 25;
        int depthClearerW = 18 + mapSize / 2 + 50;
        CustomRenderTypes.DEPTH_CLEAR.method_23516();
        ImmediateRenderUtil.coloredRectangle(this.matrixStack.method_23760().method_23761(), depthClearerX, depthClearerY, depthClearerX + depthClearerW, depthClearerY + depthClearerW, -16777216, CustomRenderTypes.RP_DEPTH_CLEAR);
        CustomRenderTypes.DEPTH_CLEAR.method_23518();
        boolean bl2 = crosshairDisplayed = settings.mainEntityAs == 0 && !lockedNorth;
        if (crosshairDisplayed) {
            this.matrixStack.method_22903();
            this.matrixStack.method_46416((float)(scaledX + 9), (float)(scaledY + 9), 0.0f);
            this.matrixStack.method_22905(0.5f, 0.5f, 1.0f);
            this.matrixStack.method_46416((float)(mapSize / 2), (float)(mapSize / 2), 0.0f);
            ImmediateRenderUtil.negativeColorRectangle(this.matrixStack, -5.0f, -1.0f, 5.0f, 1.0f);
            ImmediateRenderUtil.negativeColorRectangle(this.matrixStack, -1.0f, 3.0f, 1.0f, 5.0f);
            ImmediateRenderUtil.negativeColorRectangle(this.matrixStack, -1.0f, -5.0f, 1.0f, -3.0f);
            RadarSession radarSession = minimap.getRadarSession();
            EntityRadarCategory mainEntityCategory = radarSession.getCategoryManager().getRuleResolver().resolve(radarSession.getCategoryManager().getRootCategory(), this.mc.method_1560(), this.mc.field_1724);
            if (mainEntityCategory == null) {
                mainEntityCategory = radarSession.getCategoryManager().getRootCategory();
            }
            RadarColor crosshairRadarColor = RadarColor.fromIndex(mainEntityCategory.getSettingValue(EntityRadarCategorySettings.COLOR).intValue());
            RadarColor crosshairFallbackColor = radarSession.getColorHelper().getFallbackColor(mainEntityCategory);
            int crosshairColor = radarSession.getColorHelper().getEntityColor(this.mc.method_1560(), 0.0f, false, 100, 100, false, crosshairRadarColor, crosshairFallbackColor);
            ImmediateRenderUtil.setShaderColor((float)(crosshairColor >> 16 & 0xFF) / 255.0f, (float)(crosshairColor >> 8 & 0xFF) / 255.0f, (float)(crosshairColor & 0xFF) / 255.0f, 1.0f);
            this.getHelper().drawMyColoredRect(this.matrixStack, 1.0f, -1.0f, 3.0f, 1.0f);
            this.getHelper().drawMyColoredRect(this.matrixStack, -3.0f, -1.0f, -1.0f, 1.0f);
            this.getHelper().drawMyColoredRect(this.matrixStack, -1.0f, 1.0f, 1.0f, 3.0f);
            this.getHelper().drawMyColoredRect(this.matrixStack, -1.0f, -3.0f, 1.0f, -1.0f);
            ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.matrixStack.method_22909();
        }
        double centerX = 2 * scaledX + 18 + mapSize / 2;
        double centerY = 2 * scaledY + 18 + mapSize / 2;
        this.matrixStack.method_22903();
        this.matrixStack.method_22905(0.5f, 0.5f, 1.0f);
        this.matrixStack.method_22904(centerX, centerY, 0.0);
        class_1044 guiTextures = this.mc.method_1531().method_4619(TextureLocations.GUI_TEXTURES);
        guiTextures.method_4527(true, false);
        class_1297 mainEntity = this.mc.method_1560();
        if (!safeMode && !crosshairDisplayed) {
            this.minimap.getMinimapFBORenderer().renderMainEntityDot(mainEntity, cave, cvc.getBetterPVPRenderTypeBuffers());
        }
        TextureUtils.setTexture(0, TextureLocations.GUI_TEXTURES);
        if (lockedNorth || settings.mainEntityAs == 2) {
            float b;
            float g;
            float r;
            guiTextures.method_4527(true, false);
            float arrowAngle = lockedNorth ? mainEntity.method_5705(partial) : 180.0f;
            float arrowOpacity = (float)settings.playerArrowOpacity / 100.0f;
            if (arrowOpacity == 1.0f) {
                this.drawArrow(this.matrixStack, arrowAngle, 0.0, 1.0, 0.0f, 0.0f, 0.0f, 0.5f, settings);
            }
            if (settings.arrowColour != -1) {
                float[] c = ModSettings.arrowColours[settings.arrowColour];
                r = c[0];
                g = c[1];
                b = c[2];
                a = c[3];
            } else {
                int rgb = minimap.getRadarSession().getColorHelper().getTeamColor((class_1297)(this.mc.field_1724 == null ? mainEntity : this.mc.field_1724));
                if (rgb != -1) {
                    r = (float)(rgb >> 16 & 0xFF) / 255.0f;
                    g = (float)(rgb >> 8 & 0xFF) / 255.0f;
                    b = (float)(rgb & 0xFF) / 255.0f;
                    a = 1.0f;
                } else {
                    float[] c = ModSettings.arrowColours[0];
                    r = c[0];
                    g = c[1];
                    b = c[2];
                    a = c[3];
                }
            }
            this.drawArrow(this.matrixStack, arrowAngle, 0.0, 0.0, r, g, b, a *= arrowOpacity, settings);
            guiTextures.method_4527(false, false);
        }
        this.matrixStack.method_22909();
        guiTextures.method_4527(false, false);
        int playerBlockX = OptimizedMath.myFloor(mainEntity.method_23317());
        int playerBlockY = OptimizedMath.myFloor(mainEntity.method_23318());
        int playerBlockZ = OptimizedMath.myFloor(mainEntity.method_23321());
        class_2338.class_2339 pos = this.mutableBlockPos.method_10103(playerBlockX, playerBlockY, playerBlockZ);
        this.minimap.getInfoDisplays().getRenderer().render(this.matrixStack, minimapSession, this.minimap, height, size, (class_2338)pos, scaledX, scaledY, mapScale, renderTypeBuffers);
        this.matrixStack.method_22909();
        this.mc.field_1773.method_71114().method_71034(class_308.class_11274.field_60027);
    }

    private void renderCompass(class_4587 matrixStack, ModSettings settings, class_4597.class_4598 renderTypeBuffers, int specW, int specH, int halfFrame, double ps, double pc, boolean circleShape, float minimapScale) {
        class_4588 nameBgBuilder = renderTypeBuffers.getBuffer(CustomRenderTypes.RADAR_NAME_BGS);
        int compassScale = settings.getCompassScale();
        if (compassScale <= 0) {
            int n = compassScale = settings.compassLocation == 1 ? (int)Math.ceil(minimapScale / 2.0f) : (int)minimapScale;
        }
        if (settings.compassLocation == 1) {
            if (class_310.method_1551().method_1573()) {
                compassScale *= 2;
            }
            halfFrame = (int)((float)halfFrame - 7.0f * minimapScale / 2.0f);
            this.compassRenderer.drawCompass(matrixStack, halfFrame - 3 * compassScale, halfFrame - 3 * compassScale, ps, pc, 1.0, circleShape, compassScale, true, renderTypeBuffers, nameBgBuilder);
        } else if (settings.compassLocation == 2) {
            this.compassRenderer.drawCompass(matrixStack, specW, specH, ps, pc, this.zoom, circleShape, compassScale, false, renderTypeBuffers, null);
        }
    }

    private void drawArrow(class_4587 matrixStack, float angle, double arrowX, double arrowY, float r, float g, float b, float a, ModSettings settings) {
        matrixStack.method_22903();
        matrixStack.method_22904(arrowX, arrowY, 0.0);
        OptimizedMath.rotatePose(matrixStack, angle, (Vector3fc)OptimizedMath.ZP);
        matrixStack.method_22905((float)(0.5 * settings.arrowScale), (float)(0.5 * settings.arrowScale), 1.0f);
        int offsetY = -6;
        int h = 28;
        boolean ty = false;
        matrixStack.method_46416(-13.0f, (float)offsetY, 0.0f);
        ImmediateRenderUtil.setShaderColor(r, g, b, a);
        GuiHelper.blit(matrixStack, 0, 0, 49.0f, (float)ty, 26, h);
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.method_22909();
    }

    public double getZoom() {
        return this.zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public float getSunBrightness(MinimapProcessor minimap, boolean lighting) {
        class_638 world = this.mc.field_1687;
        float sunBrightness = (world.method_23783(1.0f) - 0.2f) / 0.8f;
        float ambient = world.method_8597().comp_656() * 24.0f / 15.0f;
        if (ambient > 1.0f) {
            ambient = 1.0f;
        }
        return ambient + (1.0f - ambient) * class_3532.method_15363((float)sunBrightness, (float)0.0f, (float)1.0f);
    }

    public MinimapRendererHelper getHelper() {
        return this.helper;
    }
}

