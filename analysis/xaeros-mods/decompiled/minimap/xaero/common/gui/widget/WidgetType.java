/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui.widget;

import xaero.common.gui.widget.init.ButtonWidgetInitializer;
import xaero.common.gui.widget.init.WidgetInitializer;
import xaero.common.gui.widget.loader.ButtonWidgetLoader;
import xaero.common.gui.widget.loader.ImageWidgetLoader;
import xaero.common.gui.widget.loader.TextWidgetLoader;
import xaero.common.gui.widget.loader.WidgetLoader;
import xaero.common.gui.widget.render.ImageWidgetRenderer;
import xaero.common.gui.widget.render.TextWidgetRenderer;
import xaero.common.gui.widget.render.WidgetRenderer;

public enum WidgetType {
    IMAGE(new ImageWidgetLoader(), new ImageWidgetRenderer(), null),
    BUTTON(new ButtonWidgetLoader(), null, new ButtonWidgetInitializer()),
    TEXT(new TextWidgetLoader(), new TextWidgetRenderer(), null);

    public final WidgetLoader widgetLoader;
    public final WidgetRenderer widgetRenderer;
    public final WidgetInitializer widgetInit;

    private WidgetType(WidgetLoader widgetLoader, WidgetRenderer widgetRenderer, WidgetInitializer widgetInit) {
        this.widgetLoader = widgetLoader;
        this.widgetRenderer = widgetRenderer;
        this.widgetInit = widgetInit;
    }
}

