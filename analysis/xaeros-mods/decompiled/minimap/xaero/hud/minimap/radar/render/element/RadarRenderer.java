/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5321
 *  net.minecraft.class_746
 */
package xaero.hud.minimap.radar.render.element;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_5321;
import net.minecraft.class_746;
import xaero.common.HudMod;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.icon.XaeroIcon;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.misc.Misc;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.RadarSession;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.color.RadarColor;
import xaero.hud.minimap.radar.icon.RadarIconManager;
import xaero.hud.minimap.radar.render.element.RadarElementReader;
import xaero.hud.minimap.radar.render.element.RadarRenderContext;
import xaero.hud.minimap.radar.render.element.RadarRenderProvider;
import xaero.hud.minimap.radar.util.RadarUtils;
import xaero.hud.render.TextureLocations;
import xaero.hud.render.util.MultiTextureRenderUtil;
import xaero.hud.render.util.RenderBufferUtil;

public final class RadarRenderer
extends MinimapElementRenderer<class_1297, RadarRenderContext> {
    private final RadarIconManager radarIconManager;
    private final Minimap minimap;
    private RadarSession radarSession;
    private EntityRadarCategoryManager categoryManager;
    private EntityRadarCategory previousCategory;
    private double maxDistanceSquared;
    private double labelScale;
    private boolean smoothDots;
    private boolean debugEntityIcons;
    private boolean debugEntityVariantIds;
    private int dotsStyle;
    private int heightLimit;
    private boolean heightBasedFade;
    private int startFadingAt;
    private boolean displayNameWhenIconFails;
    private boolean alwaysNameTags;
    private RadarColor radarColor;
    private RadarColor fallbackColor;
    private int displayY;
    private int nameSettingForCategory;
    private boolean namesForCategory;
    private boolean name;
    private boolean iconsAllowed;
    private boolean labelsAllowed;
    private class_1921 dotsRenderType;
    private class_4597.class_4598 minimapBufferSource;
    private class_4588 dotsBufferBuilder;
    private class_4588 labelBgBuilder;
    private MultiTextureRenderTypeRenderer iconsRenderer;
    private MinimapRendererHelper helper;
    private final RadarRenderProvider radarRenderProvider;

    private RadarRenderer(RadarIconManager radarIconManager, Minimap minimap, RadarElementReader elementReader, RadarRenderProvider provider, RadarRenderContext context) {
        super(elementReader, provider, context);
        this.radarIconManager = radarIconManager;
        this.minimap = minimap;
        this.radarRenderProvider = provider;
    }

    @Override
    public void preRender(MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        this.radarIconManager.allowPrerender();
        ModSettings settings = HudMod.INSTANCE.getSettings();
        this.iconsAllowed = true;
        this.labelsAllowed = true;
        ((RadarRenderContext)this.context).reversedOrder = MinimapKeyMappings.REVERSE_ENTITY_RADAR.method_1434();
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        this.radarSession = session.getRadarSession();
        this.categoryManager = this.radarSession.getCategoryManager();
        this.previousCategory = null;
        this.labelScale = settings.getDotNameScale();
        this.smoothDots = settings.getSmoothDots();
        this.debugEntityIcons = settings.debugEntityIcons;
        this.debugEntityVariantIds = settings.debugEntityVariantIds;
        this.dotsStyle = settings.getDotsStyle();
        this.dotsRenderType = CustomRenderTypes.GUI;
        class_310.method_1551().method_1531().method_4619(TextureLocations.GUI_TEXTURES).method_4527(settings.getSmoothDots(), false);
        vanillaBufferSource.method_22993();
        this.minimapBufferSource = HudMod.INSTANCE.getHudRenderer().getCustomVertexConsumers().getBetterPVPRenderTypeBuffers();
        this.dotsBufferBuilder = null;
        this.labelBgBuilder = this.minimapBufferSource.getBuffer(CustomRenderTypes.RADAR_NAME_BGS);
        this.iconsRenderer = multiTextureRenderTypeRenderers.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.GUI);
        this.helper = HudMod.INSTANCE.getMinimap().getMinimapFBORenderer().getHelper();
        double playerDimDiv = renderInfo.backgroundCoordinateScale / renderInfo.renderEntityDimensionScale;
        this.maxDistanceSquared = RadarUtils.getMaxDistance(session.getProcessor(), settings.minimapShape == 1) * playerDimDiv * playerDimDiv;
    }

    @Override
    public boolean renderElement(class_1297 e, boolean highlighted, boolean outOfBounds, double optionalDepth, float optionalScale, double partialX, double partialY, MinimapElementRenderInfo renderInfo, MinimapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource) {
        double figureScale;
        if (renderInfo.location == MinimapElementRenderLocation.IN_MINIMAP) {
            double offX = e.method_23317() - renderInfo.renderEntityPos.field_1352;
            if (offX * offX > this.maxDistanceSquared) {
                return false;
            }
            double offY = e.method_23321() - renderInfo.renderEntityPos.field_1350;
            if (offY * offY > this.maxDistanceSquared) {
                return false;
            }
        }
        if (((RadarRenderContext)this.context).entityCategory == null) {
            EntityRadarCategory rootCategory = this.categoryManager.getRootCategory();
            ((RadarRenderContext)this.context).entityCategory = this.categoryManager.getRuleResolver().resolve(rootCategory, e, renderInfo.player);
            if (((RadarRenderContext)this.context).entityCategory == null) {
                return false;
            }
        }
        if (((RadarRenderContext)this.context).entityCategory != this.previousCategory) {
            this.setupRenderForCategory(((RadarRenderContext)this.context).entityCategory);
            this.previousCategory = ((RadarRenderContext)this.context).entityCategory;
        }
        this.setupRenderForEntity(e);
        if (e instanceof class_1657) {
            this.confirmTrackedPlayerRadarRender((class_1657)e);
        }
        class_1297 renderEntity = renderInfo.renderEntity;
        boolean cave = renderInfo.cave;
        float optionalScaleAdjust = renderInfo.location == MinimapElementRenderLocation.OVER_MINIMAP ? 0.5f : 1.0f;
        optionalScale *= optionalScaleAdjust;
        class_4587 matrixStack = guiGraphics.pose();
        matrixStack.method_22903();
        boolean icon = this.iconsAllowed && ((RadarRenderContext)this.context).icon;
        boolean name = this.name;
        if (highlighted && this.nameSettingForCategory > 0) {
            name = true;
        }
        XaeroIcon entityIcon = null;
        if (icon) {
            entityIcon = this.radarIconManager.get(e, (float)((RadarRenderContext)this.context).iconScale, this.debugEntityIcons, this.debugEntityVariantIds, guiGraphics, renderInfo.framebuffer);
        }
        if (entityIcon == RadarIconManager.DOT) {
            entityIcon = null;
            icon = false;
        }
        boolean usableIcon = entityIcon != null && entityIcon != RadarIconManager.FAILED;
        float offY = (float)(renderEntity.method_23318() - e.method_23318());
        int labelOffsetX = 0;
        int labelOffsetY = 0;
        matrixStack.method_22904(partialX, partialY, 0.0);
        if (usableIcon) {
            figureScale = ((RadarRenderContext)this.context).iconScale;
            this.renderIcon(entityIcon, optionalScale, figureScale, offY, cave, matrixStack);
        } else {
            boolean smooth = this.smoothDots;
            if (!smooth) {
                optionalScale = (float)Math.ceil(optionalScale);
            }
            double dotActualScale = optionalScale;
            figureScale = ((RadarRenderContext)this.context).dotScale;
            if (this.dotsStyle == 1) {
                if (!smooth) {
                    figureScale = (int)figureScale;
                }
                dotActualScale *= figureScale;
            }
            float dotOffset = this.renderDot(e, renderInfo.player, smooth, optionalScale, figureScale, offY, cave, matrixStack);
            if (!smooth) {
                double dotRadius = (double)(-dotOffset) * dotActualScale;
                double dotRadiusPartial = dotRadius - (double)((int)dotRadius);
                labelOffsetX = partialX - dotRadiusPartial <= -0.5 ? -1 : 0;
                int n = labelOffsetY = partialY - dotRadiusPartial < -0.5 ? -1 : 0;
            }
            if (icon && this.displayNameWhenIconFails && entityIcon == RadarIconManager.FAILED) {
                name = true;
            }
        }
        matrixStack.method_22909();
        if (!this.labelsAllowed) {
            return true;
        }
        if (!name && this.displayY <= 0) {
            return true;
        }
        matrixStack.method_22904((double)labelOffsetX, (double)(labelOffsetY += (int)Math.round((double)(usableIcon ? 11 : 5) * figureScale * (double)optionalScale)), optionalDepth + (double)0.1f);
        if (optionalScale < 1.0f) {
            optionalScale = 1.0f;
        }
        this.renderLabel(e, renderEntity, name, optionalScale, matrixStack);
        return true;
    }

    @Override
    public void postRender(MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        if (((RadarRenderContext)this.context).reversedOrder && this.dotsBufferBuilder != null) {
            this.minimapBufferSource.method_22994(this.dotsRenderType);
        }
        multiTextureRenderTypeRenderers.draw(this.iconsRenderer);
        if (!((RadarRenderContext)this.context).reversedOrder && this.dotsBufferBuilder != null) {
            this.minimapBufferSource.method_22994(this.dotsRenderType);
        }
        this.minimapBufferSource.method_22993();
        class_310.method_1551().method_1531().method_4619(TextureLocations.GUI_TEXTURES).method_4527(false, false);
        this.iconsRenderer = null;
        this.previousCategory = null;
    }

    private void renderIcon(XaeroIcon entityIcon, double optionalScale, double figureScale, float offY, boolean cave, class_4587 matrixStack) {
        double clampedScale = Math.max(1.0, figureScale * optionalScale);
        matrixStack.method_22905((float)clampedScale, (float)clampedScale, 1.0f);
        float brightness = !this.heightBasedFade ? 1.0f : this.radarSession.getColorHelper().getEntityHeightFade(offY, this.heightLimit, this.startFadingAt);
        float opacity = 1.0f;
        if (cave) {
            opacity = brightness;
            brightness = 1.0f;
        }
        MultiTextureRenderUtil.prepareTexturedColoredRect(matrixStack.method_23760().method_23761(), -31.0f, -31.0f, entityIcon.getOffsetX() + 1, entityIcon.getOffsetY() + 1, 62.0f, 62.0f, 62.0f, (float)entityIcon.getTextureAtlas().getWidth(), brightness, brightness, brightness, opacity, entityIcon.getTextureAtlas().getTextureId(), this.iconsRenderer);
    }

    private float renderDot(class_1297 e, class_1657 player, boolean smooth, float optionalScale, double figureScale, float offY, boolean cave, class_4587 matrixStack) {
        matrixStack.method_22905(optionalScale, optionalScale, 1.0f);
        int color = this.radarSession.getColorHelper().getEntityColor(e, offY, cave, this.heightLimit, this.startFadingAt, this.heightBasedFade, this.radarColor, this.fallbackColor);
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        int dotTextureX = 0;
        int dotTextureY = 0;
        int dotTextureW = 0;
        int dotTextureH = 0;
        float dotOffset = 0.0f;
        if (this.dotsStyle == 1) {
            if (smooth) {
                dotTextureX = 1;
                dotTextureY = 88;
            } else {
                dotTextureX = 9;
                dotTextureY = 77;
            }
            dotOffset = -3.5f;
            dotTextureH = 8;
            dotTextureW = 8;
            matrixStack.method_22905((float)figureScale, (float)figureScale, 1.0f);
        } else {
            switch (((RadarRenderContext)this.context).dotSize) {
                case 1: {
                    dotOffset = -4.5f;
                    dotTextureY = 108;
                    dotTextureH = 9;
                    dotTextureW = 9;
                    break;
                }
                case 3: {
                    dotOffset = -7.5f;
                    dotTextureY = 128;
                    dotTextureH = 15;
                    dotTextureW = 15;
                    break;
                }
                case 4: {
                    dotOffset = -10.5f;
                    dotTextureY = 160;
                    dotTextureH = 21;
                    dotTextureW = 21;
                    break;
                }
                default: {
                    dotOffset = -5.5f;
                    dotTextureY = 117;
                    dotTextureH = 11;
                    dotTextureW = 11;
                }
            }
        }
        if (this.dotsBufferBuilder == null) {
            this.dotsBufferBuilder = this.minimapBufferSource.getBuffer(this.dotsRenderType);
        }
        RenderBufferUtil.addTexturedColoredRect(matrixStack.method_23760().method_23761(), this.dotsBufferBuilder, dotOffset, dotOffset, dotTextureX, dotTextureY, dotTextureW, dotTextureH, r, g, b, a, 256.0f);
        return dotOffset;
    }

    private void renderLabel(class_1297 e, class_1297 renderEntity, boolean name, double optionalScale, class_4587 matrixStack) {
        double dotNameScale = this.labelScale * optionalScale;
        matrixStack.method_22905((float)dotNameScale, (float)dotNameScale, 1.0f);
        Object yValueString = null;
        if (this.displayY > 0) {
            int yInt = (int)Math.floor(e.method_23318());
            int pYInt = (int)Math.floor(renderEntity.method_23318());
            yValueString = this.displayY == 1 ? "" + yInt : (this.displayY == 2 ? "" + (yInt - pYInt) : "");
            if (((String)(yValueString = (String)yValueString + (yInt > pYInt ? "\u2191" : (yInt != pYInt ? "\u2193" : "")))).length() == 0) {
                yValueString = "-";
            }
        }
        class_327 font = class_310.method_1551().field_1772;
        Object label = null;
        if (name) {
            class_2561 component = Misc.getFixedDisplayName(e);
            if (component == null) {
                return;
            }
            label = component.getString();
            if (this.displayY > 0) {
                label = (String)label + "(" + (String)yValueString + ")";
            }
        } else if (this.displayY > 0) {
            label = yValueString;
        }
        if (label == null) {
            return;
        }
        int labelW = font.method_1727((String)label);
        RenderBufferUtil.addColoredRect(matrixStack.method_23760().method_23761(), this.labelBgBuilder, -labelW / 2 - 2, -1.0f, labelW + 3, 10, 0.0f, 0.0f, 0.0f, 0.3529412f);
        Misc.drawNormalText(matrixStack, (String)label, (float)(-labelW / 2), 0.0f, -1, false, this.minimapBufferSource);
    }

    private void setupRenderForCategory(EntityRadarCategory entityCategory) {
        if (!this.radarRenderProvider.isUsed()) {
            this.radarRenderProvider.setupContextForCategory(entityCategory, (RadarRenderContext)this.context);
        }
        this.heightLimit = entityCategory.getSettingValue(EntityRadarCategorySettings.HEIGHT_LIMIT).intValue();
        this.heightBasedFade = entityCategory.getSettingValue(EntityRadarCategorySettings.HEIGHT_FADE);
        this.startFadingAt = entityCategory.getSettingValue(EntityRadarCategorySettings.START_FADING_AT).intValue();
        this.displayNameWhenIconFails = entityCategory.getSettingValue(EntityRadarCategorySettings.ICON_NAME_FALLBACK);
        this.alwaysNameTags = entityCategory.getSettingValue(EntityRadarCategorySettings.ALWAYS_NAMETAGS);
        this.radarColor = RadarColor.fromIndex(entityCategory.getSettingValue(EntityRadarCategorySettings.COLOR).intValue());
        this.fallbackColor = this.radarSession.getColorHelper().getFallbackColor(entityCategory);
        this.displayY = entityCategory.getSettingValue(EntityRadarCategorySettings.DISPLAY_Y).intValue();
        this.nameSettingForCategory = entityCategory.getSettingValue(EntityRadarCategorySettings.NAMES).intValue();
        this.namesForCategory = this.nameSettingForCategory == 1 && ((RadarRenderContext)this.context).playerListDown || this.nameSettingForCategory == 2;
    }

    private void setupRenderForEntity(class_1297 entity) {
        boolean name;
        if (!this.radarRenderProvider.isUsed()) {
            this.radarRenderProvider.setupContextForEntity(entity, (RadarRenderContext)this.context);
        }
        if (!(name = this.namesForCategory) && !(entity instanceof class_1657)) {
            name = this.alwaysNameTags && entity.method_16914();
        }
        this.name = name;
    }

    private void confirmTrackedPlayerRadarRender(class_1657 e) {
        if (HudMod.INSTANCE.getTrackedPlayerRenderer().getCollector().playerExists(e.method_5667())) {
            HudMod.INSTANCE.getTrackedPlayerRenderer().getCollector().confirmPlayerRadarRender(e);
        }
        if (!HudMod.INSTANCE.getSupportMods().worldmap()) {
            return;
        }
        HudMod.INSTANCE.getSupportMods().worldmapSupport.confirmPlayerRadarRender(e);
    }

    public void renderSingleEntity(class_1297 entity, boolean cave, boolean highlighted, float optionalScale, boolean allowIcon, boolean allowLabel, MinimapElementRenderLocation location, class_276 defaultFramebuffer, MinimapElementGraphics guiGraphics) {
        ((RadarRenderContext)this.context).entityCategory = null;
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        class_746 player = class_310.method_1551().field_1724;
        MinimapElementRenderInfo renderInfo = new MinimapElementRenderInfo(location, entity, (class_1657)player, entity.method_19538(), cave, 1.0f, defaultFramebuffer, 1.0, (class_5321<class_1937>)entity.method_37908().method_27983());
        MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers = session.getMultiTextureRenderTypeRenderers();
        class_4597.class_4598 vanillaBufferSource = class_310.method_1551().method_22940().method_23000();
        this.preRender(renderInfo, vanillaBufferSource, multiTextureRenderTypeRenderers);
        this.iconsAllowed = allowIcon;
        this.labelsAllowed = allowLabel;
        this.renderElement(entity, highlighted, false, 0.0, optionalScale, 0.0, 0.0, renderInfo, guiGraphics, vanillaBufferSource);
        this.postRender(renderInfo, vanillaBufferSource, multiTextureRenderTypeRenderers);
    }

    @Override
    public boolean shouldRender(MinimapElementRenderLocation location) {
        if (!this.minimap.usingFBO()) {
            return false;
        }
        if (location == MinimapElementRenderLocation.WORLD_MAP) {
            return true;
        }
        if (location == MinimapElementRenderLocation.WORLD_MAP_MENU) {
            return true;
        }
        return HudMod.INSTANCE.getSettings().getEntityRadar();
    }

    public static final class Builder {
        private RadarIconManager radarIconManager;
        private Minimap minimap;

        private Builder() {
        }

        public Builder setDefault() {
            this.setRadarIconManager(null);
            return this;
        }

        public Builder setRadarIconManager(RadarIconManager radarIconManager) {
            this.radarIconManager = radarIconManager;
            return this;
        }

        public Builder setMinimap(Minimap minimap) {
            this.minimap = minimap;
            return this;
        }

        public RadarRenderer build() {
            if (this.radarIconManager == null || this.minimap == null) {
                throw new IllegalStateException();
            }
            RadarElementReader elementReader = new RadarElementReader();
            RadarRenderProvider provider = new RadarRenderProvider();
            RadarRenderContext context = new RadarRenderContext();
            return new RadarRenderer(this.radarIconManager, this.minimap, elementReader, provider, context);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

