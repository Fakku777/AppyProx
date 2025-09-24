/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_339
 *  net.minecraft.class_437
 */
package xaero.map.gui;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_339;
import net.minecraft.class_437;
import xaero.map.gui.CursorBox;
import xaero.map.gui.ISettingEntry;
import xaero.map.gui.ScreenBase;
import xaero.map.gui.TooltipButton;

public class ScreenSwitchSettingEntry
implements ISettingEntry {
    private String name;
    private BiFunction<class_437, class_437, class_437> screenFactory;
    private Supplier<CursorBox> tooltipSupplier;
    private boolean active;

    public ScreenSwitchSettingEntry(String name, BiFunction<class_437, class_437, class_437> screenFactoryFromCurrentAndEscape, CursorBox tooltip, boolean active) {
        this.name = name;
        this.screenFactory = screenFactoryFromCurrentAndEscape;
        this.tooltipSupplier = () -> tooltip;
        this.active = active;
    }

    @Override
    public String getStringForSearch() {
        CursorBox entryTooltip = this.tooltipSupplier == null ? null : this.tooltipSupplier.get();
        return class_1074.method_4662((String)this.name, (Object[])new Object[0]) + " " + this.name.replace("gui.xaero", "") + (String)(entryTooltip != null ? " " + entryTooltip.getFullCode().replace("gui.xaero", "") + " " + class_1074.method_4662((String)entryTooltip.getFullCode(), (Object[])new Object[0]) : "");
    }

    @Override
    public class_339 createWidget(int x, int y, int w, boolean canEditIngameSettings) {
        TooltipButton button = new TooltipButton(x, y, w, 20, (class_2561)class_2561.method_43471((String)this.name), b -> {
            class_310 mc = class_310.method_1551();
            class_437 current = mc.field_1755;
            class_437 currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
            class_437 targetScreen = this.screenFactory.apply(current, currentEscScreen);
            if (current instanceof ScreenBase) {
                ((ScreenBase)current).onExit(targetScreen);
            } else {
                mc.method_1507(targetScreen);
            }
        }, this.tooltipSupplier);
        button.field_22763 = this.active;
        return button;
    }

    public BiFunction<class_437, class_437, class_437> getScreenFactory() {
        return this.screenFactory;
    }
}

