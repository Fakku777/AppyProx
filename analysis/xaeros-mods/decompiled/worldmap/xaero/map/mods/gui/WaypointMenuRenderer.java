/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10799
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_5250
 *  org.joml.Matrix3x2fStack
 *  xaero.common.IXaeroMinimap
 *  xaero.common.gui.GuiNewSet
 *  xaero.common.gui.GuiWaypointSets
 *  xaero.hud.minimap.controls.key.MinimapKeyMappings
 *  xaero.hud.minimap.module.MinimapSession
 *  xaero.hud.minimap.world.MinimapWorld
 */
package xaero.map.mods.gui;

import java.io.IOException;
import net.minecraft.class_10799;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import org.joml.Matrix3x2fStack;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiNewSet;
import xaero.common.gui.GuiWaypointSets;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.map.WorldMap;
import xaero.map.element.MapElementMenuRenderer;
import xaero.map.element.render.ElementRenderer;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiTexturedButton;
import xaero.map.gui.dropdown.DropDownWidget;
import xaero.map.gui.dropdown.IDropDownWidgetCallback;
import xaero.map.misc.Misc;
import xaero.map.mods.SupportMods;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointMenuRenderContext;
import xaero.map.mods.gui.WaypointMenuRenderProvider;
import xaero.map.mods.gui.WaypointRenderer;

public class WaypointMenuRenderer
extends MapElementMenuRenderer<Waypoint, WaypointMenuRenderContext> {
    private final WaypointRenderer renderer;
    private class_4185 renderWaypointsButton;
    private class_4185 showDisabledButton;
    private class_4185 closeMenuWhenHoppingButton;
    private class_4185 currentMapWaypointsButton;
    private class_4185 renderAllSetsButton;

    public WaypointMenuRenderer(WaypointMenuRenderContext context, WaypointMenuRenderProvider provider, WaypointRenderer renderer) {
        super(context, provider);
        this.renderer = renderer;
    }

    public void onMapInit(GuiMap screen, class_310 mc, int width, int height, MinimapWorld waypointWorld, IXaeroMinimap modMain, MinimapSession minimapSession) {
        DropDownWidget setsDropdown;
        super.onMapInit(screen, mc, width, height);
        GuiWaypointSets sets = waypointWorld != null ? new GuiWaypointSets(true, waypointWorld) : null;
        IDropDownWidgetCallback setsDropdownCallback = null;
        if (sets != null) {
            setsDropdownCallback = (menu, selected) -> {
                if (selected == menu.size() - 1) {
                    GuiNewSet guiNewSet = new GuiNewSet(modMain, minimapSession, (class_437)screen, (class_437)screen, waypointWorld);
                    class_310.method_1551().method_1507((class_437)guiNewSet);
                    return false;
                }
                sets.setCurrentSet(selected);
                waypointWorld.setCurrentWaypointSetId(sets.getCurrentSetKey());
                try {
                    minimapSession.getWorldManagerIO().saveWorld(waypointWorld);
                }
                catch (IOException e) {
                    WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                }
                return true;
            };
        }
        DropDownWidget dropDownWidget = setsDropdown = sets == null ? null : DropDownWidget.Builder.begin().setOptions(sets.getOptions()).setX(width - 173).setY(height - 56).setW(151).setSelected(sets.getCurrentSet()).setCallback(setsDropdownCallback).setContainer(screen).setOpeningUp(true).setNarrationTitle((class_2561)class_2561.method_43471((String)"gui.xaero_dropdown_waypoint_set")).build();
        if (setsDropdown != null) {
            screen.method_25429(setsDropdown);
        }
        class_5250 fullWaypointMenuTooltipText = class_2561.method_43469((String)"gui.xaero_box_full_waypoints_menu", (Object[])new Object[]{class_2561.method_43470((String)Misc.getKeyName(MinimapKeyMappings.WAYPOINT_MENU)).method_27692(class_124.field_1077)});
        CursorBox fullWaypointMenuTooltip = new CursorBox((class_2561)fullWaypointMenuTooltipText, true);
        CursorBox onlyCurrentMapWaypointsTooltip = new CursorBox(WorldMap.settings.onlyCurrentMapWaypoints ? "gui.xaero_box_only_current_map_waypoints" : "gui.xaero_box_waypoints_selected_by_minimap", class_2583.field_24360, true);
        CursorBox renderingWaypointsTooltip = new CursorBox((class_2561)class_2561.method_43469((String)(WorldMap.settings.renderWaypoints ? "gui.xaero_box_rendering_waypoints" : "gui.xaero_box_not_rendering_waypoints"), (Object[])new Object[]{class_2561.method_43470((String)Misc.getKeyName(MinimapKeyMappings.TOGGLE_MAP_WAYPOINTS)).method_27692(class_124.field_1077)}), true);
        CursorBox renderAllSetsTooltip = new CursorBox((class_2561)class_2561.method_43469((String)(!modMain.getSettings().renderAllSets ? "gui.xaero_box_rendering_current_set" : "gui.xaero_box_rendering_all_sets"), (Object[])new Object[]{class_2561.method_43470((String)Misc.getKeyName(MinimapKeyMappings.RENDER_ALL_SETS)).method_27692(class_124.field_1077)}), true);
        CursorBox showingDisabledTooltip = new CursorBox(WorldMap.settings.showDisabledWaypoints ? "gui.xaero_box_showing_disabled" : "gui.xaero_box_hiding_disabled", class_2583.field_24360, true);
        CursorBox closeWhenHoppingTooltip = new CursorBox(WorldMap.settings.closeWaypointsWhenHopping ? "gui.xaero_box_closing_menu_when_hopping" : "gui.xaero_box_not_closing_menu_when_hopping", class_2583.field_24360, true);
        screen.addButton(new GuiTexturedButton(width - 173, height - 20, 20, 20, 229, 0, 16, 16, WorldMap.guiTextures, b -> this.onFullMenuButton(b, screen), () -> fullWaypointMenuTooltip, 256, 256));
        this.currentMapWaypointsButton = new GuiTexturedButton(width - 153, height - 20, 20, 20, WorldMap.settings.onlyCurrentMapWaypoints ? 213 : 229, 16, 16, 16, WorldMap.guiTextures, b -> this.onCurrentMapWaypointsButton(b, screen, width, height), () -> onlyCurrentMapWaypointsTooltip, 256, 256);
        screen.addButton(this.currentMapWaypointsButton);
        this.renderWaypointsButton = new GuiTexturedButton(width - 133, height - 20, 20, 20, WorldMap.settings.renderWaypoints ? 229 : 213, 48, 16, 16, WorldMap.guiTextures, b -> this.onRenderWaypointsButton(screen, width, height), () -> renderingWaypointsTooltip, 256, 256);
        screen.addButton(this.renderWaypointsButton);
        this.renderAllSetsButton = new GuiTexturedButton(width - 113, height - 20, 20, 20, !modMain.getSettings().renderAllSets ? 81 : 97, 16, 16, 16, WorldMap.guiTextures, b -> this.onRenderAllSetsButton(b, screen, width, height), () -> renderAllSetsTooltip, 256, 256);
        screen.addButton(this.renderAllSetsButton);
        this.showDisabledButton = new GuiTexturedButton(width - 93, height - 20, 20, 20, WorldMap.settings.showDisabledWaypoints ? 133 : 149, 16, 16, 16, WorldMap.guiTextures, b -> this.onShowDisabledButton(b, screen, width, height), () -> showingDisabledTooltip, 256, 256);
        screen.addButton(this.showDisabledButton);
        this.closeMenuWhenHoppingButton = new GuiTexturedButton(width - 73, height - 20, 20, 20, WorldMap.settings.closeWaypointsWhenHopping ? 181 : 197, 16, 16, 16, WorldMap.guiTextures, b -> this.onCloseMenuWhenHoppingButton(b, screen, width, height), () -> closeWhenHoppingTooltip, 256, 256);
        screen.addButton(this.closeMenuWhenHoppingButton);
    }

    public void onRenderWaypointsButton(GuiMap screen, int width, int height) {
        WorldMap.settings.renderWaypoints = !WorldMap.settings.renderWaypoints;
        try {
            WorldMap.settings.saveSettings();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        screen.method_25423(this.mc, width, height);
        screen.method_25395((class_364)this.renderWaypointsButton);
    }

    private void onFullMenuButton(class_4185 b, GuiMap screen) {
        SupportMods.xaeroMinimap.openWaypointsMenu(this.mc, screen);
    }

    private void onRenderAllSetsButton(class_4185 b, GuiMap screen, int width, int height) {
        SupportMods.xaeroMinimap.handleMinimapKeyBinding(MinimapKeyMappings.RENDER_ALL_SETS, screen);
        screen.method_25395((class_364)this.renderAllSetsButton);
    }

    private void onShowDisabledButton(class_4185 b, GuiMap screen, int width, int height) {
        WorldMap.settings.showDisabledWaypoints = !WorldMap.settings.showDisabledWaypoints;
        try {
            WorldMap.settings.saveSettings();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        screen.method_25423(this.mc, width, height);
        screen.method_25395((class_364)this.showDisabledButton);
    }

    private void onCloseMenuWhenHoppingButton(class_4185 b, GuiMap screen, int width, int height) {
        WorldMap.settings.closeWaypointsWhenHopping = !WorldMap.settings.closeWaypointsWhenHopping;
        try {
            WorldMap.settings.saveSettings();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        screen.method_25423(this.mc, width, height);
        screen.method_25395((class_364)this.closeMenuWhenHoppingButton);
    }

    private void onCurrentMapWaypointsButton(class_4185 b, GuiMap screen, int width, int height) {
        WorldMap.settings.onlyCurrentMapWaypoints = !WorldMap.settings.onlyCurrentMapWaypoints;
        try {
            WorldMap.settings.saveSettings();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        screen.method_25423(this.mc, width, height);
        screen.method_25395((class_364)this.currentMapWaypointsButton);
    }

    @Override
    public void renderInMenu(Waypoint element, class_332 guiGraphics, class_437 gui, int mouseX, int mouseY, double scale, boolean enabled, boolean hovered, class_310 mc, boolean pressed, int textX) {
        Matrix3x2fStack matrixStack = guiGraphics.method_51448();
        Waypoint w = element;
        boolean disabled = w.isDisabled();
        boolean temporary = w.isTemporary();
        int type = w.getType();
        int color = w.getColor();
        String symbol = w.getSymbol();
        matrixStack.translate(-4.0f, -4.0f);
        if (type == 1) {
            guiGraphics.method_25294(0, 0, 9, 9, color);
            guiGraphics.method_25291(class_10799.field_56883, Waypoint.minimapTextures, 1, 1, 0.0f, 78.0f, 9, 9, 256, 256, -16119286);
            guiGraphics.method_25291(class_10799.field_56883, Waypoint.minimapTextures, 0, 0, 0.0f, 78.0f, 9, 9, 256, 256, -197380);
        } else {
            guiGraphics.method_25294(0, 0, 9, 9, color);
        }
        if (type != 1) {
            guiGraphics.method_25303(mc.field_1772, symbol, 5 - mc.field_1772.method_1727(symbol) / 2, 1, -1);
        }
        int infoIconOffset = 10;
        if (disabled) {
            guiGraphics.method_25291(class_10799.field_56883, WorldMap.guiTextures, textX - 1 - infoIconOffset, 0, 173.0f, 16.0f, 8, 8, 256, 256, -256);
            infoIconOffset += 10;
        }
        if (temporary) {
            guiGraphics.method_25291(class_10799.field_56883, WorldMap.guiTextures, textX - 1 - infoIconOffset, 0, 165.0f, 16.0f, 8, 8, 256, 256, -65536);
            infoIconOffset += 10;
        }
    }

    @Override
    public int menuStartPos(int height) {
        return height - 59;
    }

    @Override
    public int menuSearchPadding() {
        return 14;
    }

    @Override
    protected String getFilterPlaceholder() {
        return "gui.xaero_filter_waypoints_by_name";
    }

    @Override
    protected ElementRenderer<? super Waypoint, ?, ?> getRenderer(Waypoint element) {
        return this.renderer;
    }

    @Override
    protected void beforeFiltering() {
    }

    @Override
    protected void beforeMenuRender() {
    }

    @Override
    protected void afterMenuRender() {
    }
}

