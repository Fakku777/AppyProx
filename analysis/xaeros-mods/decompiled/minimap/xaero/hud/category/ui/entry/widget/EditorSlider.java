/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_3532
 *  net.minecraft.class_4892
 *  net.minecraft.class_5250
 */
package xaero.hud.category.ui.entry.widget;

import java.util.function.IntConsumer;
import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_3532;
import net.minecraft.class_4892;
import net.minecraft.class_5250;
import xaero.common.gui.IXaeroNarratableWidget;
import xaero.hud.category.ui.GuiCategoryEditor;

public class EditorSlider
extends class_4892
implements IXaeroNarratableWidget {
    protected int currentIndex;
    protected int prevNarrationIndex;
    protected int optionCount;
    protected IntConsumer updatedIndexConsumer;
    protected Supplier<String> messageSupplier;
    protected final GuiCategoryEditor.SettingRowList rowList;

    public EditorSlider(IntConsumer updatedIndexConsumer, Supplier<String> messageSupplier, int currentIndex, int optionCount, int widthIn, int heightIn, GuiCategoryEditor.SettingRowList rowList) {
        super(null, 2, 2, widthIn, heightIn, 0.0);
        this.updatedIndexConsumer = updatedIndexConsumer;
        this.messageSupplier = messageSupplier;
        this.optionCount = optionCount;
        this.currentIndex = this.prevNarrationIndex = currentIndex;
        this.field_22753 = this.toSliderValue(currentIndex);
        this.rowList = rowList;
        this.method_25346();
    }

    public boolean method_25404(int i, int j, int k) {
        if (i == 263) {
            this.manualOptionChange(this.currentIndex - 1);
            return false;
        }
        if (i == 262) {
            this.manualOptionChange(this.currentIndex + 1);
            return false;
        }
        return super.method_25404(i, j, k);
    }

    private void manualOptionChange(int index) {
        if (index < 0) {
            index = 0;
        } else if (index >= this.optionCount) {
            index = this.optionCount - 1;
        }
        this.field_22753 = this.toSliderValue(index);
        this.method_25344();
        this.method_25346();
    }

    @Override
    public class_5250 method_25360() {
        return class_2561.method_43470((String)"");
    }

    protected void method_25344() {
        this.currentIndex = this.toValue(this.field_22753);
        this.updatedIndexConsumer.accept(this.currentIndex);
    }

    protected void method_25346() {
        this.method_25355((class_2561)class_2561.method_43470((String)this.messageSupplier.get()));
        if (this.currentIndex != this.prevNarrationIndex) {
            this.rowList.narrateSelection();
        }
        this.prevNarrationIndex = this.currentIndex;
    }

    public double toSliderValue(int i) {
        return (double)i / (double)(this.optionCount - 1);
    }

    public int toValue(double d) {
        return (int)this.clamp(class_3532.method_16436((double)class_3532.method_15350((double)d, (double)0.0, (double)1.0), (double)0.0, (double)(this.optionCount - 1)));
    }

    private double clamp(double d) {
        d = Math.round(d);
        return class_3532.method_15350((double)d, (double)0.0, (double)(this.optionCount - 1));
    }
}

