/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_640
 */
package xaero.map.radar.tracker;

import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_640;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.WorldMapSession;
import xaero.map.animation.SlowingAnimation;
import xaero.map.element.MapElementGraphics;
import xaero.map.element.render.ElementRenderInfo;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderer;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.icon.XaeroIcon;
import xaero.map.icon.XaeroIconAtlas;
import xaero.map.radar.tracker.PlayerTrackerMapElement;
import xaero.map.radar.tracker.PlayerTrackerMapElementCollector;
import xaero.map.radar.tracker.PlayerTrackerMapElementReader;
import xaero.map.radar.tracker.PlayerTrackerMapElementRenderContext;
import xaero.map.radar.tracker.PlayerTrackerMapElementRenderProvider;
import xaero.map.radar.tracker.TrackedPlayerIconManager;

public final class PlayerTrackerMapElementRenderer
extends ElementRenderer<PlayerTrackerMapElement<?>, PlayerTrackerMapElementRenderContext, PlayerTrackerMapElementRenderer> {
    private final PlayerTrackerMapElementCollector elementCollector;
    private TrackedPlayerIconManager trackedPlayerIconManager;

    private PlayerTrackerMapElementRenderer(PlayerTrackerMapElementCollector elementCollector, PlayerTrackerMapElementRenderContext context, PlayerTrackerMapElementRenderProvider<PlayerTrackerMapElementRenderContext> provider, PlayerTrackerMapElementReader reader) {
        super(context, provider, reader);
        this.elementCollector = elementCollector;
    }

    public TrackedPlayerIconManager getTrackedPlayerIconManager() {
        return this.trackedPlayerIconManager;
    }

    @Override
    public void preRender(ElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
        class_310 mc = class_310.method_1551();
        WorldMapSession mapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = mapSession.getMapProcessor();
        class_4597.class_4598 renderTypeBuffers = WorldMap.worldMapClientOnly.customVertexConsumers.getRenderTypeBuffers();
        ((PlayerTrackerMapElementRenderContext)this.context).textBGConsumer = renderTypeBuffers.getBuffer(CustomRenderTypes.MAP_ELEMENT_TEXT_BG);
        ((PlayerTrackerMapElementRenderContext)this.context).uniqueTextureUIObjectRenderer = rendererProvider.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.GUI_PREMULTIPLIED);
        ((PlayerTrackerMapElementRenderContext)this.context).mapDimId = mapProcessor.getMapWorld().getCurrentDimensionId();
        ((PlayerTrackerMapElementRenderContext)this.context).mapDimDiv = mapProcessor.getMapWorld().getCurrentDimension().calculateDimDiv(mapProcessor.getWorldDimensionTypeRegistry(), mc.field_1687.method_8597());
    }

    @Override
    public void postRender(ElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
        class_4597.class_4598 renderTypeBuffers = WorldMap.worldMapClientOnly.customVertexConsumers.getRenderTypeBuffers();
        rendererProvider.draw(((PlayerTrackerMapElementRenderContext)this.context).uniqueTextureUIObjectRenderer);
        renderTypeBuffers.method_22993();
        if (!shadow) {
            this.elementCollector.resetRenderedOnRadarFlags();
        }
    }

    @Override
    public void renderElementShadow(PlayerTrackerMapElement<?> element, boolean hovered, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, MapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
    }

    @Override
    public boolean renderElement(PlayerTrackerMapElement<?> e, boolean hovered, double optionalDepth, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, MapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
        class_4587 matrixStack = guiGraphics.pose();
        class_640 info = class_310.method_1551().method_1562().method_2871(e.getPlayerId());
        if (info != null) {
            boolean firstTime;
            class_310 mc = class_310.method_1551();
            class_327 fontRenderer = mc.field_1772;
            class_1657 clientPlayer = mc.field_1687.method_18470(e.getPlayerId());
            matrixStack.method_22903();
            double fadeDest = hovered ? 1.0 : 0.0;
            boolean bl = firstTime = e.getFadeAnim() == null;
            if (firstTime || e.getFadeAnim().getDestination() != fadeDest) {
                e.setFadeAnim(new SlowingAnimation(e.getFadeAnim() == null ? 0.0 : e.getFadeAnim().getCurrent(), fadeDest, 0.8, 0.001));
            }
            float alpha = (float)e.getFadeAnim().getCurrent();
            if (!e.wasRenderedOnRadar() || alpha > 0.0f) {
                if (alpha > 0.0f) {
                    matrixStack.method_22903();
                    matrixStack.method_22905(2.0f, 2.0f, 1.0f);
                    String name = info.method_2966().getName();
                    int nameWidth = fontRenderer.method_1727(name);
                    MapRenderHelper.fillIntoExistingBuffer(matrixStack.method_23760().method_23761(), ((PlayerTrackerMapElementRenderContext)this.context).textBGConsumer, -8 - nameWidth - 2, -6, -7, 6, 0.0f, 0.0f, 0.0f, alpha * 119.0f / 255.0f);
                    int textAlphaComponent = (int)(alpha * 255.0f);
                    if (textAlphaComponent > 3) {
                        int tc = 0xFFFFFF | textAlphaComponent << 24;
                        guiGraphics.drawString(fontRenderer, name, -8 - nameWidth, -4, tc);
                    }
                    matrixStack.method_22909();
                }
                matrixStack.method_22904(partialX, partialY, 0.0);
                matrixStack.method_22905((2.0f + alpha) / 3.0f, (2.0f + alpha) / 3.0f, 1.0f);
                XaeroIcon icon = this.getTrackedPlayerIconManager().getIcon(guiGraphics, clientPlayer, info, e);
                XaeroIconAtlas atlas = icon.getTextureAtlas();
                MapRenderHelper.blitIntoMultiTextureRenderer(matrixStack.method_23760().method_23761(), ((PlayerTrackerMapElementRenderContext)this.context).uniqueTextureUIObjectRenderer, -15.0f, -15.0f, icon.getOffsetX() + 1, icon.getOffsetY() + 31, 30, 30, 30, -30, 1.0f, 1.0f, 1.0f, 1.0f, atlas.getWidth(), atlas.getWidth(), atlas.getTextureId());
            }
            matrixStack.method_22909();
        }
        return false;
    }

    @Override
    public boolean shouldRender(ElementRenderLocation location, boolean shadow) {
        return WorldMap.settings.trackedPlayers;
    }

    @Override
    public int getOrder() {
        return 200;
    }

    public PlayerTrackerMapElementCollector getCollector() {
        return this.elementCollector;
    }

    public void update(class_310 mc) {
        if (this.trackedPlayerIconManager == null) {
            this.trackedPlayerIconManager = TrackedPlayerIconManager.Builder.begin().build();
        }
        this.elementCollector.update(mc);
    }

    public static final class Builder {
        private Builder() {
        }

        private Builder setDefault() {
            return this;
        }

        public PlayerTrackerMapElementRenderer build() {
            PlayerTrackerMapElementCollector collector = new PlayerTrackerMapElementCollector(WorldMap.playerTrackerSystemManager, () -> WorldMap.trackedPlayerMenuRenderer.updateFilteredList());
            return new PlayerTrackerMapElementRenderer(collector, new PlayerTrackerMapElementRenderContext(), new PlayerTrackerMapElementRenderProvider<PlayerTrackerMapElementRenderContext>(collector), new PlayerTrackerMapElementReader());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

