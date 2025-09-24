/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10799
 *  net.minecraft.class_332
 */
package xaero.common.gui.widget.render;

import net.minecraft.class_10799;
import net.minecraft.class_332;
import xaero.common.gui.widget.ImageWidget;
import xaero.common.gui.widget.render.ScalableWidgetRenderer;
import xaero.hud.render.util.GuiRenderUtil;

public class ImageWidgetRenderer
extends ScalableWidgetRenderer<ImageWidget> {
    @Override
    protected void renderScaled(class_332 guiGraphics, int width, int height, int mouseX, int mouseY, double guiScale, ImageWidget widget) {
        GuiRenderUtil.submitBlit(guiGraphics, class_10799.field_56883, widget.getGlTexture().view, 0, 0, widget.getW(), widget.getH(), 0.0f, 0.0f, 1.0f, 1.0f, -1);
    }
}

