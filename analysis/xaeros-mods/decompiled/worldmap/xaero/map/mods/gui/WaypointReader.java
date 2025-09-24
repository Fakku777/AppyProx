/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.map.mods.gui;

import java.util.ArrayList;
import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.map.WorldMap;
import xaero.map.element.render.ElementReader;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.gui.GuiMap;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.mods.SupportMods;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointRenderContext;
import xaero.map.mods.gui.WaypointRenderer;

public class WaypointReader
extends ElementReader<Waypoint, WaypointRenderContext, WaypointRenderer> {
    public boolean waypointIsGood(Waypoint w, WaypointRenderContext context) {
        return (w.getType() != 1 && w.getType() != 2 || context.deathpoints) && (w.isGlobal() || context.userScale >= WorldMap.settings.minZoomForLocalWaypoints);
    }

    @Override
    public boolean isHidden(Waypoint element, WaypointRenderContext context) {
        return !this.waypointIsGood(element, context) || !WorldMap.settings.showDisabledWaypoints && element.isDisabled();
    }

    @Override
    public boolean isInteractable(ElementRenderLocation location, Waypoint element) {
        return true;
    }

    @Override
    public float getBoxScale(ElementRenderLocation location, Waypoint element, WaypointRenderContext context) {
        return context.worldmapWaypointsScale;
    }

    @Override
    public double getRenderX(Waypoint element, WaypointRenderContext context, float partialTicks) {
        return element.getRenderX();
    }

    @Override
    public double getRenderZ(Waypoint element, WaypointRenderContext context, float partialTicks) {
        return element.getRenderZ();
    }

    @Override
    public int getInteractionBoxLeft(Waypoint element, WaypointRenderContext context, float partialTicks) {
        return -this.getInteractionBoxRight(element, context, partialTicks);
    }

    @Override
    public int getInteractionBoxRight(Waypoint element, WaypointRenderContext context, float partialTicks) {
        return element.getSymbol().length() > 1 ? 21 : 14;
    }

    @Override
    public int getInteractionBoxTop(Waypoint element, WaypointRenderContext context, float partialTicks) {
        return WorldMap.settings.waypointBackgrounds ? -41 : -12;
    }

    @Override
    public int getInteractionBoxBottom(Waypoint element, WaypointRenderContext context, float partialTicks) {
        return WorldMap.settings.waypointBackgrounds ? 0 : 12;
    }

    @Override
    public int getLeftSideLength(Waypoint element, class_310 mc) {
        return 9 + element.getCachedNameLength();
    }

    @Override
    public String getMenuName(Waypoint element) {
        Object name = element.getName();
        if (element.isGlobal()) {
            name = "* " + (String)name;
        }
        return name;
    }

    @Override
    public int getMenuTextFillLeftPadding(Waypoint element) {
        return (element.isDisabled() ? 11 : 0) + (element.isTemporary() ? 10 : 0);
    }

    @Override
    public String getFilterName(Waypoint element) {
        return this.getMenuName(element) + " " + element.getSymbol();
    }

    @Override
    public ArrayList<RightClickOption> getRightClickOptions(final Waypoint element, IRightClickableElement target) {
        ArrayList<RightClickOption> rightClickOptions = new ArrayList<RightClickOption>();
        rightClickOptions.add(new RightClickOption(this, element.getName(), rightClickOptions.size(), target){

            @Override
            public void onAction(class_437 screen) {
                SupportMods.xaeroMinimap.openWaypoint((GuiMap)screen, element);
            }
        });
        if (WorldMap.settings.coordinates && !SupportMods.xaeroMinimap.hidingWaypointCoordinates()) {
            rightClickOptions.add(new RightClickOption(this, String.format("X: %d, Y: %s, Z: %d", element.getX(), element.isyIncluded() ? "" + element.getY() : "~", element.getZ()), rightClickOptions.size(), target){

                @Override
                public void onAction(class_437 screen) {
                    SupportMods.xaeroMinimap.openWaypoint((GuiMap)screen, element);
                }
            });
        }
        rightClickOptions.add(new RightClickOption(this, "gui.xaero_right_click_waypoint_edit", rightClickOptions.size(), target){

            @Override
            public void onAction(class_437 screen) {
                SupportMods.xaeroMinimap.openWaypoint((GuiMap)screen, element);
            }
        }.setNameFormatArgs("E"));
        rightClickOptions.add(new RightClickOption(this, "gui.xaero_right_click_waypoint_teleport", rightClickOptions.size(), target){

            @Override
            public void onAction(class_437 screen) {
                SupportMods.xaeroMinimap.teleportToWaypoint(screen, element);
            }

            @Override
            public boolean isActive() {
                return SupportMods.xaeroMinimap.canTeleport(SupportMods.xaeroMinimap.getWaypointWorld());
            }
        }.setNameFormatArgs("T"));
        rightClickOptions.add(new RightClickOption(this, "gui.xaero_right_click_waypoint_share", rightClickOptions.size(), target){

            @Override
            public void onAction(class_437 screen) {
                SupportMods.xaeroMinimap.shareWaypoint(element, (GuiMap)screen, SupportMods.xaeroMinimap.getWaypointWorld());
            }
        });
        rightClickOptions.add(new RightClickOption(this, "", rightClickOptions.size(), target){

            @Override
            public String getName() {
                return element.isTemporary() ? "gui.xaero_right_click_waypoint_restore" : (element.isDisabled() ? "gui.xaero_right_click_waypoint_enable" : "gui.xaero_right_click_waypoint_disable");
            }

            @Override
            public void onAction(class_437 screen) {
                if (element.isTemporary()) {
                    SupportMods.xaeroMinimap.toggleTemporaryWaypoint(element);
                } else {
                    SupportMods.xaeroMinimap.disableWaypoint(element);
                }
            }
        }.setNameFormatArgs("H"));
        rightClickOptions.add(new RightClickOption(this, "", rightClickOptions.size(), target){

            @Override
            public String getName() {
                return element.isTemporary() ? "gui.xaero_right_click_waypoint_delete_confirm" : "gui.xaero_right_click_waypoint_delete";
            }

            @Override
            public void onAction(class_437 screen) {
                if (element.isTemporary()) {
                    SupportMods.xaeroMinimap.deleteWaypoint(element);
                } else {
                    SupportMods.xaeroMinimap.toggleTemporaryWaypoint(element);
                }
            }
        }.setNameFormatArgs("DEL"));
        return rightClickOptions;
    }

    @Override
    public boolean isRightClickValid(Waypoint element) {
        return SupportMods.xaeroMinimap.waypointExists(element);
    }

    @Override
    public int getRightClickTitleBackgroundColor(Waypoint element) {
        return element.getColor();
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }

    @Override
    public int getRenderBoxLeft(Waypoint element, WaypointRenderContext context, float partialTicks) {
        int left = this.getInteractionBoxLeft(element, context, partialTicks);
        if (element.getAlpha() <= 0.0f) {
            return left;
        }
        return Math.min(left, -element.getCachedNameLength() * 3 / 2);
    }

    @Override
    public int getRenderBoxRight(Waypoint element, WaypointRenderContext context, float partialTicks) {
        int right = this.getInteractionBoxRight(element, context, partialTicks) + 12;
        if (element.getAlpha() <= 0.0f) {
            return right;
        }
        return Math.max(right, element.getCachedNameLength() * 3 / 2);
    }

    @Override
    public int getRenderBoxTop(Waypoint element, WaypointRenderContext context, float partialTicks) {
        return this.getInteractionBoxTop(element, context, partialTicks);
    }

    @Override
    public int getRenderBoxBottom(Waypoint element, WaypointRenderContext context, float partialTicks) {
        return this.getInteractionBoxBottom(element, context, partialTicks);
    }
}

