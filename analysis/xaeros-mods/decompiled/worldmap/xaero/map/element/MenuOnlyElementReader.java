/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.element;

import xaero.map.element.MenuOnlyElementRenderer;
import xaero.map.element.render.ElementReader;

public abstract class MenuOnlyElementReader<E>
extends ElementReader<E, Object, MenuOnlyElementRenderer<E>> {
    @Override
    public boolean isHidden(E element, Object context) {
        return false;
    }

    @Override
    public double getRenderX(E element, Object context, float partialTicks) {
        return 0.0;
    }

    @Override
    public double getRenderZ(E element, Object context, float partialTicks) {
        return 0.0;
    }

    @Override
    public int getInteractionBoxLeft(E element, Object context, float partialTicks) {
        return 0;
    }

    @Override
    public int getInteractionBoxRight(E element, Object context, float partialTicks) {
        return 0;
    }

    @Override
    public int getInteractionBoxTop(E element, Object context, float partialTicks) {
        return 0;
    }

    @Override
    public int getInteractionBoxBottom(E element, Object context, float partialTicks) {
        return 0;
    }

    @Override
    public int getRenderBoxLeft(E element, Object context, float partialTicks) {
        return 0;
    }

    @Override
    public int getRenderBoxRight(E element, Object context, float partialTicks) {
        return 0;
    }

    @Override
    public int getRenderBoxTop(E element, Object context, float partialTicks) {
        return 0;
    }

    @Override
    public int getRenderBoxBottom(E element, Object context, float partialTicks) {
        return 0;
    }
}

