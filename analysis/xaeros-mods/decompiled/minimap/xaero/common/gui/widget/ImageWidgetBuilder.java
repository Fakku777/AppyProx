/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui.widget;

import xaero.common.graphics.GpuTextureAndView;
import xaero.common.gui.widget.ImageWidget;
import xaero.common.gui.widget.ScalableWidgetBuilder;
import xaero.common.gui.widget.Widget;

public class ImageWidgetBuilder
extends ScalableWidgetBuilder {
    private String imageId;
    private int imageW;
    private int imageH;
    private GpuTextureAndView glTexture;

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setImageW(int imageW) {
        this.imageW = imageW;
    }

    public void setImageH(int imageH) {
        this.imageH = imageH;
    }

    public void setGlTexture(GpuTextureAndView glTexture) {
        this.glTexture = glTexture;
    }

    @Override
    public boolean validate() {
        return super.validate() && this.imageId != null && this.imageW > 0 && this.imageH > 0 && this.glTexture != null;
    }

    @Override
    public Widget build() {
        return new ImageWidget(this.location, this.horizontalAnchor, this.verticalAnchor, this.onClick, this.onHover, this.x, this.y, this.scaledOffsetX, this.scaledOffsetY, this.url, this.tooltip, this.scale, this.imageId, this.imageW, this.imageH, this.glTexture, this.noGuiScale);
    }
}

