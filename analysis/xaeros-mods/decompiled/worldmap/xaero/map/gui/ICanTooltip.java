/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package xaero.map.gui;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import xaero.map.gui.CursorBox;

public interface ICanTooltip {
    @Nullable
    public Supplier<CursorBox> getXaero_wm_tooltip();
}

