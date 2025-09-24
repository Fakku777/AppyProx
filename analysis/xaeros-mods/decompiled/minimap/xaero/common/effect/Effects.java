/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1291
 *  net.minecraft.class_4081
 *  net.minecraft.class_6880
 */
package xaero.common.effect;

import net.minecraft.class_1291;
import net.minecraft.class_4081;
import net.minecraft.class_6880;
import xaero.common.effect.NoCaveMapsEffect;
import xaero.common.effect.NoMinimapEffect;
import xaero.common.effect.NoRadarEffect;
import xaero.common.effect.NoWaypointsEffect;

public class Effects {
    public static class_1291 NO_MINIMAP_UNHELD = null;
    public static class_1291 NO_MINIMAP_HARMFUL_UNHELD = null;
    public static class_1291 NO_RADAR_UNHELD = null;
    public static class_1291 NO_RADAR_HARMFUL_UNHELD = null;
    public static class_1291 NO_WAYPOINTS_UNHELD = null;
    public static class_1291 NO_WAYPOINTS_HARMFUL_UNHELD = null;
    public static class_1291 NO_CAVE_MAPS_UNHELD = null;
    public static class_1291 NO_CAVE_MAPS_HARMFUL_UNHELD = null;
    public static class_6880<class_1291> NO_MINIMAP = null;
    public static class_6880<class_1291> NO_MINIMAP_HARMFUL = null;
    public static class_6880<class_1291> NO_RADAR = null;
    public static class_6880<class_1291> NO_RADAR_HARMFUL = null;
    public static class_6880<class_1291> NO_WAYPOINTS = null;
    public static class_6880<class_1291> NO_WAYPOINTS_HARMFUL = null;
    public static class_6880<class_1291> NO_CAVE_MAPS = null;
    public static class_6880<class_1291> NO_CAVE_MAPS_HARMFUL = null;

    public static void init() {
        if (NO_MINIMAP != null) {
            return;
        }
        NO_MINIMAP_UNHELD = new NoMinimapEffect(class_4081.field_18273);
        NO_MINIMAP_HARMFUL_UNHELD = new NoMinimapEffect(class_4081.field_18272);
        NO_RADAR_UNHELD = new NoRadarEffect(class_4081.field_18273);
        NO_RADAR_HARMFUL_UNHELD = new NoRadarEffect(class_4081.field_18272);
        NO_WAYPOINTS_UNHELD = new NoWaypointsEffect(class_4081.field_18273);
        NO_WAYPOINTS_HARMFUL_UNHELD = new NoWaypointsEffect(class_4081.field_18272);
        NO_CAVE_MAPS_UNHELD = new NoCaveMapsEffect(class_4081.field_18273);
        NO_CAVE_MAPS_HARMFUL_UNHELD = new NoCaveMapsEffect(class_4081.field_18272);
    }
}

