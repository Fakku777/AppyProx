/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_4185
 *  net.minecraft.class_4185$class_4241
 */
package xaero.common.gui;

import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_4185;
import xaero.common.graphics.CursorBox;
import xaero.common.gui.ICanTooltip;

public class TooltipButton
extends class_4185
implements ICanTooltip {
    private Supplier<CursorBox> tooltipSupplier;

    public TooltipButton(int x, int y, int w, int h, class_2561 text, class_4185.class_4241 onPress, Supplier<CursorBox> tooltipSupplier) {
        super(x, y, w, h, text, onPress, field_40754);
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    public Supplier<CursorBox> getXaero_tooltip() {
        return this.tooltipSupplier;
    }
}

