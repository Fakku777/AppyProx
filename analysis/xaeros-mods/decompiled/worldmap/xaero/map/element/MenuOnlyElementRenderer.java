/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4597$class_4598
 */
package xaero.map.element;

import net.minecraft.class_4597;
import xaero.map.element.MapElementGraphics;
import xaero.map.element.MenuOnlyElementReader;
import xaero.map.element.render.ElementRenderInfo;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderer;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;

public final class MenuOnlyElementRenderer<E>
extends ElementRenderer<E, Object, MenuOnlyElementRenderer<E>> {
    protected MenuOnlyElementRenderer(MenuOnlyElementReader<E> reader) {
        super(null, null, reader);
    }

    @Override
    public boolean shouldRender(ElementRenderLocation location, boolean shadow) {
        return false;
    }

    @Override
    public void preRender(ElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
    }

    @Override
    public void postRender(ElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
    }

    @Override
    public void renderElementShadow(E element, boolean hovered, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, MapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
    }

    @Override
    public boolean renderElement(E element, boolean hovered, double optionalDepth, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, MapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
        return false;
    }
}

