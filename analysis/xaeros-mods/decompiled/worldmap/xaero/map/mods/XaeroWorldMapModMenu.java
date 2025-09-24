/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.terraformersmc.modmenu.api.ConfigScreenFactory
 *  com.terraformersmc.modmenu.api.ModMenuApi
 */
package xaero.map.mods;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import xaero.map.gui.GuiSettings;
import xaero.map.gui.GuiWorldMapSettings;

public class XaeroWorldMapModMenu
implements ModMenuApi {
    public ConfigScreenFactory<GuiSettings> getModConfigScreenFactory() {
        return GuiWorldMapSettings::new;
    }
}

