/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  xaero.hud.minimap.element.render.MinimapElementReader
 */
package xaero.map.mods.minimap.element;

import net.minecraft.class_310;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.map.element.render.ElementReader;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.mods.minimap.element.MinimapElementRendererWrapper;

public class MinimapElementReaderWrapper<E, C>
extends ElementReader<E, C, MinimapElementRendererWrapper<E, C>> {
    private final MinimapElementReader<E, C> reader;

    public MinimapElementReaderWrapper(MinimapElementReader<E, C> reader) {
        this.reader = reader;
    }

    @Override
    public boolean isHidden(E element, C context) {
        return this.reader.isHidden(element, context);
    }

    @Override
    public float getBoxScale(ElementRenderLocation location, E element, C context) {
        return this.reader.getBoxScale(MinimapElementRendererWrapper.getRenderLocation(location), element, context);
    }

    @Override
    public double getRenderX(E element, C context, float partialTicks) {
        return this.reader.getRenderX(element, context, partialTicks);
    }

    @Override
    public double getRenderZ(E element, C context, float partialTicks) {
        return this.reader.getRenderZ(element, context, partialTicks);
    }

    @Override
    public int getInteractionBoxLeft(E element, C context, float partialTicks) {
        return this.reader.getInteractionBoxLeft(element, context, partialTicks);
    }

    @Override
    public int getInteractionBoxRight(E element, C context, float partialTicks) {
        return this.reader.getInteractionBoxRight(element, context, partialTicks);
    }

    @Override
    public int getInteractionBoxTop(E element, C context, float partialTicks) {
        return this.reader.getInteractionBoxTop(element, context, partialTicks);
    }

    @Override
    public int getInteractionBoxBottom(E element, C context, float partialTicks) {
        return this.reader.getInteractionBoxBottom(element, context, partialTicks);
    }

    @Override
    public int getLeftSideLength(E element, class_310 mc) {
        return this.reader.getLeftSideLength(element, mc);
    }

    @Override
    public String getMenuName(E element) {
        return this.reader.getMenuName(element);
    }

    @Override
    public String getFilterName(E element) {
        return this.reader.getFilterName(element);
    }

    @Override
    public int getMenuTextFillLeftPadding(E element) {
        return this.reader.getMenuTextFillLeftPadding(element);
    }

    @Override
    public int getRightClickTitleBackgroundColor(E element) {
        return this.reader.getRightClickTitleBackgroundColor(element);
    }

    @Override
    public int getRenderBoxLeft(E element, C context, float partialTicks) {
        return this.reader.getRenderBoxLeft(element, context, partialTicks);
    }

    @Override
    public int getRenderBoxRight(E element, C context, float partialTicks) {
        return this.reader.getRenderBoxRight(element, context, partialTicks);
    }

    @Override
    public int getRenderBoxTop(E element, C context, float partialTicks) {
        return this.reader.getRenderBoxTop(element, context, partialTicks);
    }

    @Override
    public int getRenderBoxBottom(E element, C context, float partialTicks) {
        return this.reader.getRenderBoxBottom(element, context, partialTicks);
    }

    @Override
    public boolean isInteractable(ElementRenderLocation location, E element) {
        return this.reader.isInteractable(MinimapElementRendererWrapper.getRenderLocation(location), element);
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return this.reader.shouldScaleBoxWithOptionalScale();
    }
}

