/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4597$class_4598
 */
package xaero.map.element.render;

import net.minecraft.class_4597;
import xaero.map.element.MapElementGraphics;
import xaero.map.element.render.ElementReader;
import xaero.map.element.render.ElementRenderInfo;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderProvider;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;

public abstract class ElementRenderer<E, C, R extends ElementRenderer<E, C, R>>
implements Comparable<ElementRenderer<?, ?, ?>> {
    protected final R self = this;
    protected final ElementReader<E, C, R> reader;
    protected final C context;
    protected final ElementRenderProvider<E, C> provider;

    protected ElementRenderer(C context, ElementRenderProvider<E, C> provider, ElementReader<E, C, R> reader) {
        this.context = context;
        this.provider = provider;
        this.reader = reader;
    }

    public boolean shouldRenderHovered(boolean pre) {
        return true;
    }

    public ElementReader<E, C, R> getReader() {
        return this.reader;
    }

    public C getContext() {
        return this.context;
    }

    public ElementRenderProvider<E, C> getProvider() {
        return this.provider;
    }

    public int getOrder() {
        return 0;
    }

    public boolean shouldBeDimScaled() {
        return true;
    }

    @Override
    public int compareTo(ElementRenderer<?, ?, ?> o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }

    public abstract void preRender(ElementRenderInfo var1, class_4597.class_4598 var2, MultiTextureRenderTypeRendererProvider var3, boolean var4);

    public abstract void postRender(ElementRenderInfo var1, class_4597.class_4598 var2, MultiTextureRenderTypeRendererProvider var3, boolean var4);

    public abstract void renderElementShadow(E var1, boolean var2, float var3, double var4, double var6, ElementRenderInfo var8, MapElementGraphics var9, class_4597.class_4598 var10, MultiTextureRenderTypeRendererProvider var11);

    public abstract boolean renderElement(E var1, boolean var2, double var3, float var5, double var6, double var8, ElementRenderInfo var10, MapElementGraphics var11, class_4597.class_4598 var12, MultiTextureRenderTypeRendererProvider var13);

    public abstract boolean shouldRender(ElementRenderLocation var1, boolean var2);
}

