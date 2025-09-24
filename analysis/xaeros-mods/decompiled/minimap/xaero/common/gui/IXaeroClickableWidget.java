/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.function.Supplier;
import xaero.common.graphics.CursorBox;
import xaero.common.gui.ICanTooltip;

public interface IXaeroClickableWidget
extends ICanTooltip {
    public void setXaero_tooltip(Supplier<CursorBox> var1);
}

