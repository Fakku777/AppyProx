/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_332
 *  org.joml.Matrix3x2fStack
 */
package xaero.common.gui.widget.render;

import net.minecraft.class_332;
import org.joml.Matrix3x2fStack;
import xaero.common.gui.widget.ScalableWidget;
import xaero.common.gui.widget.Widget;
import xaero.common.gui.widget.render.WidgetRenderer;

public abstract class ScalableWidgetRenderer<T extends ScalableWidget>
implements WidgetRenderer<T> {
    @Override
    public void render(class_332 guiGraphics, int width, int height, int mouseX, int mouseY, double guiScale, T widget) {
        Matrix3x2fStack matrixStack = guiGraphics.method_51448();
        matrixStack.pushMatrix();
        matrixStack.translate((float)((Widget)widget).getX(width), (float)((Widget)widget).getY(height));
        if (((ScalableWidget)widget).isNoGuiScale()) {
            matrixStack.scale((float)(1.0 / guiScale), (float)(1.0 / guiScale));
        }
        matrixStack.scale((float)((ScalableWidget)widget).getScale(), (float)((ScalableWidget)widget).getScale());
        matrixStack.translate((float)((ScalableWidget)widget).getScaledOffsetX(), (float)((ScalableWidget)widget).getScaledOffsetY());
        this.renderScaled(guiGraphics, width, height, mouseX, mouseY, guiScale, widget);
        matrixStack.popMatrix();
    }

    protected abstract void renderScaled(class_332 var1, int var2, int var3, int var4, int var5, double var6, T var8);
}

