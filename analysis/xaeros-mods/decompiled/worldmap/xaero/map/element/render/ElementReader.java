/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 */
package xaero.map.element.render;

import java.util.ArrayList;
import net.minecraft.class_310;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderer;
import xaero.map.gui.CursorBox;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.RightClickOption;

public abstract class ElementReader<E, C, R extends ElementRenderer<E, ?, R>> {
    public abstract boolean isHidden(E var1, C var2);

    public abstract double getRenderX(E var1, C var2, float var3);

    public abstract double getRenderZ(E var1, C var2, float var3);

    public abstract int getInteractionBoxLeft(E var1, C var2, float var3);

    public abstract int getInteractionBoxRight(E var1, C var2, float var3);

    public abstract int getInteractionBoxTop(E var1, C var2, float var3);

    public abstract int getInteractionBoxBottom(E var1, C var2, float var3);

    public abstract int getRenderBoxLeft(E var1, C var2, float var3);

    public abstract int getRenderBoxRight(E var1, C var2, float var3);

    public abstract int getRenderBoxTop(E var1, C var2, float var3);

    public abstract int getRenderBoxBottom(E var1, C var2, float var3);

    public abstract int getLeftSideLength(E var1, class_310 var2);

    public abstract String getMenuName(E var1);

    public abstract String getFilterName(E var1);

    public abstract int getMenuTextFillLeftPadding(E var1);

    public abstract int getRightClickTitleBackgroundColor(E var1);

    public abstract boolean shouldScaleBoxWithOptionalScale();

    public boolean isInteractable(ElementRenderLocation location, E element) {
        return false;
    }

    public float getBoxScale(ElementRenderLocation location, E element, C context) {
        return 1.0f;
    }

    public boolean isMouseOverMenuElement(E element, int x, int y, int mouseX, int mouseY, class_310 mc) {
        int topEdge = y - 8;
        if (mouseY < topEdge) {
            return false;
        }
        int bottomEdge = y + 8;
        if (mouseY >= bottomEdge) {
            return false;
        }
        int rightEdge = x + 5;
        if (mouseX >= rightEdge) {
            return false;
        }
        int leftEdge = x - this.getLeftSideLength(element, mc);
        return mouseX >= leftEdge;
    }

    public boolean isHoveredOnMap(ElementRenderLocation location, E element, double mouseX, double mouseZ, double scale, double screenSizeBasedScale, double rendererDimDiv, C context, float partialTicks) {
        double fullScale = this.getBoxScale(location, element, context);
        if (this.shouldScaleBoxWithOptionalScale()) {
            fullScale *= screenSizeBasedScale;
        }
        double left = (double)this.getInteractionBoxLeft(element, context, partialTicks) * fullScale;
        double right = (double)this.getInteractionBoxRight(element, context, partialTicks) * fullScale;
        double top = (double)this.getInteractionBoxTop(element, context, partialTicks) * fullScale;
        double bottom = (double)this.getInteractionBoxBottom(element, context, partialTicks) * fullScale;
        double screenOffX = (mouseX - this.getRenderX(element, context, partialTicks) / rendererDimDiv) * scale;
        if (screenOffX < left || screenOffX >= right) {
            return false;
        }
        double screenOffY = (mouseZ - this.getRenderZ(element, context, partialTicks) / rendererDimDiv) * scale;
        return !(screenOffY < top) && !(screenOffY >= bottom);
    }

    public boolean isOnScreen(E element, double cameraX, double cameraZ, int width, int height, double scale, double screenSizeBasedScale, double rendererDimDiv, C context, float partialTicks) {
        double left;
        double xOnScreen = (this.getRenderX(element, context, partialTicks) / rendererDimDiv - cameraX) * scale + (double)(width / 2);
        double zOnScreen = (this.getRenderZ(element, context, partialTicks) / rendererDimDiv - cameraZ) * scale + (double)(height / 2);
        float boxScale = this.getBoxScale(ElementRenderLocation.WORLD_MAP, element, context);
        if (this.shouldScaleBoxWithOptionalScale()) {
            boxScale = (float)((double)boxScale * screenSizeBasedScale);
        }
        if ((left = xOnScreen + (double)((float)this.getRenderBoxLeft(element, context, partialTicks) * boxScale)) >= (double)width) {
            return false;
        }
        double right = xOnScreen + (double)((float)this.getRenderBoxRight(element, context, partialTicks) * boxScale);
        if (right <= 0.0) {
            return false;
        }
        double top = zOnScreen + (double)((float)this.getRenderBoxTop(element, context, partialTicks) * boxScale);
        if (top >= (double)height) {
            return false;
        }
        double bottom = zOnScreen + (double)((float)this.getRenderBoxBottom(element, context, partialTicks) * boxScale);
        return !(bottom <= 0.0);
    }

    public ArrayList<RightClickOption> getRightClickOptions(E element, IRightClickableElement target) {
        return null;
    }

    public boolean isRightClickValid(E element) {
        return false;
    }

    public CursorBox getTooltip(E element, C context, boolean overMenu) {
        return null;
    }
}

