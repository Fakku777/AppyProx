/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.class_124
 *  net.minecraft.class_1937
 *  net.minecraft.class_2558
 *  net.minecraft.class_2558$class_10609
 *  net.minecraft.class_2561
 *  net.minecraft.class_2568
 *  net.minecraft.class_2568$class_10613
 *  net.minecraft.class_310
 *  net.minecraft.class_410
 *  net.minecraft.class_437
 *  net.minecraft.class_5250
 *  net.minecraft.class_5321
 */
package xaero.hud.minimap.waypoint;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_124;
import net.minecraft.class_1937;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_310;
import net.minecraft.class_410;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_5321;
import xaero.common.HudMod;
import xaero.common.gui.GuiAddWaypoint;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldContainerUtil;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.path.XaeroPath;

public final class WaypointSharingHandler {
    public static final String WAYPOINT_OLD_SHARE_PREFIX = "xaero_waypoint:";
    public static final String WAYPOINT_ADD_PREFIX = "xaero_waypoint_add:";
    public static final String WAYPOINT_SHARE_PREFIX = "xaero-waypoint:";
    private static final String DESTINATION_PREFIX_INTERNAL = "Internal";
    private static final String DESTINATION_PREFIX_INTERNAL_HYPHEN = "Internal-";
    private static final String DESTINATION_PREFIX_EXTERNAL = "External";
    private HudMod modMain;
    private MinimapSession session;
    private class_437 confirmScreenParent;
    private Waypoint sharedWaypoint;
    private MinimapWorld minimapWorld;

    private WaypointSharingHandler(HudMod modMain, MinimapSession session) {
        this.modMain = modMain;
        this.session = session;
    }

    public void shareWaypoint(class_437 currentScreen, Waypoint waypoint, MinimapWorld minimapWorld) {
        this.confirmScreenParent = currentScreen;
        this.sharedWaypoint = waypoint;
        this.minimapWorld = minimapWorld;
        class_310.method_1551().method_1507((class_437)new class_410(this::onShareConfirmationResult, (class_2561)class_2561.method_43471((String)"gui.xaero_share_msg1"), (class_2561)class_2561.method_43471((String)"gui.xaero_share_msg2")));
    }

    public void onShareConfirmationResult(boolean confirmed) {
        if (!confirmed) {
            class_310.method_1551().method_1507(this.confirmScreenParent);
            return;
        }
        String destinationDetails = this.getSharedDestinationDetails(this.minimapWorld.getContainer());
        String message = WAYPOINT_SHARE_PREFIX + this.removeFormatting(this.sharedWaypoint.getNameSafe("^col^")) + ":" + this.removeFormatting(this.sharedWaypoint.getInitialsSafe("^col^")) + ":" + this.sharedWaypoint.getX() + ":" + String.valueOf(this.sharedWaypoint.isYIncluded() ? Integer.valueOf(this.sharedWaypoint.getY()) : "~") + ":" + this.sharedWaypoint.getZ() + ":" + this.sharedWaypoint.getWaypointColor().ordinal() + ":" + this.sharedWaypoint.isRotation() + ":" + this.sharedWaypoint.getYaw() + ":" + destinationDetails;
        class_310.method_1551().field_1705.method_1743().method_1803(message);
        class_310.method_1551().field_1724.field_3944.method_45729(message);
        class_310.method_1551().method_1507(null);
    }

    private String getSharedDestinationDetails(MinimapWorldContainer minimapWorldContainer) {
        MinimapWorldRootContainer autoRootContainer;
        MinimapWorldRootContainer rootContainer = minimapWorldContainer.getRoot();
        if (rootContainer != (autoRootContainer = this.session.getWorldManager().getAutoWorld().getContainer().getRoot())) {
            return DESTINATION_PREFIX_EXTERNAL;
        }
        XaeroPath containerPath = minimapWorldContainer.getPath();
        if (containerPath.getNodeCount() <= 1) {
            return DESTINATION_PREFIX_INTERNAL;
        }
        XaeroPath containerSubPath = containerPath.getSubPath(1);
        String dimKey = containerSubPath.getRoot().getLastNode();
        if (dimKey.equals("dim%0")) {
            dimKey = "overworld";
        } else if (dimKey.equals("dim%-1")) {
            dimKey = "the_nether";
        } else if (dimKey.equals("dim%1")) {
            dimKey = "the_end";
        }
        containerSubPath = XaeroPath.root(dimKey).resolve(containerSubPath.getSubPath(1));
        String subContainersString = containerSubPath.toString().replace(":", "^col^");
        return DESTINATION_PREFIX_INTERNAL_HYPHEN + this.removeFormatting(subContainersString);
    }

    public void onWaypointReceived(String playerName, String text) {
        boolean newFormat = (text = text.replaceAll("\u00a7.", "")).contains(WAYPOINT_SHARE_PREFIX);
        String sharePrefix = newFormat ? WAYPOINT_SHARE_PREFIX : WAYPOINT_OLD_SHARE_PREFIX;
        String[] args = text.substring(text.indexOf(sharePrefix)).split(":");
        if (args.length < 9) {
            MinimapLogs.LOGGER.info("Incorrect format of the shared waypoint! Error: 0");
            return;
        }
        if (newFormat) {
            args[1] = this.restoreFormatting(args[1]);
            args[2] = this.restoreFormatting(args[2]);
        }
        class_5250 waypointName = class_2561.method_43471((String)Waypoint.getStringFromStringSafe(args[1], "^col^"));
        class_2561 dimensionName = null;
        if (args.length > 9) {
            if (args[9].equals(DESTINATION_PREFIX_INTERNAL)) {
                XaeroPath potentialContainerPath = this.session.getWorldStateUpdater().getPotentialContainerPath();
                args[9] = this.getSharedDestinationDetails(this.session.getWorldManager().getWorldContainer(potentialContainerPath));
            }
            if (args[9].startsWith(DESTINATION_PREFIX_INTERNAL_HYPHEN)) {
                dimensionName = this.getReceivedDimensionName(args[9]);
            }
        }
        class_5250 mainComponent = class_2561.method_43469((String)(dimensionName != null ? "gui.xaero_waypoint_shared_dimension2" : "gui.xaero_waypoint_shared2"), (Object[])new Object[]{playerName, waypointName, dimensionName});
        StringBuilder addCommandBuilder = new StringBuilder();
        addCommandBuilder.append(WAYPOINT_ADD_PREFIX);
        addCommandBuilder.append(args[1]);
        for (int i = 2; i < args.length; ++i) {
            addCommandBuilder.append(':').append(args[i]);
        }
        String addCommand = addCommandBuilder.toString();
        class_5250 hoverComponent = class_2561.method_43470((String)(args[3] + ", " + args[4] + ", " + args[5]));
        class_2558.class_10609 clickEvent = new class_2558.class_10609("/" + addCommand);
        class_2568.class_10613 hoverEvent = new class_2568.class_10613((class_2561)hoverComponent);
        class_5250 addComponent = class_2561.method_43471((String)"gui.xaero_waypoint_shared_add").method_27692(class_124.field_1077).method_27692(class_124.field_1073);
        mainComponent.method_10855().add(addComponent);
        mainComponent.method_10862(mainComponent.method_10866().method_27706(class_124.field_1080).method_10958((class_2558)clickEvent).method_10949((class_2568)hoverEvent));
        class_310.method_1551().field_1705.method_1743().method_1812((class_2561)mainComponent);
    }

    private class_2561 getReceivedDimensionName(String destinationDetails) {
        int lastMinus = destinationDetails.lastIndexOf("-");
        if (lastMinus == -1) {
            return null;
        }
        String containerPathRaw = lastMinus == DESTINATION_PREFIX_INTERNAL.length() ? destinationDetails.substring(DESTINATION_PREFIX_INTERNAL_HYPHEN.length()) : destinationDetails.substring(DESTINATION_PREFIX_INTERNAL_HYPHEN.length(), lastMinus);
        String containerPathString = this.restoreFormatting(containerPathRaw.replace("^col^", ":"));
        if (containerPathString.contains("/")) {
            return class_2561.method_43470((String)containerPathString);
        }
        if (!containerPathString.startsWith("dim%")) {
            return class_2561.method_43470((String)containerPathString);
        }
        if (containerPathString.length() == 4) {
            return class_2561.method_43471((String)"gui.xaero_waypoint_unknown_dimension");
        }
        class_5321<class_1937> dimId = this.session.getDimensionHelper().getDimensionKeyForDirectoryName(containerPathString);
        if (dimId == null) {
            return class_2561.method_43471((String)"gui.xaero_waypoint_unknown_dimension");
        }
        return class_2561.method_43470((String)dimId.method_29177().method_12832());
    }

    public void onWaypointAdd(String[] args) {
        MinimapWorld externalWorld;
        int yaw;
        WaypointColor color;
        int z;
        int y;
        int x;
        String waypointName = Waypoint.getStringFromStringSafe(args[1], "^col^");
        if (waypointName.length() < 1 || waypointName.length() > 32) {
            MinimapLogs.LOGGER.info("Incorrect format of the shared waypoint! Error: 1");
            return;
        }
        String waypointSymbol = Waypoint.getStringFromStringSafe(args[2], "^col^");
        if (waypointSymbol.length() < 1 || waypointSymbol.length() > 3) {
            MinimapLogs.LOGGER.info("Incorrect format of the shared waypoint! Error: 2");
            return;
        }
        if (this.session.getWorldState().getAutoWorldPath() == null) {
            MinimapLogs.LOGGER.info("Can't add a waypoint at this time!");
            return;
        }
        boolean yIsIncluded = !args[4].equals("~");
        try {
            x = Integer.parseInt(args[3]);
            y = yIsIncluded ? Integer.parseInt(args[4]) : 0;
            z = Integer.parseInt(args[5]);
            int colorIndex = Integer.parseInt(args[6]);
            if (colorIndex < 0) {
                colorIndex = 0;
            }
            color = WaypointColor.fromIndex(colorIndex %= WaypointColor.values().length);
            String yawString = args[8];
            if (yawString.length() > 4) {
                MinimapLogs.LOGGER.info("Incorrect format of the shared waypoint! Error: 4");
                return;
            }
            yaw = Integer.parseInt(yawString);
        }
        catch (NumberFormatException nfe) {
            MinimapLogs.LOGGER.info("Incorrect format of the shared waypoint! Error: 3");
            return;
        }
        boolean rotation = args[7].equals("true");
        Waypoint waypoint = new Waypoint(x, y, z, waypointName, waypointSymbol, color, WaypointPurpose.NORMAL, false, yIsIncluded);
        waypoint.setRotation(rotation);
        waypoint.setYaw(yaw);
        MinimapWorld destinationWorld = externalWorld = this.session.getWorldManager().getCurrentWorld();
        if (args.length > 9 && (destinationWorld = this.getReceivedDestinationWorld(args[9], externalWorld)) == null) {
            return;
        }
        class_310.method_1551().method_1507((class_437)new GuiAddWaypoint(this.modMain, this.session, null, null, Lists.newArrayList((Object[])new Waypoint[]{waypoint}), destinationWorld.getContainer().getRoot().getPath(), destinationWorld, destinationWorld.getCurrentWaypointSetId(), true));
    }

    private MinimapWorld getReceivedDestinationWorld(String destinationDetails, MinimapWorld externalWorld) {
        MinimapWorld destinationWorld;
        if (destinationDetails.equals(DESTINATION_PREFIX_EXTERNAL)) {
            return externalWorld;
        }
        if (!destinationDetails.startsWith(DESTINATION_PREFIX_INTERNAL_HYPHEN) || destinationDetails.equals(DESTINATION_PREFIX_INTERNAL_HYPHEN)) {
            MinimapLogs.LOGGER.info("Incorrect format of the shared waypoint! Error: 12");
            return null;
        }
        int divider = destinationDetails.lastIndexOf(45);
        if (divider == DESTINATION_PREFIX_INTERNAL.length()) {
            divider = destinationDetails.length();
        }
        String containerPathString = destinationDetails.substring(DESTINATION_PREFIX_INTERNAL_HYPHEN.length(), divider);
        String[] containerPathNodes = (containerPathString = this.restoreFormatting(containerPathString.replace("^col^", ":"))).split("/");
        if (containerPathNodes.length != 1) {
            MinimapLogs.LOGGER.info("Incorrect format of the shared waypoint! Error: 8");
            return null;
        }
        for (int i = 0; i < containerPathNodes.length; ++i) {
            String s = containerPathNodes[i];
            if (!s.isEmpty()) continue;
            MinimapLogs.LOGGER.info("Incorrect format of the shared waypoint! Error: 11");
            return null;
        }
        Optional<class_5321<class_1937>> receivedDimId = this.getReceivedDimId(containerPathNodes);
        if (receivedDimId == null) {
            return null;
        }
        if (receivedDimId.isEmpty()) {
            return externalWorld;
        }
        class_5321<class_1937> dimId = receivedDimId.get();
        containerPathNodes[0] = this.session.getDimensionHelper().getDimensionDirectoryName(dimId);
        XaeroPath containerPath = this.session.getWorldState().getAutoRootContainerPath();
        for (String node : containerPathNodes) {
            containerPath = containerPath.resolve(node);
        }
        MinimapWorldRootContainer rootContainer = this.session.getWorldManager().getAutoRootContainer();
        rootContainer.renameOldContainer(containerPath);
        MinimapWorldContainer worldContainer = this.session.getWorldManager().getWorldContainer(containerPath);
        MinimapWorld autoWorld = this.session.getWorldManager().getAutoWorld();
        if (worldContainer == autoWorld.getContainer()) {
            destinationWorld = autoWorld;
        } else {
            destinationWorld = worldContainer.getFirstWorldConnectedTo(autoWorld);
            if (destinationWorld == null) {
                destinationWorld = worldContainer.getFirstWorld();
            }
            if (destinationWorld == null) {
                destinationWorld = worldContainer.addWorld(this.session.getWorldStateUpdater().getPotentialWorldNode(dimId, false));
            }
        }
        try {
            Path securityTest = containerPath.applyToFilePath(this.modMain.getMinimapFolder().toFile().getCanonicalFile().toPath()).resolve(destinationWorld.getNode() + "_1.txt");
            if (!securityTest.equals(securityTest.toFile().getCanonicalFile().toPath())) {
                MinimapLogs.LOGGER.info("Dangerously incorrect format of the shared waypoint! Error: 10");
                return null;
            }
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("IO exception during security check when adding a shared waypoint!", (Throwable)e);
            return null;
        }
        if (!this.modMain.getSupportMods().worldmap()) {
            return destinationWorld;
        }
        if (!MinimapWorldContainerUtil.isMultiplayer(containerPath)) {
            return destinationWorld;
        }
        List<String> worldmapMultiworldIds = this.modMain.getSupportMods().worldmapSupport.getMultiworldIds(dimId);
        for (String mw : worldmapMultiworldIds) {
            this.session.getWorldManager().addWorld(containerPath.resolve(mw));
        }
        return destinationWorld;
    }

    private Optional<class_5321<class_1937>> getReceivedDimId(String[] containerPathNodes) {
        class_5321<class_1937> dimId;
        String dimensionNode = containerPathNodes[0];
        if (!dimensionNode.startsWith("dim%")) {
            if (!dimensionNode.replaceAll("[^a-zA-Z0-9_]+", "").equals(dimensionNode)) {
                MinimapLogs.LOGGER.info("Incorrect format of the shared waypoint! Error: 18");
                return null;
            }
            dimId = this.session.getDimensionHelper().findDimensionKeyForOldName(class_310.method_1551().field_1724, dimensionNode);
        } else {
            dimId = this.session.getDimensionHelper().getDimensionKeyForDirectoryName(dimensionNode);
        }
        if (dimId == null) {
            MinimapLogs.LOGGER.info("Destination dimension doesn't exists! Handling waypoint as external.");
            return Optional.empty();
        }
        return Optional.of(dimId);
    }

    private String removeFormatting(String s) {
        return s.replace("-", "^min^").replace("_", "-").replace("*", "^ast^");
    }

    private String restoreFormatting(String s) {
        return s.replace("^ast^", "*").replace("-", "_").replace("^min^", "-");
    }

    public static final class Builder {
        private HudMod modMain;
        private MinimapSession session;

        private Builder() {
        }

        public Builder setDefault() {
            this.setModMain(null);
            this.setSession(null);
            return this;
        }

        public Builder setModMain(HudMod modMain) {
            this.modMain = modMain;
            return this;
        }

        public Builder setSession(MinimapSession session) {
            this.session = session;
            return this;
        }

        public WaypointSharingHandler build() {
            if (this.modMain == null || this.session == null) {
                throw new IllegalStateException();
            }
            return new WaypointSharingHandler(this.modMain, this.session);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

