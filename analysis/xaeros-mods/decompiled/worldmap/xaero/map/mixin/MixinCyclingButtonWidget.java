/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_5676
 *  org.spongepowered.asm.mixin.Mixin
 */
package xaero.map.mixin;

import java.util.function.Supplier;
import net.minecraft.class_5676;
import org.spongepowered.asm.mixin.Mixin;
import xaero.map.gui.CursorBox;
import xaero.map.gui.IXaeroClickableWidget;

@Mixin(value={class_5676.class})
public class MixinCyclingButtonWidget
implements IXaeroClickableWidget {
    private Supplier<CursorBox> xaero_wm_tooltip;

    @Override
    public Supplier<CursorBox> getXaero_wm_tooltip() {
        return this.xaero_wm_tooltip;
    }

    @Override
    public void setXaero_wm_tooltip(Supplier<CursorBox> tooltip) {
        this.xaero_wm_tooltip = tooltip;
    }
}

