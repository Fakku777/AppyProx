/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.class_1041
 *  net.minecraft.class_1074
 *  net.minecraft.class_1657
 *  net.minecraft.class_1936
 *  net.minecraft.class_2338
 *  net.minecraft.class_2556$class_7602
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_3218
 *  net.minecraft.class_332
 *  net.minecraft.class_4358
 *  net.minecraft.class_437
 *  net.minecraft.class_4398
 *  net.minecraft.class_442
 *  net.minecraft.class_4439
 *  net.minecraft.class_4877
 *  net.minecraft.class_638
 */
package xaero.map.events;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Queue;
import net.minecraft.class_1041;
import net.minecraft.class_1074;
import net.minecraft.class_1657;
import net.minecraft.class_1936;
import net.minecraft.class_2338;
import net.minecraft.class_2556;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_3218;
import net.minecraft.class_332;
import net.minecraft.class_4358;
import net.minecraft.class_437;
import net.minecraft.class_4398;
import net.minecraft.class_442;
import net.minecraft.class_4439;
import net.minecraft.class_4877;
import net.minecraft.class_638;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.WorldMapSession;
import xaero.map.file.worldsave.WorldDataHandler;
import xaero.map.graphics.shader.CustomUniforms;
import xaero.map.misc.Misc;
import xaero.map.mods.SupportMods;
import xaero.map.patreon.GuiUpdateAll;
import xaero.map.patreon.Patreon;

public class ClientEvents {
    private class_4877 latestRealm;
    private Field realmsTaskField;
    private Field realmsTaskServerField;

    public class_437 handleGuiOpen(class_437 gui) {
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
                WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
            }
        }
        return gui;
    }

    public boolean handleRenderTick(boolean start) {
        WorldMapSession worldmapSession;
        if (!WorldMap.loaded) {
            return false;
        }
        class_310 mc = class_310.method_1551();
        if (!start) {
            WorldMap.gpuObjectDeleter.work();
            CustomUniforms.endFrame();
        }
        boolean shouldCancelGameRender = false;
        if (mc.field_1724 != null && (worldmapSession = WorldMapSession.getCurrentSession()) != null) {
            MapProcessor mapProcessor = worldmapSession.getMapProcessor();
            if (!start) {
                mapProcessor.onRenderProcess(mc);
                mapProcessor.resetRenderStartTime();
                Queue<Runnable> minecraftScheduledTasks = mapProcessor.getMinecraftScheduledTasks();
                Runnable task = mapProcessor.getRenderStartTimeUpdater();
                Runnable[] currentTasks = minecraftScheduledTasks.toArray(new Runnable[0]);
                minecraftScheduledTasks.clear();
                minecraftScheduledTasks.add(task);
                for (Runnable t : currentTasks) {
                    minecraftScheduledTasks.add(t);
                }
            } else {
                if (!SupportMods.vivecraft && Misc.screenShouldSkipWorldRender(mc.field_1755, true)) {
                    mc.field_1687.method_38534();
                    mc.field_1687.method_2935().method_12130().method_15516();
                    shouldCancelGameRender = true;
                }
                if (mapProcessor != null) {
                    mapProcessor.setMainValues();
                }
            }
        }
        return shouldCancelGameRender;
    }

    public void handleDrawScreen(class_437 gui) {
        if (Patreon.needsNotification() && gui instanceof class_442 && !SupportMods.minimap()) {
            class_310.method_1551().method_1507((class_437)new GuiUpdateAll());
        } else if (WorldMap.isOutdated) {
            WorldMap.isOutdated = false;
        }
    }

    public void handlePlayerSetSpawnEvent(class_2338 spawn, class_638 world) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession != null) {
            MapProcessor mapProcessor = worldmapSession.getMapProcessor();
            mapProcessor.updateWorldSpawn(spawn, world);
        }
    }

    public void handleWorldUnload(class_1936 world) {
        WorldMapSession worldmapSession;
        if (class_310.method_1551().field_1724 != null && (worldmapSession = WorldMapSession.getCurrentSession()) != null) {
            MapProcessor mapProcessor = worldmapSession.getMapProcessor();
            if (world == mapProcessor.mainWorld) {
                mapProcessor.onWorldUnload();
            }
        }
        if (world instanceof class_3218) {
            class_3218 sw = (class_3218)world;
            WorldDataHandler.onServerWorldUnload(sw);
        }
    }

    public class_4877 getLatestRealm() {
        return this.latestRealm;
    }

    public boolean handleRenderCrosshairOverlay(class_332 guiGraphics) {
        String crosshairMessage;
        if (class_310.method_1551().field_1690.field_1842) {
            return false;
        }
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession == null ? null : worldmapSession.getMapProcessor();
        String string = crosshairMessage = mapProcessor == null ? null : mapProcessor.getCrosshairMessage();
        if (crosshairMessage != null) {
            int messageWidth = class_310.method_1551().field_1772.method_1727(crosshairMessage);
            class_1041 window = class_310.method_1551().method_22683();
            guiGraphics.method_25303(class_310.method_1551().field_1772, crosshairMessage, window.method_4486() / 2 - messageWidth / 2, window.method_4502() / 2 + 60, -1);
        }
        return false;
    }

    public boolean handleClientPlayerChatReceivedEvent(class_2556.class_7602 chatType, class_2561 component, GameProfile gameProfile) {
        if (component == null) {
            return false;
        }
        return this.handleChatMessage(gameProfile == null ? null : gameProfile.getName(), component);
    }

    public boolean handleClientSystemChatReceivedEvent(class_2561 component) {
        WorldMapSession worldmapSession;
        if (component == null) {
            return false;
        }
        String textString = component.getString();
        if (textString.contains("\u00a7r\u00a7e\u00a7s\u00a7e\u00a7t\u00a7x\u00a7a\u00a7e\u00a7r\u00a7o")) {
            worldmapSession = WorldMapSession.getCurrentSession();
            worldmapSession.getMapProcessor().setConsideringNetherFairPlayMessage(false);
        }
        if (textString.contains("\u00a7x\u00a7a\u00a7e\u00a7r\u00a7o\u00a7w\u00a7m\u00a7n\u00a7e\u00a7t\u00a7h\u00a7e\u00a7r\u00a7i\u00a7s\u00a7f\u00a7a\u00a7i\u00a7r")) {
            worldmapSession = WorldMapSession.getCurrentSession();
            worldmapSession.getMapProcessor().setConsideringNetherFairPlayMessage(true);
        }
        return this.handleChatMessage(class_1074.method_4662((String)"gui.xaero_waypoint_server_shared", (Object[])new Object[0]), component);
    }

    private boolean handleChatMessage(String playerName, class_2561 text) {
        return false;
    }

    public void handlePlayerTickStart(class_1657 player) {
        WorldMapSession worldmapSession;
        if (player == class_310.method_1551().field_1724 && (worldmapSession = WorldMapSession.getCurrentSession()) != null) {
            worldmapSession.getControlsHandler().handleKeyEvents();
        }
    }

    public void handleClientTickStart() {
        if (class_310.method_1551().field_1724 != null) {
            if (!WorldMap.loaded) {
                return;
            }
            WorldMap.crashHandler.checkForCrashes();
            WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession != null) {
                MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                mapProcessor.onClientTickStart();
            }
        }
    }

    public void handleClientRunTickStart() {
        if (class_310.method_1551().field_1724 != null) {
            if (!WorldMap.loaded) {
                return;
            }
            WorldMap.crashHandler.checkForCrashes();
            WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession != null) {
                worldmapSession.getMapProcessor().getWorldDataHandler().handleRenderExecutor();
            }
        }
    }
}

