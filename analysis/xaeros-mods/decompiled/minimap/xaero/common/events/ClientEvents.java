/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.class_10366
 *  net.minecraft.class_1041
 *  net.minecraft.class_1074
 *  net.minecraft.class_11278
 *  net.minecraft.class_1657
 *  net.minecraft.class_1936
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_2556$class_7602
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_429
 *  net.minecraft.class_4358
 *  net.minecraft.class_437
 *  net.minecraft.class_4398
 *  net.minecraft.class_442
 *  net.minecraft.class_4439
 *  net.minecraft.class_4666
 *  net.minecraft.class_4877
 *  net.minecraft.class_500
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  org.apache.commons.lang3.StringUtils
 */
package xaero.common.events;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.class_10366;
import net.minecraft.class_1041;
import net.minecraft.class_1074;
import net.minecraft.class_11278;
import net.minecraft.class_1657;
import net.minecraft.class_1936;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2556;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_429;
import net.minecraft.class_4358;
import net.minecraft.class_437;
import net.minecraft.class_4398;
import net.minecraft.class_442;
import net.minecraft.class_4439;
import net.minecraft.class_4666;
import net.minecraft.class_4877;
import net.minecraft.class_500;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import org.apache.commons.lang3.StringUtils;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.XaeroMinimapCore;
import xaero.common.effect.Effects;
import xaero.common.graphics.shader.CustomUniforms;
import xaero.common.gui.GuiAddWaypoint;
import xaero.common.gui.GuiWaypoints;
import xaero.common.gui.GuiWidgetUpdateAll;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.misc.Misc;
import xaero.common.patreon.Patreon;
import xaero.common.settings.ModSettings;
import xaero.hud.HudSession;
import xaero.hud.controls.key.KeyMappingTickHandler;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.element.render.world.MinimapElementWorldRendererHandler;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointSession;

public class ClientEvents {
    protected IXaeroMinimap modMain;
    private class_437 lastGuiOpen;
    private Field realmsTaskField;
    private Field realmsTaskServerField;
    public class_4877 latestRealm;

    public ClientEvents(IXaeroMinimap modMain) {
        this.modMain = modMain;
    }

    public class_437 handleGuiOpen(class_437 gui) {
        if (!this.modMain.isFirstStageLoaded()) {
            return gui;
        }
        if (gui != null && gui.getClass() == class_429.class) {
            if (!ModSettings.settingsButton) {
                return gui;
            }
            gui = this.modMain.getGuiHelper().getMyOptions();
            try {
                this.modMain.getSettings().saveSettings();
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
        }
        if (gui instanceof class_442 || gui instanceof class_500) {
            this.modMain.getSettings().resetServerSettings();
        }
        class_310 mc = class_310.method_1551();
        if (gui instanceof class_4398) {
            try {
                if (this.realmsTaskField == null) {
                    this.realmsTaskField = Misc.getFieldReflection(class_4398.class, "queuedTasks", "field_46707", "Ljava/util/List;", "f_302752_");
                    this.realmsTaskField.setAccessible(true);
                }
                if (this.realmsTaskServerField == null) {
                    this.realmsTaskServerField = Misc.getFieldReflection(class_4439.class, "server", "field_20224", "Lnet/minecraft/class_4877;", "f_90327_");
                    this.realmsTaskServerField.setAccessible(true);
                }
                class_4398 realmsTaskScreen = (class_4398)gui;
                List tasks = (List)this.realmsTaskField.get(realmsTaskScreen);
                for (class_4358 task : tasks) {
                    class_4439 realmsTask;
                    class_4877 realm;
                    if (!(task instanceof class_4439) || (realm = (class_4877)this.realmsTaskServerField.get(realmsTask = (class_4439)task)) == null || this.latestRealm != null && realm.field_22599 == this.latestRealm.field_22599) continue;
                    this.latestRealm = realm;
                }
            }
            catch (Exception e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
        } else if ((gui instanceof GuiAddWaypoint || gui instanceof GuiWaypoints) && (mc.field_1724.method_6059(Effects.NO_WAYPOINTS) || mc.field_1724.method_6059(Effects.NO_WAYPOINTS_HARMFUL))) {
            gui = null;
        }
        this.lastGuiOpen = gui;
        return gui;
    }

    public void handleRenderGameOverlayEventPre(class_332 guiGraphics, float partialTicks) {
        if (class_310.method_1551().field_1690.field_1842) {
            return;
        }
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null) {
            class_1041 mainwindow = class_310.method_1551().method_22683();
            GpuBufferSlice projectionMatrixBU = RenderSystem.getProjectionMatrixBuffer();
            class_10366 projectionTypeBU = RenderSystem.getProjectionType();
            MinimapElementWorldRendererHandler worldRendererHandler = HudMod.INSTANCE.getMinimap().getWorldRendererHandler();
            class_11278 orthoProjectionCache = worldRendererHandler.getOrthoProjectionCache();
            GpuBufferSlice orthoBufferSlice = orthoProjectionCache.method_71092((float)mainwindow.method_4489(), (float)mainwindow.method_4506());
            RenderSystem.setProjectionMatrix((GpuBufferSlice)orthoBufferSlice, (class_10366)class_10366.field_54954);
            RenderSystem.getModelViewStack().pushMatrix();
            RenderSystem.getModelViewStack().identity();
            class_310 mc = class_310.method_1551();
            class_243 renderPos = mc.field_1773.method_19418().method_19326();
            worldRendererHandler.prepareRender(XaeroMinimapCore.waypointsProjection, XaeroMinimapCore.waypointModelView);
            worldRendererHandler.render(renderPos, partialTicks, null, mc.field_1687.method_8597().comp_646(), (class_5321<class_1937>)mc.field_1687.method_27983());
            RenderSystem.getModelViewStack().popMatrix();
            RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionMatrixBU, (class_10366)projectionTypeBU);
        }
    }

    public void handleRenderGameOverlayEventPost() {
        if (!this.modMain.isLoadedClient()) {
            return;
        }
        this.modMain.getHud().getEventHandler().handleRenderGameOverlayEventPost();
    }

    public boolean handleClientSendChatEvent(String message) {
        if (message.startsWith("xaero_waypoint_add:")) {
            String[] args = message.split(":");
            WaypointSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession().getWaypointSession();
            minimapSession.getSharing().onWaypointAdd(args);
            return true;
        }
        if (message.equals("xaero_tp_anyway")) {
            WaypointSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession().getWaypointSession();
            minimapSession.getTeleport().teleportAnyway();
            return true;
        }
        return false;
    }

    public boolean handleClientPlayerChatReceivedEvent(class_2556.class_7602 chatType, class_2561 component, GameProfile gameProfile) {
        if (component == null) {
            return false;
        }
        return this.handleChatMessage(gameProfile == null ? class_1074.method_4662((String)"gui.xaero_waypoint_somebody_shared", (Object[])new Object[0]) : gameProfile.getName(), component);
    }

    public boolean handleClientSystemChatReceivedEvent(class_2561 component) {
        String probableName;
        XaeroMinimapSession minimapSession;
        if (component == null) {
            return false;
        }
        String textString = component.getString();
        if (textString.contains("\u00a7r\u00a7e\u00a7s\u00a7e\u00a7t\u00a7x\u00a7a\u00a7e\u00a7r\u00a7o")) {
            minimapSession = XaeroMinimapSession.getCurrentSession();
            minimapSession.getMinimapProcessor().setNoMinimapMessageReceived(false);
            minimapSession.getMinimapProcessor().setFairPlayOnlyMessageReceived(false);
        }
        if (textString.contains("\u00a7n\u00a7o\u00a7m\u00a7i\u00a7n\u00a7i\u00a7m\u00a7a\u00a7p")) {
            minimapSession = XaeroMinimapSession.getCurrentSession();
            minimapSession.getMinimapProcessor().setNoMinimapMessageReceived(true);
        }
        if (textString.contains("\u00a7f\u00a7a\u00a7i\u00a7r\u00a7x\u00a7a\u00a7e\u00a7r\u00a7o")) {
            minimapSession = XaeroMinimapSession.getCurrentSession();
            minimapSession.getMinimapProcessor().setFairPlayOnlyMessageReceived(true);
        }
        return this.handleChatMessage((probableName = StringUtils.substringBetween((String)textString, (String)"<", (String)">")) == null ? class_1074.method_4662((String)"gui.xaero_waypoint_server_shared", (Object[])new Object[0]) : probableName, component);
    }

    private boolean handleChatMessage(String playerName, class_2561 text) {
        String textString = text.getString();
        if (textString.contains("xaero_waypoint:") || textString.contains("xaero-waypoint:")) {
            MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
            if (minimapSession == null) {
                return false;
            }
            minimapSession.getWaypointSession().getSharing().onWaypointReceived(playerName, textString);
            return true;
        }
        return false;
    }

    public void handleDrawScreenEventPost(class_437 gui) {
        if (Patreon.needsNotification() && gui instanceof class_442) {
            class_310.method_1551().method_1507((class_437)new GuiWidgetUpdateAll(this.modMain));
        } else if (this.modMain.isOutdated()) {
            this.modMain.setOutdated(false);
        }
    }

    public void handlePlayerSetSpawnEvent(class_2338 newSpawnPoint, class_1937 world) {
        MinimapSession minimapSession;
        if (world instanceof class_638 && (minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession()) != null) {
            minimapSession.getWorldStateUpdater().setCurrentWorldSpawn(newSpawnPoint);
        }
    }

    public Object getLastGuiOpen() {
        return this.lastGuiOpen;
    }

    public void worldUnload(class_1936 world) {
        XaeroMinimapSession minimapSession;
        if (world instanceof class_638 && (minimapSession = XaeroMinimapSession.getCurrentSession()) != null) {
            MinimapProcessor minimap = minimapSession.getMinimapProcessor();
            minimap.getRadarSession().update(null, null, null);
        }
    }

    public void handleClientTickStart() {
        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession != null) {
            MinimapProcessor minimap = minimapSession.getMinimapProcessor();
            minimap.onClientTick();
            if (class_310.method_1551().field_1755 == null) {
                minimapSession.getKeyMappingTickHandler().tick();
            }
            HudSession hudSession = HudSession.getCurrentSession();
            this.modMain.getClientEventsListener().clientTickPost(hudSession);
        }
    }

    public void handlePlayerTickStart(class_1657 player) {
        if (player != class_310.method_1551().field_1724) {
            return;
        }
        if (!this.modMain.isLoadedClient()) {
            return;
        }
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null) {
            try {
                MinimapProcessor minimap = minimapSession.getProcessor();
                minimapSession.getWorldStateUpdater().update();
                minimap.onPlayerTick();
                class_310 mc = class_310.method_1551();
                HudSession hudSession = HudSession.getCurrentSession();
                this.modMain.getClientEventsListener().playerTickPost(hudSession);
            }
            catch (Throwable t) {
                this.modMain.getMinimap().setCrashedWith(t);
            }
        }
    }

    public void handleRenderTickStart() {
        CustomUniforms.endFrame();
        if (class_310.method_1551().field_1724 != null) {
            if (!this.modMain.isLoadedClient()) {
                return;
            }
            this.modMain.getMinimap().checkCrashes();
            XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
            if (minimapSession != null) {
                MinimapProcessor minimap = minimapSession.getMinimapProcessor();
                minimap.getMinimapWriter().onRender();
            }
        }
    }

    public boolean handleRenderStatusEffectOverlay(class_332 guiGraphics) {
        if (!this.modMain.isLoadedClient()) {
            return false;
        }
        return this.modMain.getClientEventsListener().handleRenderStatusEffectOverlay(guiGraphics);
    }

    public boolean handleRenderCrosshairOverlay(class_332 guiGraphics) {
        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession != null) {
            return minimapSession.getMinimapProcessor().isEnlargedMap() && this.modMain.getSettings().centeredEnlarged;
        }
        return false;
    }

    public boolean handleForceToggleKeyMapping(class_4666 keyMapping) {
        if (KeyMappingTickHandler.DISABLE_KEY_MAPPING_OVERRIDES) {
            return false;
        }
        return this.modMain.getClientEventsListener().handleForceToggleKeyMapping(keyMapping);
    }
}

