/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.common.gui.widget;

import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.common.gui.widget.Alignment;
import xaero.common.gui.widget.ClickAction;
import xaero.common.gui.widget.HoverAction;
import xaero.common.gui.widget.ScalableWidget;
import xaero.common.gui.widget.WidgetType;

public class TextWidget
extends ScalableWidget {
    private String text;
    private Alignment alignment;

    public TextWidget(Class<? extends class_437> location, float horizontalAnchor, float verticalAnchor, ClickAction onClick, HoverAction onHover, int x, int y, int scaledOffsetX, int scaledOffsetY, String url, String tooltip, String text, Alignment alignment, boolean noGuiScale, double scale) {
        super(WidgetType.TEXT, location, horizontalAnchor, verticalAnchor, onClick, onHover, x, y, scaledOffsetX, scaledOffsetY, url, tooltip, noGuiScale, scale);
        this.text = text;
        this.alignment = alignment;
    }

    public String getText() {
        return this.text;
    }

    public Alignment getAlignment() {
        return this.alignment;
    }

    @Override
    public int getW() {
        return class_310.method_1551().field_1772.method_1727(this.text);
    }

    @Override
    public int getH() {
        return 10;
    }

    @Override
    public int getScaledOffsetX() {
        int pos = super.getScaledOffsetX();
        if (this.alignment == Alignment.RIGHT) {
            pos -= class_310.method_1551().field_1772.method_1727(this.text);
        } else if (this.alignment == Alignment.CENTER) {
            pos -= class_310.method_1551().field_1772.method_1727(this.text) / 2;
        }
        return pos;
    }
}

