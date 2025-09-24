/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_339
 */
package xaero.map.settings;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import xaero.map.gui.XaeroDoubleOptionWidget;
import xaero.map.settings.ModOptions;
import xaero.map.settings.Option;

public class XaeroDoubleOption
extends Option {
    private final double min;
    private final double max;
    private final float step;
    private final Supplier<Double> getter;
    private final Consumer<Double> setter;
    private final Supplier<class_2561> displayStringGetter;

    public XaeroDoubleOption(ModOptions option, double min, double max, float step, Supplier<Double> getter, Consumer<Double> setter, Supplier<class_2561> displayStringGetter) {
        super(option);
        this.min = min;
        this.max = max;
        this.step = step;
        this.getter = getter;
        this.setter = setter;
        this.displayStringGetter = displayStringGetter;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public float getStep() {
        return this.step;
    }

    public Supplier<Double> getGetter() {
        return this.getter;
    }

    public Consumer<Double> getSetter() {
        return this.setter;
    }

    public Supplier<class_2561> getDisplayStringGetter() {
        return this.displayStringGetter;
    }

    public ModOptions getOption() {
        return this.option;
    }

    @Override
    public class_339 createButton(int x, int y, int width) {
        return new XaeroDoubleOptionWidget(this, x, y, width, 20);
    }

    public class_2561 getMessage() {
        return this.displayStringGetter.get();
    }
}

