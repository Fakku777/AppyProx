/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_410
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import java.io.IOException;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_410;
import net.minecraft.class_437;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiWaypoints;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.path.XaeroPath;

public class GuiDeleteSet
extends class_410 {
    public GuiDeleteSet(String setName, XaeroPath worldPath, String name, class_437 parent, class_437 escapeScreen, IXaeroMinimap modMain, MinimapSession session) {
        super(result -> GuiDeleteSet.confirmDeleteSet(result, worldPath, name, parent, escapeScreen, modMain, session), (class_2561)class_2561.method_43470((String)(class_1074.method_4662((String)"gui.xaero_delete_set_message", (Object[])new Object[0]) + ": " + setName.replace("\u00a7\u00a7", ":") + "?")), (class_2561)class_2561.method_43471((String)"gui.xaero_delete_set_message2"));
    }

    private static void confirmDeleteSet(boolean p_confirmResult_1_, XaeroPath worldPath, String name, class_437 parent, class_437 escapeScreen, IXaeroMinimap modMain, MinimapSession session) {
        if (p_confirmResult_1_) {
            MinimapWorldManager waypointsManager = session.getWorldManager();
            waypointsManager.getWorld(worldPath).removeWaypointSet(name);
            waypointsManager.getWorld(worldPath).setCurrentWaypointSetId("gui.xaero_default");
            try {
                session.getWorldManagerIO().saveWorld(waypointsManager.getWorld(worldPath));
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
            class_310.method_1551().method_1507((class_437)new GuiWaypoints((HudMod)modMain, session, ((GuiWaypoints)parent).parent, escapeScreen));
        } else {
            class_310.method_1551().method_1507(parent);
        }
    }
}

