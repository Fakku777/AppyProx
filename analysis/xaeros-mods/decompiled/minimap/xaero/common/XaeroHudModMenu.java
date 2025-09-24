/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.terraformersmc.modmenu.api.ConfigScreenFactory
 *  com.terraformersmc.modmenu.api.ModMenuApi
 */
package xaero.common;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import xaero.common.HudMod;
import xaero.common.gui.GuiSettings;

public class XaeroHudModMenu
implements ModMenuApi {
    public ConfigScreenFactory<GuiSettings> getModConfigScreenFactory() {
        return screen -> HudMod.INSTANCE.getGuiHelper().getMainSettingsScreen(screen);
    }
}

