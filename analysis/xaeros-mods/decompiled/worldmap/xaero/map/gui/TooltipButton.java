/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_4185
 *  net.minecraft.class_4185$class_4241
 */
package xaero.map.gui;

import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_4185;
import xaero.map.gui.CursorBox;
import xaero.map.gui.ICanTooltip;

public class TooltipButton
extends class_4185
implements ICanTooltip {
    protected Supplier<CursorBox> tooltipSupplier;

    public TooltipButton(int x, int y, int w, int h, class_2561 message, class_4185.class_4241 action, Supplier<CursorBox> tooltipSupplier) {
        super(x, y, w, h, message, action, field_40754);
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    public Supplier<CursorBox> getXaero_wm_tooltip() {
        return this.tooltipSupplier;
    }
}

