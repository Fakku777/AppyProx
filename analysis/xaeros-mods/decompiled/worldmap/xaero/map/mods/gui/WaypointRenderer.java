/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1060
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597$class_4598
 */
package xaero.map.mods.gui;

import net.minecraft.class_1060;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import xaero.map.WorldMap;
import xaero.map.animation.SlowingAnimation;
import xaero.map.element.MapElementGraphics;
import xaero.map.element.render.ElementRenderInfo;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderer;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.gui.GuiMap;
import xaero.map.icon.XaeroIcon;
import xaero.map.misc.Misc;
import xaero.map.mods.SupportXaeroMinimap;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointReader;
import xaero.map.mods.gui.WaypointRenderContext;
import xaero.map.mods.gui.WaypointRenderProvider;
import xaero.map.mods.gui.WaypointSymbolCreator;

public final class WaypointRenderer
extends ElementRenderer<Waypoint, WaypointRenderContext, WaypointRenderer> {
    private final SupportXaeroMinimap minimap;
    private final WaypointSymbolCreator symbolCreator;
    private ElementRenderInfo compatibleRenderInfo;

    private WaypointRenderer(WaypointRenderContext context, WaypointRenderProvider provider, WaypointReader reader, SupportXaeroMinimap minimap, WaypointSymbolCreator symbolCreator) {
        super(context, provider, reader);
        this.minimap = minimap;
        this.symbolCreator = symbolCreator;
    }

    public WaypointSymbolCreator getSymbolCreator() {
        return this.symbolCreator;
    }

    @Override
    public void renderElementShadow(Waypoint w, boolean hovered, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, MapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
        class_4587 matrixStack = guiGraphics.pose();
        matrixStack.method_22904(partialX, partialY, 0.0);
        matrixStack.method_22905(optionalScale * ((WaypointRenderContext)this.context).worldmapWaypointsScale, optionalScale * ((WaypointRenderContext)this.context).worldmapWaypointsScale, 1.0f);
        float visibilityAlpha = w.isDisabled() ? 0.3f : 1.0f;
        matrixStack.method_46416(-14.0f, -41.0f, 0.0f);
        MapRenderHelper.blitIntoExistingBuffer(matrixStack.method_23760().method_23761(), ((WaypointRenderContext)this.context).regularUIObjectConsumer, 0, 19, 0, 117, 41, 22, 0.0f, 0.0f, 0.0f, renderInfo.brightness * visibilityAlpha / ((WaypointRenderContext)this.context).worldmapWaypointsScale);
    }

    @Override
    public boolean shouldRender(ElementRenderLocation location, boolean shadow) {
        return WorldMap.settings.renderWaypoints && (!shadow || WorldMap.settings.waypointBackgrounds);
    }

    @Override
    public boolean renderElement(Waypoint w, boolean hovered, double optionalDepth, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, MapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
        int symbolFrameWidth;
        class_4587 matrixStack = guiGraphics.pose();
        boolean renderBackground = hovered || WorldMap.settings.waypointBackgrounds;
        matrixStack.method_22904(partialX, partialY, 0.0);
        matrixStack.method_22905(optionalScale * ((WaypointRenderContext)this.context).worldmapWaypointsScale, optionalScale * ((WaypointRenderContext)this.context).worldmapWaypointsScale, 1.0f);
        matrixStack.method_22903();
        float visibilityAlpha = w.isDisabled() ? 0.3f : 1.0f;
        int color = w.getColor();
        String symbol = w.getSymbol();
        int type = w.getType();
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        int flagU = 35;
        int flagV = 34;
        int flagW = 30;
        int flagH = 43;
        if (symbol.length() > 1) {
            flagU += 35;
            flagW += 13;
        }
        if (w.isTemporary()) {
            flagU += 83;
        }
        matrixStack.method_46416((float)(-flagW) / 2.0f, (float)(-flagH + 1), 0.0f);
        if (renderBackground) {
            class_1060 textureManager = class_310.method_1551().method_1531();
            MapRenderHelper.blitIntoMultiTextureRenderer(matrixStack.method_23760().method_23761(), ((WaypointRenderContext)this.context).uniqueTextureUIObjectRenderer, 0.0f, 0.0f, flagU, flagV, flagW, flagH, red * visibilityAlpha, green * visibilityAlpha, blue * visibilityAlpha, visibilityAlpha, textureManager.method_4619(WorldMap.guiTextures).method_68004());
        }
        matrixStack.method_22909();
        float oldDestAlpha = w.getDestAlpha();
        if (hovered) {
            w.setDestAlpha(255.0f);
        } else {
            w.setDestAlpha(0.0f);
        }
        if (oldDestAlpha != w.getDestAlpha()) {
            w.setAlphaAnim(new SlowingAnimation(w.getAlpha(), w.getDestAlpha(), 0.8, 1.0));
        }
        if (w.getAlphaAnim() != null) {
            w.setAlpha((float)w.getAlphaAnim().getCurrent());
        }
        float alpha = w.getAlpha();
        XaeroIcon symbolIcon = null;
        int symbolVerticalOffset = 0;
        int symbolWidth = 0;
        class_327 fontRenderer = class_310.method_1551().field_1772;
        int stringWidth = fontRenderer.method_1727(symbol);
        int n = symbolFrameWidth = stringWidth / 2 > 4 ? 62 : 32;
        if (type != 1 && alpha < 200.0f) {
            symbolVerticalOffset = 5;
            symbolWidth = (stringWidth - 1) * 3;
            symbolIcon = this.symbolCreator.getSymbolTexture(guiGraphics, symbol);
        } else if (type == 1) {
            symbolVerticalOffset = 3;
            symbolWidth = 27;
            symbolIcon = this.symbolCreator.getDeathSymbolTexture(guiGraphics);
        }
        if (symbolIcon != null) {
            matrixStack.method_22903();
            matrixStack.method_46416(-1.0f - (float)symbolWidth / 2.0f, (float)(62 + (renderBackground ? -43 + symbolVerticalOffset - 1 : -12)), 0.0f);
            matrixStack.method_22905(1.0f, -1.0f, 1.0f);
            MapRenderHelper.blitIntoMultiTextureRenderer(matrixStack.method_23760().method_23761(), ((WaypointRenderContext)this.context).uniqueTextureUIObjectRenderer, 0.0f, 0.0f, symbolIcon.getOffsetX() + 1, symbolIcon.getOffsetY() + 1, symbolFrameWidth, 62, visibilityAlpha, visibilityAlpha, visibilityAlpha, visibilityAlpha, symbolIcon.getTextureAtlas().getWidth(), symbolIcon.getTextureAtlas().getWidth(), symbolIcon.getTextureAtlas().getTextureId());
            matrixStack.method_22909();
        }
        if ((int)alpha > 0) {
            int tc = (int)alpha << 24 | 0xFFFFFF;
            String name = w.getName();
            int len = fontRenderer.method_1727(name);
            matrixStack.method_46416(0.0f, (float)(renderBackground ? -38 : -11), 0.0f);
            matrixStack.method_22905(3.0f, 3.0f, 1.0f);
            int bgLen = Math.max(len + 2, 10);
            MapRenderHelper.fillIntoExistingBuffer(matrixStack.method_23760().method_23761(), ((WaypointRenderContext)this.context).textBGConsumer, -bgLen / 2, -1, bgLen / 2, 9, red, green, blue, alpha / 255.0f);
            MapRenderHelper.fillIntoExistingBuffer(matrixStack.method_23760().method_23761(), ((WaypointRenderContext)this.context).textBGConsumer, -bgLen / 2, -1, bgLen / 2, 8, 0.0f, 0.0f, 0.0f, alpha / 255.0f * 200.0f / 255.0f);
            if ((int)alpha > 3) {
                matrixStack.method_46416(0.0f, 0.0f, 1.0f);
                Misc.drawNormalText(matrixStack, name, (float)(-(len - 1)) / 2.0f, 0.0f, tc, false, vanillaBufferSource);
            }
        }
        return false;
    }

    @Override
    public void preRender(ElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
        class_4597.class_4598 renderTypeBuffers = WorldMap.worldMapClientOnly.customVertexConsumers.getRenderTypeBuffers();
        ((WaypointRenderContext)this.context).regularUIObjectConsumer = renderTypeBuffers.getBuffer(CustomRenderTypes.GUI);
        class_310.method_1551().method_1531().method_4619(WorldMap.guiTextures).method_4527(true, false);
        ((WaypointRenderContext)this.context).textBGConsumer = renderTypeBuffers.getBuffer(CustomRenderTypes.MAP_ELEMENT_TEXT_BG);
        ((WaypointRenderContext)this.context).uniqueTextureUIObjectRenderer = rendererProvider.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.GUI_PREMULTIPLIED);
        ((WaypointRenderContext)this.context).deathpoints = this.minimap.getDeathpoints();
        class_310 mc = class_310.method_1551();
        ((WaypointRenderContext)this.context).userScale = mc.field_1755 != null && mc.field_1755 instanceof GuiMap ? ((GuiMap)mc.field_1755).getUserScale() : 1.0;
    }

    @Override
    public void postRender(ElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
        class_4597.class_4598 renderTypeBuffers = WorldMap.worldMapClientOnly.customVertexConsumers.getRenderTypeBuffers();
        rendererProvider.draw(((WaypointRenderContext)this.context).uniqueTextureUIObjectRenderer);
        renderTypeBuffers.method_22993();
        class_310.method_1551().method_1531().method_4619(WorldMap.guiTextures).method_4527(false, false);
    }

    @Override
    public int getOrder() {
        return 200;
    }

    @Override
    public boolean shouldBeDimScaled() {
        return false;
    }

    public static final class Builder {
        private SupportXaeroMinimap minimap;
        private WaypointSymbolCreator symbolCreator;

        private Builder() {
        }

        private Builder setDefault() {
            this.setMinimap(null);
            this.setSymbolCreator(null);
            return this;
        }

        public Builder setMinimap(SupportXaeroMinimap minimap) {
            this.minimap = minimap;
            return this;
        }

        public Builder setSymbolCreator(WaypointSymbolCreator symbolCreator) {
            this.symbolCreator = symbolCreator;
            return this;
        }

        public WaypointRenderer build() {
            if (this.minimap == null || this.symbolCreator == null) {
                throw new IllegalStateException();
            }
            return new WaypointRenderer(new WaypointRenderContext(), new WaypointRenderProvider(this.minimap), new WaypointReader(), this.minimap, this.symbolCreator);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

