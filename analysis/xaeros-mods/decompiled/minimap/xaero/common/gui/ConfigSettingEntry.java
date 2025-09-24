/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_339
 */
package xaero.common.gui;

import net.minecraft.class_339;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.CursorBox;
import xaero.common.gui.ISettingEntry;
import xaero.common.settings.ModOptions;
import xaero.common.settings.Option;
import xaero.common.settings.XaeroCyclingOption;
import xaero.common.settings.XaeroDoubleOption;

public class ConfigSettingEntry
implements ISettingEntry {
    private ModOptions option;

    public ConfigSettingEntry(ModOptions option) {
        this.option = option;
    }

    @Override
    public class_339 createWidget(int x, int y, int w, boolean canEditIngameSettings) {
        class_339 widget = this.option.getXOption().createButton(x, y, w);
        widget.field_22763 = !this.option.isIngameOnly() || canEditIngameSettings;
        return widget;
    }

    @Override
    public String getStringForSearch() {
        Object tooltipPart;
        String mainText;
        Option mcOption = this.option.getXOption();
        CursorBox optionTooltip = this.option.getTooltip();
        String string = mcOption instanceof XaeroCyclingOption ? ((XaeroCyclingOption)mcOption).getSearchText() : (mainText = mcOption instanceof XaeroDoubleOption ? ((XaeroDoubleOption)mcOption).getMessage().getString() : "");
        if (optionTooltip != null) {
            tooltipPart = " " + optionTooltip.getPlainText();
            if (optionTooltip.getFullCode() != null) {
                tooltipPart = (String)tooltipPart + " " + optionTooltip.getFullCode().replace("gui.xaero", "");
            }
        } else {
            tooltipPart = "";
        }
        return mainText + " " + this.option.getEnumStringRaw().replace("gui.xaero", "") + (String)tooltipPart;
    }

    public int hashCode() {
        return this.option.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof ConfigSettingEntry && ((ConfigSettingEntry)obj).option == this.option;
    }

    public boolean usesWorldMapHardValue(IXaeroMinimap modMain) {
        return modMain.getSettings().usesWorldMapHardValue(this.option);
    }
}

