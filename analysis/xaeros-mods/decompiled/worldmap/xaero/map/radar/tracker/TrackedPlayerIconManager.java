/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.class_1068
 *  net.minecraft.class_1657
 *  net.minecraft.class_2960
 *  net.minecraft.class_640
 *  net.minecraft.class_742
 */
package xaero.map.radar.tracker;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1068;
import net.minecraft.class_1657;
import net.minecraft.class_2960;
import net.minecraft.class_640;
import net.minecraft.class_742;
import xaero.map.element.MapElementGraphics;
import xaero.map.icon.XaeroIcon;
import xaero.map.icon.XaeroIconAtlas;
import xaero.map.icon.XaeroIconAtlasManager;
import xaero.map.radar.tracker.PlayerTrackerMapElement;
import xaero.map.radar.tracker.TrackedPlayerIconPrerenderer;

public final class TrackedPlayerIconManager {
    private static final int ICON_WIDTH = 32;
    private static final int PREFERRED_ATLAS_WIDTH = 1024;
    private final TrackedPlayerIconPrerenderer prerenderer;
    private final XaeroIconAtlasManager iconAtlasManager;
    private final Map<class_2960, XaeroIcon> icons;
    private final int iconWidth;

    private TrackedPlayerIconManager(TrackedPlayerIconPrerenderer prerenderer, XaeroIconAtlasManager iconAtlasManager, Map<class_2960, XaeroIcon> icons, int iconWidth) {
        this.prerenderer = prerenderer;
        this.iconAtlasManager = iconAtlasManager;
        this.icons = icons;
        this.iconWidth = iconWidth;
    }

    public class_2960 getPlayerSkin(class_1657 player, class_640 info) {
        class_2960 skinTextureLocation;
        class_2960 class_29602 = skinTextureLocation = player instanceof class_742 ? ((class_742)player).method_52814().comp_1626() : info.method_52810().comp_1626();
        if (skinTextureLocation == null) {
            skinTextureLocation = class_1068.method_4648((UUID)player.method_5667()).comp_1626();
        }
        return skinTextureLocation;
    }

    public XaeroIcon getIcon(MapElementGraphics guiGraphics, class_1657 player, class_640 info, PlayerTrackerMapElement<?> element) {
        class_2960 skinTextureLocation = this.getPlayerSkin(player, info);
        XaeroIcon result = this.icons.get(skinTextureLocation);
        if (result == null) {
            result = this.iconAtlasManager.getCurrentAtlas().createIcon();
            this.icons.put(skinTextureLocation, result);
            this.prerenderer.prerender(guiGraphics, result, player, this.iconWidth, skinTextureLocation, element);
        }
        return result;
    }

    public static final class Builder {
        public TrackedPlayerIconManager build() {
            int maxTextureSize = RenderSystem.getDevice().getMaxTextureSize();
            int atlasTextureSize = Math.min(maxTextureSize, 1024) / 32 * 32;
            return new TrackedPlayerIconManager(new TrackedPlayerIconPrerenderer(), new XaeroIconAtlasManager(32, atlasTextureSize, new ArrayList<XaeroIconAtlas>()), new HashMap<class_2960, XaeroIcon>(), 32);
        }

        public static Builder begin() {
            return new Builder();
        }
    }
}

