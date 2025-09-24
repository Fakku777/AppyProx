/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_437
 */
package xaero.common.gui.widget;

import net.minecraft.class_437;
import xaero.common.graphics.CursorBox;
import xaero.common.gui.widget.ClickAction;
import xaero.common.gui.widget.HoverAction;
import xaero.common.gui.widget.WidgetType;

public class Widget {
    private WidgetType type;
    private Class<? extends class_437> location;
    private float horizontalAnchor;
    private float verticalAnchor;
    private ClickAction onClick;
    private HoverAction onHover;
    private int x;
    private int y;
    private String url;
    private String tooltip;
    private CursorBox cursorBox;

    public Widget(WidgetType type, Class<? extends class_437> location, float horizontalAnchor, float verticalAnchor, ClickAction onClick, HoverAction onHover, int x, int y, String url, String tooltip) {
        this.type = type;
        this.location = location;
        this.horizontalAnchor = horizontalAnchor;
        this.verticalAnchor = verticalAnchor;
        this.onClick = onClick;
        this.onHover = onHover;
        this.x = x;
        this.y = y;
        this.url = url;
        this.tooltip = tooltip;
        if (tooltip != null && !tooltip.isEmpty()) {
            this.cursorBox = new CursorBox(tooltip);
        }
    }

    public WidgetType getType() {
        return this.type;
    }

    public Class<? extends class_437> getLocation() {
        return this.location;
    }

    public float getHorizontalAnchor() {
        return this.horizontalAnchor;
    }

    public float getVerticalAnchor() {
        return this.verticalAnchor;
    }

    public ClickAction getOnClick() {
        return this.onClick;
    }

    public HoverAction getOnHover() {
        return this.onHover;
    }

    public int getX(int width) {
        return (int)((float)width * this.horizontalAnchor + (float)this.x);
    }

    public int getY(int height) {
        return (int)((float)height * this.verticalAnchor + (float)this.y);
    }

    public int getW() {
        return 1;
    }

    public int getH() {
        return 1;
    }

    public int getBoxX(int width, double guiScale) {
        return this.getX(width);
    }

    public int getBoxY(int height, double guiScale) {
        return this.getX(height);
    }

    public int getBoxW(double guiScale) {
        return this.getW();
    }

    public int getBoxH(double guiScale) {
        return this.getH();
    }

    public String getUrl() {
        return this.url;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public CursorBox getCursorBox() {
        return this.cursorBox;
    }
}

