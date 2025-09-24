/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 */
package xaero.map.mods.pac.gui.claim.element;

import net.minecraft.class_310;
import xaero.map.element.render.ElementReader;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.gui.CursorBox;
import xaero.map.mods.pac.gui.claim.ClaimResultElement;
import xaero.map.mods.pac.gui.claim.element.ClaimResultElementRenderContext;
import xaero.map.mods.pac.gui.claim.element.ClaimResultElementRenderer;

public class ClaimResultElementRenderReader
extends ElementReader<ClaimResultElement, ClaimResultElementRenderContext, ClaimResultElementRenderer> {
    @Override
    public boolean isHidden(ClaimResultElement element, ClaimResultElementRenderContext context) {
        return false;
    }

    @Override
    public double getRenderX(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return (element.getLeft() + element.getRight() << 3) + 8;
    }

    @Override
    public double getRenderZ(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return (element.getTop() + element.getBottom() << 3) + 8;
    }

    @Override
    public int getInteractionBoxLeft(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return -14;
    }

    @Override
    public int getInteractionBoxRight(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return 14;
    }

    @Override
    public int getInteractionBoxTop(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return -14;
    }

    @Override
    public int getInteractionBoxBottom(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return 14;
    }

    @Override
    public int getRenderBoxLeft(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return -16;
    }

    @Override
    public int getRenderBoxRight(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return 16;
    }

    @Override
    public int getRenderBoxTop(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return -16;
    }

    @Override
    public int getRenderBoxBottom(ClaimResultElement element, ClaimResultElementRenderContext context, float partialTicks) {
        return 16;
    }

    @Override
    public int getLeftSideLength(ClaimResultElement element, class_310 mc) {
        return 0;
    }

    @Override
    public String getMenuName(ClaimResultElement element) {
        return "n/a";
    }

    @Override
    public String getFilterName(ClaimResultElement element) {
        return this.getMenuName(element);
    }

    @Override
    public int getMenuTextFillLeftPadding(ClaimResultElement element) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(ClaimResultElement element) {
        return 0;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }

    @Override
    public boolean isInteractable(ElementRenderLocation location, ClaimResultElement element) {
        return true;
    }

    @Override
    public CursorBox getTooltip(ClaimResultElement element, ClaimResultElementRenderContext context, boolean overMenu) {
        return element.getTooltip();
    }
}

