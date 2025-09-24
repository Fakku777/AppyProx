/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_5676
 *  org.spongepowered.asm.mixin.Mixin
 */
package xaero.common.mixin;

import java.util.function.Supplier;
import net.minecraft.class_5676;
import org.spongepowered.asm.mixin.Mixin;
import xaero.common.graphics.CursorBox;
import xaero.common.gui.IXaeroClickableWidget;

@Mixin(value={class_5676.class})
public class MixinCyclingButtonWidget
implements IXaeroClickableWidget {
    private Supplier<CursorBox> xaero_tooltip;

    @Override
    public Supplier<CursorBox> getXaero_tooltip() {
        return this.xaero_tooltip;
    }

    @Override
    public void setXaero_tooltip(Supplier<CursorBox> tooltip) {
        this.xaero_tooltip = tooltip;
    }
}

