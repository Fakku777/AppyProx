/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  xaero.hud.minimap.element.render.MinimapElementRenderProvider
 */
package xaero.map.mods.minimap.element;

import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderProvider;
import xaero.map.mods.minimap.element.MinimapElementRendererWrapper;

public class MinimapElementRenderProviderWrapper<E, C>
extends ElementRenderProvider<E, C> {
    private final MinimapElementRenderProvider<E, C> provider;

    public MinimapElementRenderProviderWrapper(MinimapElementRenderProvider<E, C> provider) {
        this.provider = provider;
    }

    @Override
    public void begin(ElementRenderLocation location, C context) {
        this.provider.begin(MinimapElementRendererWrapper.getRenderLocation(location), context);
    }

    @Override
    public boolean hasNext(ElementRenderLocation location, C context) {
        return this.provider.hasNext(MinimapElementRendererWrapper.getRenderLocation(location), context);
    }

    @Override
    public E setupContextAndGetNext(ElementRenderLocation location, C context) {
        return (E)this.provider.setupContextAndGetNext(MinimapElementRendererWrapper.getRenderLocation(location), context);
    }

    @Override
    public E getNext(ElementRenderLocation location, C context) {
        return (E)this.provider.getNext(MinimapElementRendererWrapper.getRenderLocation(location), context);
    }

    @Override
    public void end(ElementRenderLocation location, C context) {
        this.provider.end(MinimapElementRendererWrapper.getRenderLocation(location), context);
    }
}

