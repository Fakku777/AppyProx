/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_357
 */
package xaero.map.gui;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_357;
import xaero.map.gui.CursorBox;
import xaero.map.gui.ICanTooltip;

public class TooltipSlider
extends class_357
implements ICanTooltip {
    protected final Supplier<CursorBox> tooltipSupplier;
    protected final Consumer<Double> onValue;
    protected final Function<TooltipSlider, class_2561> messageUpdater;

    public TooltipSlider(int x, int y, int w, int h, class_2561 message, double value, Consumer<Double> onValue, Function<TooltipSlider, class_2561> messageUpdater, Supplier<CursorBox> tooltipSupplier) {
        super(x, y, w, h, message, value);
        this.tooltipSupplier = tooltipSupplier;
        this.onValue = onValue;
        this.messageUpdater = messageUpdater;
    }

    @Override
    public Supplier<CursorBox> getXaero_wm_tooltip() {
        return this.tooltipSupplier;
    }

    protected void method_25346() {
        this.method_25355(this.messageUpdater.apply(this));
    }

    protected void method_25344() {
        this.onValue.accept(this.field_22753);
    }
}

