/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_124
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_5250
 *  net.minecraft.class_5321
 */
package xaero.map.teleport;

import net.minecraft.class_1074;
import net.minecraft.class_124;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_5321;
import xaero.map.WorldMap;
import xaero.map.world.MapConnectionNode;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

public class MapTeleporter {
    public void teleport(class_437 screen, MapWorld mapWorld, int x, int y, int z, class_5321<class_1937> d) {
        String tpCommand;
        class_310.method_1551().method_1507(null);
        if (class_310.method_1551().field_1761.method_2908()) {
            MapConnectionNode destinationMapKey;
            MapDimension destinationDim = mapWorld.getDimension(d != null ? d : class_310.method_1551().field_1687.method_27983());
            MapConnectionNode playerMapKey = mapWorld.getPlayerMapKey();
            if (playerMapKey == null) {
                class_5250 messageComponent = class_2561.method_43470((String)class_1074.method_4662((String)"gui.xaero_wm_teleport_never_confirmed", (Object[])new Object[0]));
                messageComponent.method_10862(messageComponent.method_10866().method_10977(class_124.field_1061));
                class_310.method_1551().field_1705.method_1743().method_1812((class_2561)messageComponent);
                return;
            }
            MapConnectionNode mapConnectionNode = destinationMapKey = destinationDim == null ? null : destinationDim.getSelectedMapKeyUnsynced();
            if (!mapWorld.getMapConnections().isConnected(playerMapKey, destinationMapKey)) {
                class_5250 messageComponent = class_2561.method_43470((String)class_1074.method_4662((String)"gui.xaero_wm_teleport_not_connected", (Object[])new Object[0]));
                messageComponent.method_10862(messageComponent.method_10866().method_10977(class_124.field_1061));
                class_310.method_1551().field_1705.method_1743().method_1812((class_2561)messageComponent);
                return;
            }
        }
        String string = tpCommand = d == null ? mapWorld.getTeleportCommandFormat() : mapWorld.getDimensionTeleportCommandFormat();
        String yString = y == Short.MAX_VALUE ? "~" : (WorldMap.settings.partialYTeleportation ? "" + ((double)y + 0.5) : "" + y);
        tpCommand = tpCommand.replace("{x}", "" + x).replace("{y}", yString).replace("{z}", "" + z);
        if (d != null) {
            tpCommand = tpCommand.replace("{d}", d.method_29177().toString());
        }
        class_310 mc = class_310.method_1551();
        if (tpCommand.startsWith("/")) {
            tpCommand = tpCommand.substring(1);
            mc.field_1724.field_3944.method_45730(tpCommand);
        } else {
            mc.field_1724.field_3944.method_45729(tpCommand);
        }
        mapWorld.setCustomDimensionId(null);
        mapWorld.getMapProcessor().checkForWorldUpdate();
    }
}

