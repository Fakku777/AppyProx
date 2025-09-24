/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package xaero.common.gui.widget;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_332;
import net.minecraft.class_437;
import xaero.common.gui.widget.ClickAction;
import xaero.common.gui.widget.HoverAction;
import xaero.common.gui.widget.Widget;
import xaero.common.gui.widget.WidgetClickHandler;
import xaero.common.gui.widget.WidgetScreen;
import xaero.common.gui.widget.WidgetType;
import xaero.common.gui.widget.init.WidgetInitializer;
import xaero.common.gui.widget.render.WidgetRenderer;

public class WidgetScreenHandler {
    private List<Widget> widgets = new ArrayList<Widget>();

    void addWidget(Widget widget) {
        if (widget != null) {
            this.widgets.add(widget);
        }
    }

    public void initialize(WidgetScreen screen, int width, int height) {
        for (Widget w : this.widgets) {
            WidgetInitializer widgetInit;
            if (!w.getLocation().isAssignableFrom(screen.getClass()) || (widgetInit = w.getType().widgetInit) == null) continue;
            widgetInit.init(screen, width, height, w);
        }
    }

    public void render(class_332 guiGraphics, WidgetScreen screen, int width, int height, int mouseX, int mouseY, double guiScale) {
        for (Widget w : this.widgets) {
            WidgetRenderer renderer;
            if (!w.getLocation().isAssignableFrom(screen.getClass()) || (renderer = w.getType().widgetRenderer) == null) continue;
            renderer.render(guiGraphics, width, height, mouseX, mouseY, guiScale, w);
        }
    }

    public boolean renderTooltips(class_332 guiGraphics, class_437 screen, int width, int height, int mouseX, int mouseY, double guiScale) {
        boolean result = false;
        for (Widget w : this.widgets) {
            if (!w.getLocation().isAssignableFrom(screen.getClass()) || !this.renderTooltip(guiGraphics, width, height, mouseX, mouseY, guiScale, w)) continue;
            result = true;
        }
        return result;
    }

    private boolean renderTooltip(class_332 guiGraphics, int width, int height, int mouseX, int mouseY, double guiScale, Widget widget) {
        if (widget.getOnHover() != HoverAction.TOOLTIP || widget.getTooltip() == null) {
            return false;
        }
        int x = widget.getBoxX(width, guiScale);
        int y = widget.getBoxY(height, guiScale);
        int w = widget.getBoxW(guiScale);
        int h = widget.getBoxH(guiScale);
        if (mouseX >= x && mouseY >= y && mouseX < x + w && mouseY < y + h) {
            widget.getCursorBox().drawBox(guiGraphics, mouseX, mouseY, width, height);
            return true;
        }
        return false;
    }

    public void handleClick(class_437 screen, int width, int height, int mouseX, int mouseY, double guiScale) {
        for (Widget w : this.widgets) {
            if (!w.getLocation().isAssignableFrom(screen.getClass())) continue;
            this.handleWidgetClick(screen, width, height, mouseX, mouseY, guiScale, w);
        }
    }

    private void handleWidgetClick(class_437 screen, int width, int height, int mouseX, int mouseY, double guiScale, Widget widget) {
        WidgetClickHandler clickHandler;
        if (widget.getOnClick() == ClickAction.NOTHING || widget.getType() == WidgetType.BUTTON) {
            return;
        }
        int x = widget.getBoxX(width, guiScale);
        int y = widget.getBoxY(height, guiScale);
        int w = widget.getBoxW(guiScale);
        int h = widget.getBoxH(guiScale);
        if (mouseX >= x && mouseY >= y && mouseX < x + w && mouseY < y + h && (clickHandler = widget.getOnClick().clickHandler) != null) {
            clickHandler.onClick(screen, widget);
        }
    }
}

