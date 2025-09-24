/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597$class_4598
 */
package xaero.hud.minimap.element.render.over;

import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import xaero.common.HudMod;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.element.render.MinimapElementRendererHandler;

public final class MinimapElementOverMapRendererHandler
extends MinimapElementRendererHandler {
    private double ps;
    private double pc;
    private double zoom;
    private int halfViewW;
    private int halfViewH;
    private int specW;
    private int specH;
    private boolean circle;
    private float optionalScale;
    private final double[] partialTranslate;

    private MinimapElementOverMapRendererHandler(HudMod modMain, List<MinimapElementRenderer<?, ?>> renderers, MinimapElementGraphics guiGraphics, double[] partialTranslate) {
        super(modMain, renderers, MinimapElementRenderLocation.OVER_MINIMAP, 9800, guiGraphics);
        this.partialTranslate = partialTranslate;
    }

    public void prepareRender(double ps, double pc, double zoom, int specW, int specH, int halfViewW, int halfViewH, boolean circle, float minimapScale) {
        this.ps = ps;
        this.pc = pc;
        this.zoom = zoom;
        this.specW = specW;
        this.specH = specH;
        this.halfViewW = halfViewW;
        this.halfViewH = halfViewH;
        this.circle = circle;
        this.optionalScale = minimapScale;
    }

    @Override
    protected <E, RRC, RR extends MinimapElementRenderer<E, RRC>> boolean transformAndRenderForRenderer(E element, double elementX, double elementY, double elementZ, RR renderer, RRC context, int elementIndex, double optionalDepth, MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource) {
        class_4587 matrixStack = this.guiGraphics.pose();
        class_243 renderPos = renderInfo.renderPos;
        double offx = elementX - renderPos.field_1352;
        double offy = elementZ - renderPos.field_1350;
        matrixStack.method_22903();
        boolean outOfBounds = MinimapElementOverMapRendererHandler.translatePosition(matrixStack, this.specW, this.specH, this.halfViewW, this.halfViewH, this.ps, this.pc, offx, offy, this.zoom, this.circle, this.partialTranslate);
        boolean result = renderer.renderElement(element, false, outOfBounds, optionalDepth, this.optionalScale, this.partialTranslate[0], this.partialTranslate[1], renderInfo, this.guiGraphics, vanillaBufferSource);
        matrixStack.method_22909();
        return result;
    }

    @Override
    protected void beforeRender(MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource) {
    }

    @Override
    protected void afterRender(MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource) {
    }

    public static boolean translatePosition(class_4587 matrixStack, int specW, int specH, int halfViewW, int halfViewH, double ps, double pc, double offx, double offy, double zoom, boolean circle, double[] partialTranslate) {
        double X;
        boolean outOfBounds = false;
        double Y = (pc * offx + ps * offy) * zoom;
        double borderedX = X = (ps * offx - pc * offy) * zoom;
        double borderedY = Y;
        if (!circle) {
            if (borderedX > (double)specW) {
                borderedX = specW;
                borderedY = Y * (double)specW / X;
                outOfBounds = true;
            } else if (borderedX < (double)(-specW)) {
                borderedX = -specW;
                borderedY = -Y * (double)specW / X;
                outOfBounds = true;
            }
            if (borderedY > (double)specH) {
                borderedY = specH;
                borderedX = X * (double)specH / Y;
                outOfBounds = true;
            } else if (borderedY < (double)(-specH)) {
                borderedY = -specH;
                borderedX = -X * (double)specH / Y;
                outOfBounds = true;
            }
            if (!outOfBounds && (borderedX > (double)halfViewW || borderedX < (double)(-halfViewW) || borderedY > (double)halfViewH || borderedY < (double)(-halfViewH))) {
                outOfBounds = true;
            }
        } else {
            double distSquared = borderedX * borderedX + borderedY * borderedY;
            double maxDistSquared = specW * specW;
            if (distSquared > maxDistSquared) {
                double scaleDown = Math.sqrt(maxDistSquared / distSquared);
                borderedX *= scaleDown;
                borderedY *= scaleDown;
                outOfBounds = true;
            }
            if (!outOfBounds && distSquared > (double)(halfViewW * halfViewW)) {
                outOfBounds = true;
            }
        }
        long roundedX = Math.round(borderedX);
        long roundedY = Math.round(borderedY);
        partialTranslate[0] = borderedX - (double)roundedX;
        partialTranslate[1] = borderedY - (double)roundedY;
        matrixStack.method_46416((float)roundedX, (float)roundedY, 0.0f);
        return outOfBounds;
    }

    public static final class Builder
    extends MinimapElementRendererHandler.Builder<Builder> {
        @Override
        public MinimapElementOverMapRendererHandler build() {
            return (MinimapElementOverMapRendererHandler)super.build();
        }

        @Override
        protected MinimapElementOverMapRendererHandler buildInternally(List<MinimapElementRenderer<?, ?>> renderers, MinimapElementGraphics graphics) {
            return new MinimapElementOverMapRendererHandler(HudMod.INSTANCE, renderers, graphics, new double[2]);
        }

        @Override
        protected Builder setDefault() {
            super.setDefault();
            return this;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

