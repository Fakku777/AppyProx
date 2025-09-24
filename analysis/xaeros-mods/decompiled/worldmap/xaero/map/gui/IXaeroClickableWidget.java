/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.gui;

import java.util.function.Supplier;
import xaero.map.gui.CursorBox;
import xaero.map.gui.ICanTooltip;

public interface IXaeroClickableWidget
extends ICanTooltip {
    public void setXaero_wm_tooltip(Supplier<CursorBox> var1);
}

