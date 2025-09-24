/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_124
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_332
 *  net.minecraft.class_339
 *  net.minecraft.class_364
 *  net.minecraft.class_410
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_5321
 *  org.apache.commons.io.FileUtils
 */
package xaero.common.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.class_1074;
import net.minecraft.class_124;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5321;
import org.apache.commons.io.FileUtils;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.CursorBox;
import xaero.common.graphics.TextureUtils;
import xaero.common.gui.GuiTransfer;
import xaero.common.gui.GuiWaypoints;
import xaero.common.gui.GuiWorldTpCommand;
import xaero.common.gui.MyBigButton;
import xaero.common.gui.MyTinyButton;
import xaero.common.gui.ScreenBase;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldContainerUtil;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.path.XaeroPath;
import xaero.hud.render.util.GuiRenderUtil;

public class GuiWaypointsOptions
extends ScreenBase {
    private MinimapSession session;
    private MinimapWorldManager manager;
    private class_4185 automaticButton;
    private class_4185 subAutomaticButton;
    private class_4185 deleteButton;
    private class_4185 subDeleteButton;
    private class_4185 connectButton;
    private boolean buttonTest;
    private MinimapWorld minimapWorld;
    private MinimapWorld automaticMinimapWorld;
    private MinimapWorldRootContainer rootContainer;
    private boolean teleportationOptionShown;
    private boolean selectedWorldIsConnected;
    public CursorBox mwTooltip = new CursorBox("gui.xaero_use_multiworld_tooltip");
    public CursorBox teleportationTooltip = new CursorBox("gui.xaero_teleportation_tooltip", class_2583.field_24360.method_10977(class_124.field_1061));
    public CursorBox connectionTooltip = new CursorBox("gui.xaero_world_connection_tooltip");

    public GuiWaypointsOptions(IXaeroMinimap modMain, MinimapSession session, class_437 parent, class_437 escapeScreen, MinimapWorld minimapWorld, XaeroPath frozenAutoWorldPath) {
        super(modMain, parent, escapeScreen, (class_2561)class_2561.method_43471((String)"gui.xaero_options"));
        this.session = session;
        this.manager = this.session.getWorldManager();
        this.minimapWorld = minimapWorld;
        this.rootContainer = minimapWorld.getContainer().getRoot();
        this.automaticMinimapWorld = this.manager.getWorld(frozenAutoWorldPath);
        this.teleportationOptionShown = this.rootContainer.getConfig().isTeleportationEnabled();
    }

    @Override
    public void method_25426() {
        super.method_25426();
        this.parent.method_25410(this.field_22787, this.field_22789, this.field_22790);
        this.selectedWorldIsConnected = this.rootContainer.getSubWorldConnections().isConnected(this.automaticMinimapWorld, this.minimapWorld);
        this.method_37063((class_364)new MyTinyButton(this.field_22789 / 2 - 203, 32, (class_2561)class_2561.method_43469((String)"gui.xaero_close", (Object[])new Object[0]), b -> this.actionPerformed(b, 5)));
        this.method_37063((class_364)new MyBigButton(6, this.field_22789 / 2 - 203, 57, (class_2561)class_2561.method_43469((String)"gui.xaero_transfer", (Object[])new Object[0]), b -> this.actionPerformed(b, 6)));
        this.automaticButton = new MyBigButton(7, this.field_22789 / 2 - 203, 82, (class_2561)class_2561.method_43469((String)"gui.xaero_make_automatic", (Object[])new Object[0]), b -> this.actionPerformed(b, 7));
        this.method_37063((class_364)this.automaticButton);
        this.subAutomaticButton = new MyBigButton(8, this.field_22789 / 2 - 203, 107, (class_2561)class_2561.method_43469((String)"gui.xaero_make_multi_automatic", (Object[])new Object[0]), b -> this.actionPerformed(b, 8));
        this.method_37063((class_364)this.subAutomaticButton);
        this.deleteButton = new MyBigButton(9, this.field_22789 / 2 - 203, 132, (class_2561)class_2561.method_43469((String)"gui.xaero_delete_world", (Object[])new Object[0]), b -> this.actionPerformed(b, 9));
        this.method_37063((class_364)this.deleteButton);
        this.subDeleteButton = new MyBigButton(10, this.field_22789 / 2 - 203, 157, (class_2561)class_2561.method_43469((String)"gui.xaero_delete_multi_world", (Object[])new Object[0]), b -> this.actionPerformed(b, 10));
        this.method_37063((class_364)this.subDeleteButton);
        this.method_37063((class_364)new MyBigButton(200, this.field_22789 / 2 + 3, 57, (class_2561)class_2561.method_43470((String)this.getConfigButtonName(0)), b -> this.onConfigButtonClick((MyBigButton)b)));
        MyBigButton teleportationEnabledButton = (MyBigButton)this.method_37063((class_364)new MyBigButton(201, this.field_22789 / 2 + 3, 82, (class_2561)class_2561.method_43470((String)this.getConfigButtonName(1)), b -> this.onConfigButtonClick((MyBigButton)b)));
        teleportationEnabledButton.field_22763 = this.teleportationOptionShown;
        this.method_37063((class_364)new MyBigButton(13, this.field_22789 / 2 + 3, 107, (class_2561)class_2561.method_43471((String)"gui.xaero_world_teleport_command"), b -> this.actionPerformed(b, 13)));
        this.connectButton = new MyBigButton(14, this.field_22789 / 2 + 3, 132, (class_2561)class_2561.method_43470((String)this.getConfigButtonName(4)), b -> this.actionPerformed(b, 14));
        this.method_37063((class_364)this.connectButton);
        this.connectButton.field_22763 = MinimapWorldContainerUtil.isMultiplayer(this.rootContainer.getPath()) && this.rootContainer == this.automaticMinimapWorld.getContainer().getRoot();
        this.method_37063((class_364)new MyBigButton(202, this.field_22789 / 2 + 3, 182, (class_2561)class_2561.method_43470((String)this.getConfigButtonName(2)), b -> this.onConfigButtonClick((MyBigButton)b)));
        this.method_37063((class_364)new MyBigButton(203, this.field_22789 / 2 + 3, 207, (class_2561)class_2561.method_43470((String)this.getConfigButtonName(3)), b -> this.onConfigButtonClick((MyBigButton)b)));
    }

    private String getConfigButtonName(int buttonId) {
        switch (buttonId) {
            case 0: {
                return class_1074.method_4662((String)"gui.xaero_use_multiworld", (Object[])new Object[0]) + ": " + ModSettings.getTranslation(this.rootContainer.getConfig().isUsingMultiworldDetection());
            }
            case 1: {
                return class_1074.method_4662((String)"gui.xaero_teleportation", (Object[])new Object[0]) + ": " + ModSettings.getTranslation(this.rootContainer.getConfig().isTeleportationEnabled());
            }
            case 2: {
                return class_1074.method_4662((String)"gui.xaero_sort", (Object[])new Object[0]) + ": " + class_1074.method_4662((String)this.rootContainer.getConfig().getSortType().optionName, (Object[])new Object[0]);
            }
            case 3: {
                return class_1074.method_4662((String)"gui.xaero_sort_reversed", (Object[])new Object[0]) + ": " + ModSettings.getTranslation(this.rootContainer.getConfig().isSortReversed());
            }
            case 4: {
                return this.selectedWorldIsConnected ? class_1074.method_4662((String)"gui.xaero_disconnect_from_auto", (Object[])new Object[0]) : class_1074.method_4662((String)"gui.xaero_connect_with_auto", (Object[])new Object[0]);
            }
        }
        return "";
    }

    private void onConfigButtonClick(MyBigButton button) {
        this.buttonTest = true;
        MinimapWorldRootContainer wc = this.rootContainer;
        switch (button.getId() - 200) {
            case 0: {
                wc.getConfig().setUsingMultiworldDetection(!this.rootContainer.getConfig().isUsingMultiworldDetection());
                wc.getConfig().setDefaultMultiworldId(null);
                break;
            }
            case 1: {
                wc.getConfig().setTeleportationEnabled(!wc.getConfig().isTeleportationEnabled());
                break;
            }
            case 2: {
                this.rootContainer.getConfig().toggleSortType();
                this.parent.method_25423(this.field_22787, this.field_22789, this.field_22790);
                break;
            }
            case 3: {
                this.rootContainer.getConfig().toggleSortReversed();
                this.parent.method_25423(this.field_22787, this.field_22789, this.field_22790);
            }
        }
        this.session.getWorldManagerIO().getRootConfigIO().save(wc);
        button.method_25355((class_2561)class_2561.method_43470((String)this.getConfigButtonName(button.getId() - 200)));
    }

    @Override
    public boolean method_25402(double par1, double par2, int par3) {
        this.buttonTest = false;
        boolean toReturn = super.method_25402(par1, par2, par3);
        if (!this.buttonTest) {
            this.goBack();
        }
        return toReturn;
    }

    protected void actionPerformed(class_4185 p_146284_1_, int id) {
        this.buttonTest = true;
        if (p_146284_1_.field_22763) {
            switch (id) {
                case 5: {
                    this.goBack();
                    break;
                }
                case 6: {
                    this.field_22787.method_1507((class_437)new GuiTransfer(this.modMain, this.session, this.parent, this.escape));
                    break;
                }
                case 7: {
                    this.field_22787.method_1507((class_437)new class_410(result -> this.confirmResult(result, id), (class_2561)class_2561.method_43471((String)"gui.xaero_make_automatic_msg1"), (class_2561)class_2561.method_43471((String)"gui.xaero_make_automatic_msg2")));
                    break;
                }
                case 8: {
                    this.field_22787.method_1507((class_437)new class_410(result -> this.confirmResult(result, id), (class_2561)class_2561.method_43471((String)"gui.xaero_make_multi_automatic_msg1"), (class_2561)class_2561.method_43471((String)"gui.xaero_make_multi_automatic_msg2")));
                    break;
                }
                case 9: {
                    this.field_22787.method_1507((class_437)new class_410(result -> this.confirmResult(result, id), (class_2561)class_2561.method_43471((String)"gui.xaero_delete_world_msg1"), (class_2561)class_2561.method_43471((String)"gui.xaero_delete_world_msg2")));
                    break;
                }
                case 10: {
                    this.field_22787.method_1507((class_437)new class_410(result -> this.confirmResult(result, id), (class_2561)class_2561.method_43471((String)"gui.xaero_delete_multi_world_msg1"), (class_2561)class_2561.method_43471((String)"gui.xaero_delete_multi_world_msg2")));
                    break;
                }
                case 11: {
                    this.field_22787.method_1507((class_437)new class_410(result -> this.confirmResult(result, id), (class_2561)class_2561.method_43471((String)"gui.xaero_multiply_msg1"), (class_2561)class_2561.method_43471((String)"gui.xaero_multiply_msg2")));
                    break;
                }
                case 12: {
                    this.field_22787.method_1507((class_437)new class_410(result -> this.confirmResult(result, id), (class_2561)class_2561.method_43471((String)"gui.xaero_multiply_msg1"), (class_2561)class_2561.method_43471((String)"gui.xaero_divide_msg2")));
                    break;
                }
                case 13: {
                    this.field_22787.method_1507((class_437)new GuiWorldTpCommand(this.modMain, (class_437)this, this.escape, this.minimapWorld.getContainer().getRoot()));
                    break;
                }
                case 14: {
                    MinimapWorldContainer autoContainer = this.automaticMinimapWorld.getContainer();
                    MinimapWorldContainer selectedContainer = this.minimapWorld.getContainer();
                    String autoWorldName = autoContainer.getFullWorldName(this.automaticMinimapWorld.getNode(), autoContainer.getSubName()) + " (auto)";
                    String selectedWorldName = selectedContainer.getFullWorldName(this.minimapWorld.getNode(), selectedContainer.getSubName());
                    String connectionDisplayString = autoWorldName + "   \u00a7e<=>\u00a7r   " + selectedWorldName;
                    if (this.selectedWorldIsConnected) {
                        this.field_22787.method_1507((class_437)new class_410(result -> this.confirmResult(result, id), (class_2561)class_2561.method_43471((String)"gui.xaero_disconnect_from_auto_msg"), (class_2561)class_2561.method_43470((String)connectionDisplayString)));
                        break;
                    }
                    this.field_22787.method_1507((class_437)new class_410(result -> this.confirmResult(result, id), (class_2561)class_2561.method_43471((String)"gui.xaero_connect_with_auto_msg"), (class_2561)class_2561.method_43470((String)connectionDisplayString)));
                }
            }
        }
    }

    public void confirmResult(boolean result, int id) {
        block30: {
            block29: {
                boolean differentRoot = this.isDifferentRootContainer();
                boolean differentSub = this.isDifferentSubWorld(differentRoot);
                boolean exitOptions = true;
                if (!result) break block29;
                switch (id) {
                    case 7: {
                        if (!differentRoot) break;
                        MinimapWorldRootContainer selected = this.rootContainer;
                        MinimapWorldRootContainer auto = this.manager.getAutoRootContainer();
                        if (selected == null || auto == null) break;
                        XaeroPath buKey = selected.getPath();
                        this.manager.removeContainer(selected.getPath());
                        this.manager.removeContainer(auto.getPath());
                        selected.setPath(auto.getPath());
                        auto.setPath(buKey);
                        this.manager.addRootWorldContainer(selected);
                        this.manager.addRootWorldContainer(auto);
                        selected.updateConnectionsField(this.session.getWaypointSession());
                        auto.updateConnectionsField(this.session.getWaypointSession());
                        Path selectedPath = selected.getDirectoryPath();
                        Path autoPath = auto.getDirectoryPath();
                        Path tempFolder = this.modMain.getMinimapFolder().resolve("temp_to_add");
                        try {
                            Files.createDirectories(tempFolder, new FileAttribute[0]);
                            Path selectedTemp = tempFolder.resolve(selectedPath.getFileName());
                            if (Files.exists(selectedPath, new LinkOption[0])) {
                                Files.move(selectedPath, selectedTemp, new CopyOption[0]);
                            }
                            if (Files.exists(autoPath, new LinkOption[0])) {
                                Files.move(autoPath, selectedPath, new CopyOption[0]);
                            }
                            if (Files.exists(selectedTemp, new LinkOption[0])) {
                                Files.move(selectedTemp, autoPath, new CopyOption[0]);
                            }
                            Files.deleteIfExists(tempFolder);
                            this.session.getWorldManagerIO().getRootConfigIO().load(selected);
                            this.session.getWorldManagerIO().getRootConfigIO().load(auto);
                        }
                        catch (Throwable e) {
                            this.modMain.getMinimap().setCrashedWith(e);
                        }
                        this.session.getWorldState().setCustomWorldPath(null);
                        break;
                    }
                    case 8: {
                        if (!differentSub) break;
                        MinimapWorld autoWorld = this.automaticMinimapWorld;
                        MinimapWorld selectedWorld = this.minimapWorld;
                        try {
                            Path autoFile = this.session.getWorldManagerIO().getWorldFile(autoWorld);
                            Path selectedFile = this.session.getWorldManagerIO().getWorldFile(selectedWorld);
                            Path autoTempFile = autoFile.getParent().resolve("temp_to_add").resolve(autoFile.getFileName());
                            Files.createDirectories(autoTempFile.getParent(), new FileAttribute[0]);
                            if (!Files.exists(autoFile, new LinkOption[0])) {
                                Files.createFile(autoFile, new FileAttribute[0]);
                            }
                            Files.move(autoFile, autoTempFile, new CopyOption[0]);
                            if (!Files.exists(selectedFile, new LinkOption[0])) {
                                Files.createFile(selectedFile, new FileAttribute[0]);
                            }
                            Files.move(selectedFile, autoFile, new CopyOption[0]);
                            if (Files.exists(autoTempFile, new LinkOption[0])) {
                                Files.move(autoTempFile, selectedFile, new CopyOption[0]);
                            }
                            Files.deleteIfExists(autoTempFile.getParent());
                        }
                        catch (Throwable e) {
                            this.modMain.getMinimap().setCrashedWith(e);
                            break;
                        }
                        MinimapWorldContainer autoWc = autoWorld.getContainer();
                        MinimapWorldContainer selectedWc = selectedWorld.getContainer();
                        autoWorld.setContainer(selectedWc);
                        selectedWorld.setContainer(autoWc);
                        selectedWc.removeWorld(selectedWorld.getNode());
                        autoWc.removeWorld(autoWorld.getNode());
                        String buSelected = selectedWorld.getNode();
                        selectedWorld.setNode(autoWorld.getNode());
                        autoWorld.setNode(buSelected);
                        selectedWc.addWorld(autoWorld);
                        autoWc.addWorld(selectedWorld);
                        class_5321<class_1937> buDimId = selectedWorld.getDimId();
                        selectedWorld.setDimId(autoWorld.getDimId());
                        autoWorld.setDimId(buDimId);
                        this.rootContainer.getSubWorldConnections().swapConnections(autoWorld, selectedWorld);
                        this.session.getWorldManagerIO().getRootConfigIO().save(this.rootContainer);
                        this.session.getWorldState().setCustomWorldPath(null);
                        break;
                    }
                    case 9: {
                        if (!differentRoot) break;
                        XaeroPath selectedRootContainerId = this.rootContainer.getPath();
                        try {
                            File directory = selectedRootContainerId.applyToFilePath(this.modMain.getMinimapFolder()).toFile();
                            if (directory.exists()) {
                                FileUtils.deleteDirectory((File)directory);
                            }
                        }
                        catch (Throwable e) {
                            this.modMain.getMinimap().setCrashedWith(e);
                            break;
                        }
                        this.manager.removeContainer(selectedRootContainerId);
                        this.session.getWorldState().setCustomWorldPath(null);
                        break;
                    }
                    case 10: {
                        if (!differentSub) break;
                        MinimapWorld selectedWorld = this.minimapWorld;
                        try {
                            Files.deleteIfExists(this.session.getWorldManagerIO().getWorldFile(selectedWorld));
                        }
                        catch (IOException e) {
                            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
                        }
                        selectedWorld.getContainer().removeWorld(selectedWorld.getNode());
                        selectedWorld.getContainer().removeName(selectedWorld.getNode());
                        this.session.getWorldState().setCustomWorldPath(null);
                        break;
                    }
                    case 11: {
                        this.multiplyWaypoints(this.minimapWorld, 8.0);
                        break;
                    }
                    case 12: {
                        this.multiplyWaypoints(this.minimapWorld, 0.125);
                        break;
                    }
                    case 14: {
                        if (!this.selectedWorldIsConnected) {
                            this.rootContainer.getSubWorldConnections().addConnection(this.automaticMinimapWorld, this.minimapWorld);
                        } else {
                            this.rootContainer.getSubWorldConnections().removeConnection(this.automaticMinimapWorld, this.minimapWorld);
                        }
                        this.session.getWorldManagerIO().getRootConfigIO().save(this.rootContainer);
                    }
                }
                if (exitOptions) {
                    if (this.parent instanceof GuiWaypoints) {
                        this.field_22787.method_1507((class_437)new GuiWaypoints((HudMod)this.modMain, this.session, ((GuiWaypoints)this.parent).parent, this.escape));
                    } else {
                        this.goBack();
                    }
                }
                break block30;
            }
            this.field_22787.method_1507((class_437)this);
        }
    }

    private void multiplyWaypoints(MinimapWorld world, double factor) {
        for (WaypointSet set : world.getIterableWaypointSets()) {
            for (Waypoint wp : set.getWaypoints()) {
                wp.setX((int)Math.floor((double)wp.getX() * factor));
                wp.setZ((int)Math.floor((double)wp.getZ() * factor));
            }
        }
        try {
            this.session.getWorldManagerIO().saveWorld(world);
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    private boolean isDifferentRootContainer() {
        return this.session.getWorldState().getAutoRootContainerPath() != null && !this.session.getWorldState().getAutoRootContainerPath().equals(this.rootContainer.getPath());
    }

    private boolean isDifferentSubWorld(boolean differentRoot) {
        return !differentRoot && this.minimapWorld != this.automaticMinimapWorld;
    }

    public void method_25420(class_332 guiGraphics, int par1, int par2, float par3) {
        this.parent.method_47413(guiGraphics, 0, 0, par3);
        GuiRenderUtil.flushGUI();
        TextureUtils.clearRenderTargetDepth(this.field_22787.method_1522(), 1.0f);
        super.method_25420(guiGraphics, par1, par2, par3);
    }

    @Override
    public void method_25394(class_332 guiGraphics, int par1, int par2, float par3) {
        this.automaticButton.field_22763 = this.deleteButton.field_22763 = this.isDifferentRootContainer();
        this.subAutomaticButton.field_22763 = this.subDeleteButton.field_22763 = this.isDifferentSubWorld(this.automaticButton.field_22763);
        super.method_25394(guiGraphics, par1, par2, par3);
        for (class_364 el : this.method_25396()) {
            MyBigButton b;
            class_339 w;
            if (!(el instanceof class_339) || !((w = (class_339)el) instanceof MyBigButton) || par1 < (b = (MyBigButton)w).method_46426() || par2 < b.method_46427() || par1 >= b.method_46426() + b.method_25368() || par2 >= b.method_46427() + 20) continue;
            if (b.getId() >= 200) {
                switch (b.getId() - 200) {
                    case 0: {
                        this.mwTooltip.drawBox(guiGraphics, par1, par2, this.field_22789, this.field_22790);
                        break;
                    }
                    case 1: {
                        this.teleportationTooltip.drawBox(guiGraphics, par1, par2, this.field_22789, this.field_22790);
                    }
                }
                continue;
            }
            if (b.getId() != 14 || !b.field_22763) continue;
            this.connectionTooltip.drawBox(guiGraphics, par1, par2, this.field_22789, this.field_22790);
        }
    }
}

