/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_437
 */
package xaero.common.gui.widget;

import net.minecraft.class_437;
import xaero.common.graphics.GpuTextureAndView;
import xaero.common.gui.widget.ClickAction;
import xaero.common.gui.widget.HoverAction;
import xaero.common.gui.widget.ScalableWidget;
import xaero.common.gui.widget.WidgetType;

public class ImageWidget
extends ScalableWidget {
    private String imageId;
    private int imageW;
    private int imageH;
    private GpuTextureAndView glTexture;

    public ImageWidget(Class<? extends class_437> location, float horizontalAnchor, float verticalAnchor, ClickAction onClick, HoverAction onHover, int x, int y, int scaledOffsetX, int scaledOffsetY, String url, String tooltip, double scale, String imageId, int imageW, int imageH, GpuTextureAndView glTexture, boolean noGuiScale) {
        super(WidgetType.IMAGE, location, horizontalAnchor, verticalAnchor, onClick, onHover, x, y, scaledOffsetX, scaledOffsetY, url, tooltip, noGuiScale, scale);
        this.imageId = imageId;
        this.imageW = imageW;
        this.imageH = imageH;
        this.glTexture = glTexture;
    }

    public String getImageId() {
        return this.imageId;
    }

    public int getImageW() {
        return this.imageW;
    }

    public int getImageH() {
        return this.imageH;
    }

    public GpuTextureAndView getGlTexture() {
        return this.glTexture;
    }

    @Override
    public int getW() {
        return this.imageW;
    }

    @Override
    public int getH() {
        return this.imageH;
    }
}

