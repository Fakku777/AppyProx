/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package xaero.hud.minimap.waypoint.render.world;

import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import xaero.common.HudMod;
import xaero.common.effect.Effects;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.gui.GuiMisc;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointUtil;
import xaero.common.misc.Misc;
import xaero.common.misc.OptimizedMath;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderContext;
import xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderProvider;
import xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderReader;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.render.util.RenderBufferUtil;

public final class WaypointWorldRenderer
extends MinimapElementRenderer<Waypoint, WaypointWorldRenderContext> {
    private Vector3f lookVector;
    private boolean temporaryWaypointsGlobal;
    private double waypointsDistance;
    private double waypointsDistanceMin;
    private int distanceSetting;
    private boolean displayShortDistances;
    private boolean dimensionScaleDistance;
    private double clampDepth;
    private int lookingAtAngle;
    private int lookingAtAngleVertical;
    private boolean keepWaypointNames;
    private int autoConvertWaypointDistanceToKmThreshold;
    private int waypointDistancePrecision;
    private float iconScale;
    private int distanceTextScale;
    private int nameScale;
    private int opacity;
    private float cameraAngleYaw;
    private float cameraAnglePitch;
    private String subWorldName;
    private MinimapRendererHelper helper;
    private class_327 fontRenderer;
    private class_4597.class_4598 minimapBufferSource;
    private class_4588 texturedIconConsumer;
    private class_4588 waypointBackgroundConsumer;

    private WaypointWorldRenderer(MinimapElementReader<Waypoint, WaypointWorldRenderContext> elementReader, WaypointWorldRenderProvider provider, WaypointWorldRenderContext context) {
        super(elementReader, provider, context);
    }

    @Override
    public boolean renderElement(Waypoint w, boolean highlighted, boolean outOfBounds, double optionalDepth, float optionalScale, double partialX, double partialY, MinimapElementRenderInfo renderInfo, MinimapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource) {
        double zFromEntity;
        double distanceFromEntity;
        double waypointPosDivider = renderInfo.backgroundCoordinateScale / ((WaypointWorldRenderContext)this.context).dimCoordinateScale;
        double wX = (double)w.getX(waypointPosDivider) + 0.5;
        double wZ = (double)w.getZ(waypointPosDivider) + 0.5;
        double offX = wX - renderInfo.renderPos.field_1352;
        double offY = (double)w.getY() + 1.0 - renderInfo.renderPos.field_1351;
        if (!w.isYIncluded()) {
            offY = renderInfo.renderEntityPos.field_1351 + 1.0 - renderInfo.renderPos.field_1351;
        }
        double offZ = wZ - renderInfo.renderPos.field_1350;
        double distance2D = Math.sqrt(offX * offX + offZ * offZ);
        if (this.waypointsDistanceMin != 0.0 && distance2D < this.waypointsDistanceMin) {
            return false;
        }
        double distanceScale = this.dimensionScaleDistance ? renderInfo.backgroundCoordinateScale : 1.0;
        double scaledDistance2D = distance2D * distanceScale;
        if (!(w.isDestination() || w.getPurpose() == WaypointPurpose.DEATH || w.isGlobal() || w.isTemporary() && this.temporaryWaypointsGlobal || this.waypointsDistance == 0.0 || !(scaledDistance2D > this.waypointsDistance))) {
            return false;
        }
        Vector3f lookVector = this.lookVector;
        double depth = offX * (double)lookVector.x() + offY * (double)lookVector.y() + offZ * (double)lookVector.z();
        double xFromEntity = wX - renderInfo.renderEntityPos.field_1352;
        double yFromEntity = (double)w.getY() - renderInfo.renderEntityPos.field_1351;
        if (!w.isYIncluded()) {
            yFromEntity = 0.0;
        }
        boolean usingNearbyDisplay = (distanceFromEntity = Math.sqrt(xFromEntity * xFromEntity + yFromEntity * yFromEntity + (zFromEntity = wZ - renderInfo.renderEntityPos.field_1350) * zFromEntity)) <= 20.0 && !this.displayShortDistances;
        boolean displayingDistance = !usingNearbyDisplay && highlighted;
        String distanceText = displayingDistance ? this.getDistanceText(distanceFromEntity) : null;
        String name = null;
        if (usingNearbyDisplay || displayingDistance && this.keepWaypointNames || !displayingDistance && w.getPurpose() == WaypointPurpose.DEATH) {
            name = w.getLocalizedName();
        }
        class_327 fontRenderer = this.fontRenderer;
        class_4597.class_4598 bufferSource = this.minimapBufferSource;
        float iconScale = this.iconScale;
        int nameScale = this.nameScale;
        int halfIconPixel = (int)iconScale / 2;
        class_4587 matrixStack = guiGraphics.pose();
        if (renderInfo.location == MinimapElementRenderLocation.IN_WORLD && depth < this.clampDepth) {
            float scale = (float)(this.clampDepth / depth);
            matrixStack.method_22905(scale, scale, 1.0f);
        }
        matrixStack.method_22904((double)halfIconPixel, 0.0, optionalDepth);
        this.renderIconWithLabels(w, highlighted, name, distanceText, this.subWorldName, iconScale, nameScale, this.distanceTextScale, fontRenderer, halfIconPixel, matrixStack, bufferSource);
        return true;
    }

    @Override
    public void preRender(MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        class_310 mc = class_310.method_1551();
        class_4184 activeRender = mc.field_1773.method_19418();
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorldManager manager = session.getWorldManager();
        MinimapWorld currentWorld = manager.getCurrentWorld();
        ModSettings settings = HudMod.INSTANCE.getSettings();
        this.lookVector = activeRender.method_19335().get(new Vector3f());
        this.cameraAngleYaw = activeRender.method_19330();
        this.cameraAnglePitch = activeRender.method_19329();
        double fov = ((Integer)mc.field_1690.method_41808().method_41753()).doubleValue();
        int screenWidth = mc.method_22683().method_4489();
        int screenHeight = mc.method_22683().method_4506();
        this.subWorldName = null;
        if (currentWorld != null && manager.getAutoWorld() != currentWorld) {
            this.subWorldName = "(" + currentWorld.getContainer().getSubName() + ")";
        }
        ((WaypointWorldRenderContext)this.context).dimCoordinateScale = session.getDimensionHelper().getDimCoordinateScale(manager.getCurrentWorld());
        ((WaypointWorldRenderContext)this.context).renderEntityPos = renderInfo.renderEntityPos;
        int displayMultipleWaypointInfo = settings.displayMultipleWaypointInfo;
        ((WaypointWorldRenderContext)this.context).onlyMainInfo = displayMultipleWaypointInfo == 0 || displayMultipleWaypointInfo == 1 && !renderInfo.renderEntity.method_5715();
        this.temporaryWaypointsGlobal = settings.temporaryWaypointsGlobal;
        this.waypointsDistance = settings.getMaxWaypointsDistance();
        this.waypointsDistanceMin = settings.waypointsDistanceMin;
        this.distanceSetting = settings.distance;
        this.displayShortDistances = settings.alwaysShowDistance;
        this.dimensionScaleDistance = settings.dimensionScaledMaxWaypointDistance;
        this.clampDepth = settings.getWaypointsClampDepth(fov, screenHeight);
        this.lookingAtAngle = class_3532.method_15340((int)settings.lookingAtAngle, (int)0, (int)180);
        this.lookingAtAngleVertical = class_3532.method_15340((int)settings.lookingAtAngleVertical, (int)0, (int)180);
        this.keepWaypointNames = settings.keepWaypointNames;
        this.autoConvertWaypointDistanceToKmThreshold = settings.autoConvertWaypointDistanceToKmThreshold;
        this.waypointDistancePrecision = settings.waypointDistancePrecision;
        this.iconScale = settings.getWaypointsIngameIconScale();
        this.distanceTextScale = (int)Math.ceil(settings.getWaypointsIngameDistanceScale());
        this.nameScale = settings.getWaypointsIngameNameScale();
        this.opacity = settings.waypointOpacityIngame;
        ((WaypointWorldRenderContext)this.context).interactionBoxTop = this.distanceSetting == 0 || this.lookingAtAngleVertical == 0 ? 0 : (this.distanceSetting == 2 || this.lookingAtAngleVertical >= 90 ? -screenHeight : -OptimizedMath.myFloor((double)(screenHeight / 2) * Math.tan(Math.toRadians(this.lookingAtAngleVertical)) / Math.tan(Math.toRadians(fov / 2.0))));
        double horizontalTan = Math.tan(Math.toRadians(fov / 2.0)) * (double)screenWidth / (double)screenHeight;
        int n = this.distanceSetting == 0 || this.lookingAtAngle == 0 ? 0 : (((WaypointWorldRenderContext)this.context).interactionBoxLeft = this.distanceSetting == 2 || this.lookingAtAngle >= 90 ? -screenWidth : -OptimizedMath.myFloor((double)(screenWidth / 2) * Math.tan(Math.toRadians(this.lookingAtAngle)) / horizontalTan));
        if (class_310.method_1551().method_1573()) {
            this.iconScale = (float)(Math.ceil(this.iconScale / 2.0f) * 2.0);
            this.distanceTextScale = (this.distanceTextScale + 1) / 2 * 2;
            this.nameScale = (this.nameScale + 1) / 2 * 2;
        }
        this.helper = HudMod.INSTANCE.getMinimap().getMinimapFBORenderer().getHelper();
        this.fontRenderer = mc.field_1772;
        vanillaBufferSource.method_22993();
        this.minimapBufferSource = HudMod.INSTANCE.getHudRenderer().getCustomVertexConsumers().getBetterPVPRenderTypeBuffers();
        this.waypointBackgroundConsumer = this.minimapBufferSource.getBuffer(CustomRenderTypes.COLORED_WAYPOINTS_BGS);
        this.texturedIconConsumer = this.minimapBufferSource.getBuffer(CustomRenderTypes.GUI);
    }

    @Override
    public void postRender(MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        this.minimapBufferSource.method_22993();
        ((WaypointWorldRenderContext)this.context).onlyMainInfo = false;
        ((WaypointWorldRenderContext)this.context).renderEntityPos = null;
        this.fontRenderer = null;
        this.minimapBufferSource = null;
        this.waypointBackgroundConsumer = null;
        this.texturedIconConsumer = null;
    }

    private void renderIconWithLabels(Waypoint w, boolean highlit, String name, String distanceText, String subWorldName, float iconScale, int nameScale, int distanceTextScale, class_327 fontRenderer, int halfIconPixel, class_4587 matrixStack, class_4597.class_4598 bufferSource) {
        matrixStack.method_22905(iconScale, iconScale, 1.0f);
        this.renderIcon(w, highlit, matrixStack, fontRenderer, bufferSource);
        matrixStack.method_22905(1.0f / iconScale, 1.0f / iconScale, 1.0f);
        matrixStack.method_46416((float)(-halfIconPixel), 0.0f, 0.0f);
        matrixStack.method_46416(0.0f, 2.0f, 0.0f);
        float labelAlpha = 0.3529412f;
        if ((distanceText != null || name != null) && subWorldName != null) {
            this.renderWaypointLabel(subWorldName, matrixStack, this.helper, fontRenderer, nameScale, labelAlpha);
            matrixStack.method_46416(0.0f, 2.0f, 0.0f);
        }
        if (name != null) {
            this.renderWaypointLabel(name, matrixStack, this.helper, fontRenderer, nameScale, labelAlpha);
        }
        matrixStack.method_46416(0.0f, 2.0f, 0.0f);
        if (distanceText != null) {
            this.renderWaypointLabel(distanceText, matrixStack, this.helper, fontRenderer, distanceTextScale, labelAlpha);
        }
    }

    private void renderIcon(Waypoint w, boolean highlit, class_4587 matrixStack, class_327 fontRenderer, class_4597.class_4598 bufferSource) {
        int color = w.getWaypointColor().getHex();
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        float alpha = 0.52274513f * (float)this.opacity / 100.0f;
        if (highlit && ((WaypointWorldRenderContext)this.context).onlyMainInfo) {
            alpha = Math.min(1.0f, alpha * 1.5f);
        }
        int initialsWidth = w.getPurpose() == WaypointPurpose.DEATH ? 7 : fontRenderer.method_1727(w.getInitials());
        int addedFrame = WaypointUtil.getAddedMinimapIconFrame(initialsWidth);
        this.renderColorBackground(matrixStack, addedFrame, red, green, blue, alpha, this.waypointBackgroundConsumer);
        if (w.getPurpose() == WaypointPurpose.DEATH) {
            this.renderTexturedIcon(matrixStack, addedFrame, 0, 78, 0.9882f, 0.9882f, 0.9882f, 1.0f, this.texturedIconConsumer);
            return;
        }
        Misc.drawNormalText(matrixStack, w.getInitials(), (float)(-initialsWidth / 2), -8.0f, -1, false, bufferSource);
    }

    private void renderColorBackground(class_4587 matrixStack, int addedFrame, float r, float g, float b, float a, class_4588 waypointBackgroundConsumer) {
        Matrix4f matrix = matrixStack.method_23760().method_23761();
        waypointBackgroundConsumer.method_22918(matrix, (float)(-5 - addedFrame), -9.0f, 0.0f).method_22915(r, g, b, a);
        waypointBackgroundConsumer.method_22918(matrix, (float)(-5 - addedFrame), 0.0f, 0.0f).method_22915(r, g, b, a);
        waypointBackgroundConsumer.method_22918(matrix, (float)(4 + addedFrame), 0.0f, 0.0f).method_22915(r, g, b, a);
        waypointBackgroundConsumer.method_22918(matrix, (float)(4 + addedFrame), -9.0f, 0.0f).method_22915(r, g, b, a);
    }

    private void renderTexturedIcon(class_4587 matrixStack, int addedFrame, int textureX, int textureY, float r, float g, float b, float a, class_4588 vertexBuffer) {
        float f = 0.00390625f;
        float f1 = 0.00390625f;
        Matrix4f matrix = matrixStack.method_23760().method_23761();
        vertexBuffer.method_22918(matrix, (float)(-5 - addedFrame), (float)(-9 - addedFrame), 0.0f).method_22915(r, g, b, a).method_22913((float)textureX * f, (float)textureY * f1);
        vertexBuffer.method_22918(matrix, (float)(-5 - addedFrame), (float)addedFrame, 0.0f).method_22915(r, g, b, a).method_22913((float)textureX * f, (float)(textureY + 9 + addedFrame * 2) * f1);
        vertexBuffer.method_22918(matrix, (float)(4 + addedFrame), (float)addedFrame, 0.0f).method_22915(r, g, b, a).method_22913((float)(textureX + 9 + addedFrame * 2) * f, (float)(textureY + 9 + addedFrame * 2) * f1);
        vertexBuffer.method_22918(matrix, (float)(4 + addedFrame), (float)(-9 - addedFrame), 0.0f).method_22915(r, g, b, a).method_22913((float)(textureX + 9 + addedFrame * 2) * f, (float)textureY * f1);
    }

    private void renderWaypointLabel(String label, class_4587 matrixStack, MinimapRendererHelper helper, class_327 fontRenderer, int labelScale, float bgAlpha) {
        int nameWidth = fontRenderer.method_1727(label);
        int backgroundWidth = nameWidth + 3;
        int halfBackgroundWidth = backgroundWidth / 2;
        int halfPixel = 0;
        if ((backgroundWidth & 1) != 0) {
            halfPixel = labelScale - labelScale / 2;
            matrixStack.method_46416((float)(-halfPixel), 0.0f, 0.0f);
        }
        matrixStack.method_22905((float)labelScale, (float)labelScale, 1.0f);
        RenderBufferUtil.addColoredRect(matrixStack.method_23760().method_23761(), this.waypointBackgroundConsumer, -halfBackgroundWidth, 0.0f, backgroundWidth, 9, 0.0f, 0.0f, 0.0f, bgAlpha);
        Misc.drawNormalText(matrixStack, label, (float)(-halfBackgroundWidth + 2), 1.0f, -1, false, this.minimapBufferSource);
        matrixStack.method_46416(0.0f, 9.0f, 0.0f);
        matrixStack.method_22905(1.0f / (float)labelScale, 1.0f / (float)labelScale, 1.0f);
        if ((backgroundWidth & 1) != 0) {
            matrixStack.method_46416((float)halfPixel, 0.0f, 0.0f);
        }
    }

    private String getDistanceText(double distanceFromEntity) {
        if (this.autoConvertWaypointDistanceToKmThreshold != -1 && distanceFromEntity >= (double)this.autoConvertWaypointDistanceToKmThreshold) {
            return GuiMisc.getFormat(this.waypointDistancePrecision).format(distanceFromEntity / 1000.0) + "km";
        }
        return GuiMisc.getFormat(this.waypointDistancePrecision).format(distanceFromEntity) + "m";
    }

    @Override
    public boolean shouldRender(MinimapElementRenderLocation location) {
        if (!HudMod.INSTANCE.getSettings().getShowIngameWaypoints()) {
            return false;
        }
        class_310 mc = class_310.method_1551();
        return mc.field_1724 != null && !Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_WAYPOINTS) && !Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_WAYPOINTS_HARMFUL);
    }

    @Override
    public int getOrder() {
        return 100;
    }

    public static final class Builder {
        private Builder() {
        }

        private Builder setDefault() {
            return this;
        }

        public WaypointWorldRenderer build() {
            WaypointWorldRenderContext context = new WaypointWorldRenderContext();
            return new WaypointWorldRenderer(new WaypointWorldRenderReader(context), new WaypointWorldRenderProvider(), context);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

