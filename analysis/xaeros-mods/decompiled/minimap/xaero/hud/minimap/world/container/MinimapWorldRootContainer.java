/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1132
 *  net.minecraft.class_1937
 *  net.minecraft.class_2874
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3218
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  net.minecraft.class_7134
 *  net.minecraft.class_7924
 */
package xaero.hud.minimap.world.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.class_1132;
import net.minecraft.class_1937;
import net.minecraft.class_2874;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3218;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_7134;
import net.minecraft.class_7924;
import xaero.common.HudMod;
import xaero.common.file.SimpleBackup;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointSession;
import xaero.hud.minimap.waypoint.server.ServerWaypointManager;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.connection.MinimapWorldConnectionManager;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldContainerUtil;
import xaero.hud.minimap.world.container.config.RootConfig;
import xaero.hud.path.XaeroPath;

public class MinimapWorldRootContainer
extends MinimapWorldContainer {
    private final RootConfig config;
    private final Map<class_5321<class_1937>, class_2960> dimensionTypeIds;
    private final Map<class_5321<class_1937>, class_2874> dimensionTypes;

    protected MinimapWorldRootContainer(HudMod modMain, MinimapSession session, Map<String, MinimapWorldContainer> subContainers, Map<String, MinimapWorld> worlds, Map<String, String> worldNames, ServerWaypointManager serverWaypointManager, XaeroPath path, RootConfig config, Map<class_5321<class_1937>, class_2960> dimensionTypeIds, Map<class_5321<class_1937>, class_2874> dimensionTypes) {
        super(modMain, session, subContainers, worlds, worldNames, serverWaypointManager, path, null);
        this.config = config;
        this.dimensionTypeIds = dimensionTypeIds;
        this.dimensionTypes = dimensionTypes;
    }

    public void updateConnectionsField(WaypointSession session) {
        this.config.resetSubWorldConnections(MinimapWorldContainerUtil.isMultiplayer(this.path));
    }

    public MinimapWorldConnectionManager getSubWorldConnections() {
        return this.config.getSubWorldConnections();
    }

    public class_2874 getDimensionType(class_5321<class_1937> dimId) {
        class_2874 dimensionType = this.dimensionTypes.get(dimId);
        if (dimensionType != null) {
            return dimensionType;
        }
        class_2960 dimensionTypeId = this.dimensionTypeIds.get(dimId);
        if (dimensionTypeId == null) {
            if (dimId == class_1937.field_25180) {
                dimensionTypeId = class_7134.field_37671;
            } else if (dimId == class_1937.field_25179) {
                dimensionTypeId = class_7134.field_37670;
            } else if (dimId == class_1937.field_25181) {
                dimensionTypeId = class_7134.field_37672;
            } else {
                class_1132 integratedServer = class_310.method_1551().method_1576();
                if (integratedServer == null) {
                    return null;
                }
                class_3218 serverLevel = integratedServer.method_3847(dimId);
                if (serverLevel == null) {
                    return null;
                }
                this.dimensionTypes.put(dimId, serverLevel.method_8597());
                return serverLevel.method_8597();
            }
        }
        if ((dimensionType = (class_2874)class_310.method_1551().field_1687.method_30349().method_30530(class_7924.field_41241).method_63535(dimensionTypeId)) != null) {
            this.dimensionTypes.put(dimId, dimensionType);
        }
        return dimensionType;
    }

    public double getDimensionScale(class_5321<class_1937> dimId) {
        class_2874 dimType = this.getDimensionType(dimId);
        if (dimType == null) {
            return 1.0;
        }
        return dimType.comp_646();
    }

    public void updateDimensionType(class_638 level) {
        class_5321 dimId = level.method_27983();
        class_5321 dimTypeId = (class_5321)level.method_40134().method_40230().get();
        class_2874 dimType = level.method_8597();
        if (Objects.equals(this.dimensionTypeIds.get(dimId), dimTypeId.method_29177())) {
            return;
        }
        this.dimensionTypes.put((class_5321<class_1937>)dimId, dimType);
        this.dimensionTypeIds.put((class_5321<class_1937>)dimId, dimTypeId.method_29177());
        this.session.getWorldManagerIO().getRootConfigIO().save(this);
    }

    public void renameOldContainer(XaeroPath containerPath) {
        if (this.subContainers.isEmpty()) {
            return;
        }
        String dimensionPart = containerPath.getAtIndex(1).getLastNode();
        if (this.subContainers.containsKey(dimensionPart)) {
            return;
        }
        class_5321<class_1937> dimId = this.session.getDimensionHelper().getDimensionKeyForDirectoryName(dimensionPart);
        if (dimId == null) {
            return;
        }
        class_2960 dimKey = dimId.method_29177();
        String dimKeyOldValidation = dimKey.method_12832().replaceAll("[^a-zA-Z0-9_]+", "");
        XaeroPath customWorldPath = this.session.getWorldState().getCustomWorldPath();
        MinimapWorldContainer currentCustomContainer = customWorldPath == null ? null : this.session.getWorldManager().getWorld(customWorldPath).getContainer();
        for (Map.Entry subContainerEntry : this.subContainers.entrySet()) {
            String subKey = (String)subContainerEntry.getKey();
            if (!subKey.equals(dimKeyOldValidation)) continue;
            MinimapWorldContainer dimContainer = (MinimapWorldContainer)subContainerEntry.getValue();
            boolean currentlySelected = currentCustomContainer != null && currentCustomContainer.getPath().isSubOf(dimContainer.getPath());
            this.subContainers.put(dimensionPart, dimContainer);
            this.subContainers.remove(subKey);
            SimpleBackup.moveToBackup(dimContainer.getDirectoryPath());
            dimContainer.setPath(this.path.resolve(dimensionPart));
            if (currentlySelected) {
                this.session.getWorldState().setCustomWorldPath(dimContainer.getPath().resolve(customWorldPath.getSubPath(2)));
            }
            try {
                this.session.getWorldManagerIO().saveWorlds(this);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to rename a dimension! Can't continue.", e);
            }
            MinimapWorldConnectionManager connections = this.getSubWorldConnections();
            connections.renameDimension(subKey, dimensionPart);
            this.session.getWorldManagerIO().getRootConfigIO().save(this);
            return;
        }
    }

    public Iterable<Map.Entry<class_5321<class_1937>, class_2960>> getDimensionTypeIds() {
        return this.dimensionTypeIds.entrySet();
    }

    public void setDimensionTypeId(class_5321<class_1937> dim, class_2960 dimType) {
        this.dimensionTypes.remove(dim);
        this.dimensionTypeIds.put(dim, dimType);
    }

    @Override
    public MinimapWorldRootContainer getRoot() {
        return this;
    }

    public boolean isConfigLoaded() {
        return this.config.isLoaded();
    }

    public RootConfig getConfig() {
        return this.config;
    }

    public static final class Builder
    extends MinimapWorldContainer.Builder<Builder> {
        private Builder() {
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            return this;
        }

        @Override
        public MinimapWorldRootContainer build() {
            return (MinimapWorldRootContainer)super.build();
        }

        @Override
        protected MinimapWorldContainer buildInternally(Map<String, MinimapWorldContainer> subContainers, Map<String, MinimapWorld> worlds, Map<String, String> worldNames, ServerWaypointManager serverWaypointManager) {
            return new MinimapWorldRootContainer(this.modMain, this.session, subContainers, worlds, worldNames, serverWaypointManager, this.path, new RootConfig(MinimapWorldContainerUtil.isMultiplayer(this.path)), new HashMap<class_5321<class_1937>, class_2960>(), new HashMap<class_5321<class_1937>, class_2874>());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

