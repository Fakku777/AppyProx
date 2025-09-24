/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597$class_4598
 */
package xaero.map.mods.pac.gui.claim.element;

import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import xaero.map.WorldMap;
import xaero.map.element.MapElementGraphics;
import xaero.map.element.render.ElementRenderInfo;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderer;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.mods.pac.gui.claim.ClaimResultElement;
import xaero.map.mods.pac.gui.claim.ClaimResultElementManager;
import xaero.map.mods.pac.gui.claim.element.ClaimResultElementRenderContext;
import xaero.map.mods.pac.gui.claim.element.ClaimResultElementRenderProvider;
import xaero.map.mods.pac.gui.claim.element.ClaimResultElementRenderReader;

public class ClaimResultElementRenderer
extends ElementRenderer<ClaimResultElement, ClaimResultElementRenderContext, ClaimResultElementRenderer> {
    private final ClaimResultElementManager manager;

    private ClaimResultElementRenderer(ClaimResultElementManager manager, ClaimResultElementRenderContext context, ClaimResultElementRenderProvider provider, ClaimResultElementRenderReader reader) {
        super(context, provider, reader);
        this.manager = manager;
    }

    @Override
    public void preRender(ElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
        class_4597.class_4598 renderTypeBuffers = WorldMap.worldMapClientOnly.customVertexConsumers.getRenderTypeBuffers();
        ((ClaimResultElementRenderContext)this.context).guiIconBuffer = renderTypeBuffers.getBuffer(CustomRenderTypes.GUI);
        class_310.method_1551().method_1531().method_4619(WorldMap.guiTextures).method_4527(true, false);
        ((ClaimResultElementRenderContext)this.context).toDelete.clear();
    }

    @Override
    public void postRender(ElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
        class_4597.class_4598 renderTypeBuffers = WorldMap.worldMapClientOnly.customVertexConsumers.getRenderTypeBuffers();
        renderTypeBuffers.method_22993();
        for (ClaimResultElement element : ((ClaimResultElementRenderContext)this.context).toDelete) {
            this.manager.remove(element);
        }
        class_310.method_1551().method_1531().method_4619(WorldMap.guiTextures).method_4527(false, false);
    }

    @Override
    public void renderElementShadow(ClaimResultElement element, boolean hovered, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, MapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
    }

    @Override
    public boolean renderElement(ClaimResultElement element, boolean hovered, double optionalDepth, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, MapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
        float b;
        float g;
        float r;
        class_4587 matrixStack = guiGraphics.pose();
        long time = System.currentTimeMillis();
        int iconScale = (int)Math.ceil(optionalScale);
        matrixStack.method_22904(partialX, partialY, 0.0);
        matrixStack.method_22905((float)iconScale, (float)iconScale, 1.0f);
        int iconU = element.hasPositive() ? 0 : 32;
        int iconV = 78;
        if (element.hasPositive() == element.hasNegative()) {
            r = 1.0f;
            g = 0.6666667f;
            b = 0.0f;
        } else if (element.hasPositive()) {
            r = 0.0f;
            g = 0.6666667f;
            b = 0.0f;
        } else {
            r = 0.8f;
            g = 0.1f;
            b = 0.1f;
        }
        MapRenderHelper.blitIntoExistingBuffer(matrixStack.method_23760().method_23761(), ((ClaimResultElementRenderContext)this.context).guiIconBuffer, -16.0f, -16.0f, iconU, iconV, 32, 32, 32, 32, r, g, b, 1.0f, 256, 256);
        if (hovered) {
            element.setFadeOutStartTime(time);
        }
        if (time - element.getFadeOutStartTime() > 3000L) {
            ((ClaimResultElementRenderContext)this.context).toDelete.add(element);
        }
        return false;
    }

    @Override
    public boolean shouldRender(ElementRenderLocation location, boolean pre) {
        return true;
    }

    @Override
    public int getOrder() {
        return 150;
    }

    public static final class Builder {
        private ClaimResultElementManager manager;

        private Builder() {
        }

        private Builder setDefault() {
            this.setManager(null);
            return this;
        }

        public Builder setManager(ClaimResultElementManager manager) {
            this.manager = manager;
            return this;
        }

        public ClaimResultElementRenderer build() {
            if (this.manager == null) {
                throw new IllegalStateException();
            }
            return new ClaimResultElementRenderer(this.manager, new ClaimResultElementRenderContext(), new ClaimResultElementRenderProvider(this.manager), new ClaimResultElementRenderReader());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

