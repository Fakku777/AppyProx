/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.class_1074
 *  net.minecraft.class_2561
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_437;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.ConfigSettingEntry;
import xaero.common.gui.GuiEditMode;
import xaero.common.gui.GuiEntityRadarSettings;
import xaero.common.gui.GuiMinimapBlockMapSettings;
import xaero.common.gui.GuiMinimapInfoSettings;
import xaero.common.gui.GuiMinimapMiscSettings;
import xaero.common.gui.GuiMinimapOverlaysSettings;
import xaero.common.gui.GuiMinimapSettings;
import xaero.common.gui.GuiMinimapViewSettings;
import xaero.common.gui.GuiReset;
import xaero.common.gui.GuiSettings;
import xaero.common.gui.GuiWaypointSettings;
import xaero.common.gui.GuiWaypoints;
import xaero.common.gui.ISettingEntry;
import xaero.common.gui.ScreenBase;
import xaero.common.gui.ScreenSwitchSettingEntry;
import xaero.common.settings.ModOptions;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class GuiMinimapMain
extends GuiMinimapSettings {
    private ISettingEntry[] mainEntries;
    private ISettingEntry[] searchableEntries;

    public GuiMinimapMain(class_437 current) {
        this(HudMod.INSTANCE, current, ScreenBase.tryToGetEscape(current));
    }

    public GuiMinimapMain(IXaeroMinimap modMain, class_437 par1GuiScreen, class_437 escScreen) {
        super(modMain, (class_2561)class_2561.method_43471((String)"gui.xaero_minimap_settings"), par1GuiScreen, escScreen);
        ScreenSwitchSettingEntry changePositionEntry = new ScreenSwitchSettingEntry("gui.xaero_change_position", (current, escape) -> par1GuiScreen instanceof GuiEditMode ? par1GuiScreen : new GuiEditMode(modMain, (class_437)current, (class_437)escape, false, (class_2561)class_2561.method_43471((String)"gui.xaero_minimap_guide")), null, true);
        ScreenSwitchSettingEntry viewSettingsEntry = new ScreenSwitchSettingEntry("gui.xaero_minimap_view_settings", (current, escape) -> new GuiMinimapViewSettings(modMain, (class_437)current, (class_437)escape), null, true);
        ScreenSwitchSettingEntry entityRadarSettingsEntry = new ScreenSwitchSettingEntry("gui.xaero_entity_radar_settings", (current, escape) -> new GuiEntityRadarSettings(modMain, (class_437)current, (class_437)escape), null, true);
        ScreenSwitchSettingEntry blockMapSettingsEntry = new ScreenSwitchSettingEntry("gui.xaero_minimap_block_map_settings", (current, escape) -> new GuiMinimapBlockMapSettings(modMain, (class_437)current, (class_437)escape), null, true);
        ScreenSwitchSettingEntry overlaySettingsEntry = new ScreenSwitchSettingEntry("gui.xaero_overlay_settings", (current, escape) -> new GuiMinimapOverlaysSettings(modMain, (class_437)current, (class_437)escape), null, true);
        ScreenSwitchSettingEntry infoSettingsEntry = new ScreenSwitchSettingEntry("gui.xaero_minimap_info_settings", (current, escape) -> new GuiMinimapInfoSettings(modMain, (class_437)current, (class_437)escape), null, true);
        ScreenSwitchSettingEntry waypointSettingsEntry = new ScreenSwitchSettingEntry("gui.xaero_waypoint_settings", (current, escape) -> new GuiWaypointSettings(modMain, (class_437)current, (class_437)escape), null, true);
        ScreenSwitchSettingEntry miscSettingsEntry = new ScreenSwitchSettingEntry("gui.xaero_minimap_misc_settings", (current, escape) -> new GuiMinimapMiscSettings(modMain, (class_437)current, (class_437)escape), null, true);
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        ScreenSwitchSettingEntry waypointsEntry = new ScreenSwitchSettingEntry("gui.xaero_waypoints", (current, escape) -> {
            MinimapSession minimapSession2 = BuiltInHudModules.MINIMAP.getCurrentSession();
            if (minimapSession2 != null && modMain.getSettings().waypointsGUI(minimapSession2)) {
                return new GuiWaypoints((HudMod)modMain, minimapSession2, (class_437)this, (class_437)escape);
            }
            return null;
        }, null, minimapSession != null && modMain.getSettings().waypointsGUI(minimapSession));
        ArrayList mainEntriesBuilder = Lists.newArrayList((Object[])new ISettingEntry[]{new ConfigSettingEntry(ModOptions.MINIMAP), changePositionEntry, viewSettingsEntry, blockMapSettingsEntry, entityRadarSettingsEntry, overlaySettingsEntry, infoSettingsEntry, waypointSettingsEntry, miscSettingsEntry, waypointsEntry});
        if (modMain.isStandalone()) {
            mainEntriesBuilder.add(new ScreenSwitchSettingEntry("gui.xaero_reset_defaults", (current, escape) -> new GuiReset(this::resetConfirmResult, par1GuiScreen, (class_437)escape), null, true));
        }
        this.mainEntries = mainEntriesBuilder.toArray(new ISettingEntry[0]);
        LinkedHashSet<ISettingEntry> searchableEntriesBuilder = new LinkedHashSet<ISettingEntry>();
        for (ISettingEntry entry : this.mainEntries) {
            if (entry instanceof ScreenSwitchSettingEntry) {
                ScreenSwitchSettingEntry screenSwitchEntry = (ScreenSwitchSettingEntry)entry;
                class_437 tempScreen = screenSwitchEntry.getScreenFactory().apply(this, this);
                if (tempScreen instanceof GuiSettings) {
                    GuiSettings tempSettingsScreen = (GuiSettings)tempScreen;
                    ISettingEntry[] settingsScreenEntries = tempSettingsScreen.getEntriesCopy();
                    if (settingsScreenEntries == null) continue;
                    searchableEntriesBuilder.addAll(Arrays.asList(settingsScreenEntries));
                    continue;
                }
                searchableEntriesBuilder.add(entry);
                continue;
            }
            searchableEntriesBuilder.add(entry);
        }
        this.searchableEntries = searchableEntriesBuilder.toArray(new ISettingEntry[0]);
    }

    @Override
    public void method_25426() {
        this.entries = this.entryFilter.isEmpty() ? this.mainEntries : this.searchableEntries;
        super.method_25426();
        if (ModSettings.serverSettings != ModSettings.defaultSettings) {
            this.screenTitle = class_2561.method_43470((String)("\u00a7e" + class_1074.method_4662((String)"gui.xaero_server_disabled", (Object[])new Object[0])));
        }
    }
}

