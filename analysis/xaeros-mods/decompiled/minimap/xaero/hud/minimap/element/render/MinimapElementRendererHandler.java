/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5321
 *  net.minecraft.class_746
 */
package xaero.hud.minimap.element.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5321;
import net.minecraft.class_746;
import xaero.common.HudMod;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.misc.OptimizedMath;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.module.MinimapSession;

public abstract class MinimapElementRendererHandler {
    private final HudMod modMain;
    protected final MinimapElementGraphics guiGraphics;
    private final List<MinimapElementRenderer<?, ?>> renderers;
    protected final MinimapElementRenderLocation location;
    private final int indexLimit;

    protected MinimapElementRendererHandler(HudMod modMain, List<MinimapElementRenderer<?, ?>> renderers, MinimapElementRenderLocation location, int indexLimit, MinimapElementGraphics guiGraphics) {
        this.modMain = modMain;
        this.guiGraphics = guiGraphics;
        this.renderers = renderers;
        this.location = location;
        this.indexLimit = indexLimit;
    }

    public void add(MinimapElementRenderer<?, ?> renderer) {
        this.renderers.add(renderer);
        Collections.sort(this.renderers);
    }

    public void render(class_243 renderPos, float partialTicks, class_276 framebuffer, double backgroundCoordinateScale, class_5321<class_1937> mapDimension) {
        class_310 mc = class_310.method_1551();
        class_1297 renderEntity = mc.method_1560();
        class_746 player = mc.field_1724;
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers = session.getMultiTextureRenderTypeRenderers();
        class_4597.class_4598 vanillaBufferSource = mc.method_22940().method_23000();
        boolean cave = session.getProcessor().isCaveModeDisplayed();
        MinimapElementRenderInfo renderInfo = new MinimapElementRenderInfo(this.location, renderEntity, (class_1657)player, renderPos, cave, partialTicks, framebuffer, backgroundCoordinateScale, mapDimension);
        class_4587 matrixStack = this.guiGraphics.pose();
        this.beforeRender(renderInfo, vanillaBufferSource);
        int indexLimit = this.getIndexLimit();
        for (int i = 0; i < this.renderers.size(); ++i) {
            MinimapElementRenderer<?, ?> renderer = this.renderers.get(i);
            int elementIndex = 0;
            elementIndex = this.renderForRenderer(renderer, elementIndex, multiTextureRenderTypeRenderers, indexLimit, renderInfo);
            matrixStack.method_22904(0.0, 0.0, this.getElementIndexDepth(elementIndex, indexLimit));
            if ((indexLimit -= elementIndex) >= 0) continue;
            indexLimit = 0;
        }
        this.afterRender(renderInfo, vanillaBufferSource);
        this.guiGraphics.flush();
    }

    protected <E, RRC, RR extends MinimapElementRenderer<E, RRC>> int renderForRenderer(RR renderer, int elementIndex, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers, int indexLimit, MinimapElementRenderInfo renderInfo) {
        MinimapElementRenderLocation location = this.location;
        if (!renderer.shouldRender(location)) {
            return elementIndex;
        }
        class_4597.class_4598 vanillaBufferSource = class_310.method_1551().method_22940().method_23000();
        MinimapElementReader elementReader = renderer.elementReader;
        MinimapElementRenderProvider provider = renderer.provider;
        Object context = renderer.context;
        renderer.preRender(renderInfo, vanillaBufferSource, multiTextureRenderTypeRenderers);
        provider.begin(location, context);
        while (provider.hasNext(location, context)) {
            double optionalDepth;
            Object element = provider.setupContextAndGetNext(location, context);
            if (element == null || elementReader.isHidden(element, context) || !this.transformAndRenderForRenderer(element, renderer, context, elementIndex, optionalDepth = this.getElementIndexDepth(elementIndex, indexLimit), renderInfo, vanillaBufferSource)) continue;
            ++elementIndex;
        }
        provider.end(location, context);
        renderer.postRender(renderInfo, vanillaBufferSource, multiTextureRenderTypeRenderers);
        return elementIndex;
    }

    protected <E, RRC, RR extends MinimapElementRenderer<E, RRC>> boolean transformAndRenderForRenderer(E element, RR renderer, RRC context, int elementIndex, double optionalDepth, MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource) {
        MinimapElementReader<E, RRC> elementReader = renderer.elementReader;
        double elementX = elementReader.getRenderX(element, context, renderInfo.partialTicks);
        double elementY = elementReader.getRenderY(element, context, renderInfo.partialTicks);
        double elementZ = elementReader.getRenderZ(element, context, renderInfo.partialTicks);
        double elementCoordinateScale = elementReader.getCoordinateScale(element, context, renderInfo);
        double coordinateMultiplier = elementCoordinateScale / renderInfo.backgroundCoordinateScale;
        if (coordinateMultiplier == 1.0) {
            return this.transformAndRenderForRenderer(element, elementX, elementY, elementZ, renderer, context, elementIndex, optionalDepth, renderInfo, vanillaBufferSource);
        }
        if (elementReader.shouldScalePartialCoordinates(element, context, renderInfo)) {
            elementX *= coordinateMultiplier;
            elementZ *= coordinateMultiplier;
        } else {
            int flooredRenderX = OptimizedMath.myFloor(elementX);
            int flooredRenderZ = OptimizedMath.myFloor(elementZ);
            elementX = (double)OptimizedMath.myFloor((double)flooredRenderX * coordinateMultiplier) + (elementX - (double)flooredRenderX);
            elementZ = (double)OptimizedMath.myFloor((double)flooredRenderZ * coordinateMultiplier) + (elementZ - (double)flooredRenderZ);
        }
        return this.transformAndRenderForRenderer(element, elementX, elementY, elementZ, renderer, context, elementIndex, optionalDepth, renderInfo, vanillaBufferSource);
    }

    protected double getElementIndexDepth(int elementIndex, int indexLimit) {
        return (double)(elementIndex >= indexLimit ? indexLimit : elementIndex) * 0.1;
    }

    public MinimapElementGraphics getGuiGraphics() {
        return this.guiGraphics;
    }

    protected int getIndexLimit() {
        return this.indexLimit;
    }

    protected abstract <E, RRC, RR extends MinimapElementRenderer<E, RRC>> boolean transformAndRenderForRenderer(E var1, double var2, double var4, double var6, RR var8, RRC var9, int var10, double var11, MinimapElementRenderInfo var13, class_4597.class_4598 var14);

    protected abstract void beforeRender(MinimapElementRenderInfo var1, class_4597.class_4598 var2);

    protected abstract void afterRender(MinimapElementRenderInfo var1, class_4597.class_4598 var2);

    public static abstract class Builder<B extends Builder<B>> {
        protected final B self = this;
        protected class_4587 poseStack;

        protected Builder() {
        }

        protected B setDefault() {
            this.setPoseStack(null);
            return this.self;
        }

        public B setPoseStack(class_4587 poseStack) {
            this.poseStack = poseStack;
            return this.self;
        }

        public MinimapElementRendererHandler build() {
            if (this.poseStack == null) {
                throw new IllegalStateException();
            }
            return this.buildInternally(new ArrayList(), new MinimapElementGraphics(this.poseStack, () -> class_310.method_1551().method_22940().method_23000()));
        }

        protected abstract MinimapElementRendererHandler buildInternally(List<MinimapElementRenderer<?, ?>> var1, MinimapElementGraphics var2);
    }
}

