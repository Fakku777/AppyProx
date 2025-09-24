/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_437
 */
package xaero.common.gui.widget;

import net.minecraft.class_437;
import xaero.common.gui.widget.ClickAction;
import xaero.common.gui.widget.HoverAction;
import xaero.common.gui.widget.Widget;

public abstract class WidgetBuilder {
    protected Class<? extends class_437> location;
    protected float horizontalAnchor;
    protected float verticalAnchor;
    protected ClickAction onClick = ClickAction.NOTHING;
    protected HoverAction onHover = HoverAction.NOTHING;
    protected int x;
    protected int y;
    protected String url;
    protected String tooltip;

    public void setLocation(Class<? extends class_437> location) {
        this.location = location;
    }

    public void setHorizontalAnchor(float horizontalAnchor) {
        this.horizontalAnchor = horizontalAnchor;
    }

    public void setVerticalAnchor(float verticalAnchor) {
        this.verticalAnchor = verticalAnchor;
    }

    public void setOnClick(ClickAction onClick) {
        this.onClick = onClick;
    }

    public void setOnHover(HoverAction onHover) {
        this.onHover = onHover;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public boolean validate() {
        return !(this.location == null || this.onHover == HoverAction.TOOLTIP && this.tooltip == null || this.onClick == ClickAction.URL && this.url == null);
    }

    public abstract Widget build();
}

