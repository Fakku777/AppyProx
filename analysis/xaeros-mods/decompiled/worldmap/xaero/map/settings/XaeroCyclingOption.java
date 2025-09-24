/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_339
 *  net.minecraft.class_5676
 */
package xaero.map.settings;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_5676;
import xaero.map.gui.IXaeroClickableWidget;
import xaero.map.settings.ModOptions;
import xaero.map.settings.Option;

public class XaeroCyclingOption<T>
extends Option {
    private List<T> values;
    private Supplier<T> getter;
    private Consumer<T> setter;
    private Supplier<class_2561> buttonNameSupplier;

    public XaeroCyclingOption(ModOptions option, List<T> values, Supplier<T> getter, Consumer<T> setter, Supplier<class_2561> buttonNameSupplier) {
        super(option);
        this.values = values;
        this.getter = getter;
        this.setter = setter;
        this.buttonNameSupplier = buttonNameSupplier;
    }

    @Override
    public class_339 createButton(int x, int y, int width) {
        class_5676 resultButton = class_5676.method_32606(v -> this.buttonNameSupplier.get()).method_32620(this.values).method_32619(this.getter.get()).method_32617(x, y, width, 20, this.getCaption(), (button, value) -> {
            this.setter.accept(value);
            button.method_32605(value);
        });
        ((IXaeroClickableWidget)resultButton).setXaero_wm_tooltip(() -> this.option.getTooltip());
        return resultButton;
    }

    public String getSearchText() {
        return this.option.getEnumString() + ": " + this.buttonNameSupplier.get().getString();
    }
}

