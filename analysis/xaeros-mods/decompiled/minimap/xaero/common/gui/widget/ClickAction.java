/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui.widget;

import xaero.common.gui.widget.WidgetClickHandler;
import xaero.common.gui.widget.WidgetUrlClickHandler;

public enum ClickAction {
    NOTHING(null),
    URL(new WidgetUrlClickHandler());

    public final WidgetClickHandler clickHandler;

    private ClickAction(WidgetClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
}

