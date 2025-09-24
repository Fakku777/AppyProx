/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.class_339
 */
package xaero.map.gui;

import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.class_339;
import xaero.map.WorldMap;
import xaero.map.gui.CursorBox;
import xaero.map.gui.ISettingEntry;
import xaero.map.settings.ModOptions;
import xaero.map.settings.Option;
import xaero.map.settings.XaeroCyclingOption;
import xaero.map.settings.XaeroDoubleOption;

public class ConfigSettingEntry
implements ISettingEntry {
    public static final Set<ModOptions> FILE_ONLY_ENABLE = new HashSet<ModOptions>(Lists.newArrayList((Object[])new ModOptions[]{ModOptions.MAP_TELEPORT_ALLOWED}));
    private ModOptions option;

    public ConfigSettingEntry(ModOptions option) {
        this.option = option;
    }

    @Override
    public class_339 createWidget(int x, int y, int w, boolean canEditIngameSettings) {
        class_339 widget = this.option.getXOption().createButton(x, y, w);
        boolean bl = widget.field_22763 = !this.option.isDisabledBecauseNotIngame() && !this.option.isDisabledBecauseMinimap() && !this.option.isDisabledBecausePac();
        if (widget.field_22763 && FILE_ONLY_ENABLE.contains(this.option) && !WorldMap.settings.getClientBooleanValue(this.option)) {
            widget.field_22763 = false;
        }
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
}

