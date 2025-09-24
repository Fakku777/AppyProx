/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_437
 */
package xaero.hud.category.ui.entry.widget;

import java.util.function.IntConsumer;
import java.util.function.Supplier;
import net.minecraft.class_437;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.widget.EditorButton;
import xaero.hud.category.ui.node.EditorNode;

public class EditorButtonIteration
extends EditorButton {
    protected int currentIndex;
    protected int optionCount;
    protected IntConsumer updatedIndexConsumer;

    public EditorButtonIteration(EditorNode parent, IntConsumer updatedIndexConsumer, Supplier<String> messageSupplier, boolean active, int currentIndex, int optionCount, int w, int h, GuiCategoryEditor.SettingRowList rowList) {
        super(parent, messageSupplier, active, w, h, b -> ((EditorButtonIteration)b).toggle(), rowList);
        this.currentIndex = currentIndex;
        this.optionCount = optionCount;
        this.updatedIndexConsumer = updatedIndexConsumer;
        this.updateMessage();
    }

    public final void toggle() {
        this.iterate(class_437.method_25442() ? -1 : 1);
    }

    private void iterate(int direction) {
        this.currentIndex += direction;
        this.putCurrentIndexInRange();
        this.updatedIndexConsumer.accept(this.currentIndex);
        this.updateMessage();
    }

    private void putCurrentIndexInRange() {
        if (this.currentIndex >= this.optionCount) {
            this.currentIndex %= this.optionCount;
            return;
        }
        if (this.currentIndex >= 0) {
            return;
        }
        this.currentIndex = this.optionCount + this.currentIndex % this.optionCount;
        if (this.currentIndex == this.optionCount) {
            this.currentIndex = 0;
        }
    }

    public boolean method_25401(double d, double e, double f, double g) {
        return super.method_25401(d, e, f, g);
    }
}

