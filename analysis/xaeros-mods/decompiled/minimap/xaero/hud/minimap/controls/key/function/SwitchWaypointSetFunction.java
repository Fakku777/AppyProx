/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 */
package xaero.hud.minimap.controls.key.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.class_1074;
import xaero.common.misc.KeySortableByOther;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;

public class SwitchWaypointSetFunction
extends KeyMappingFunction {
    protected SwitchWaypointSetFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorld currentWorld = session.getWorldManager().getCurrentWorld();
        if (currentWorld == null) {
            return;
        }
        ArrayList<KeySortableByOther<Object>> keysList = new ArrayList<KeySortableByOther<Object>>();
        for (WaypointSet set : currentWorld.getIterableWaypointSets()) {
            String key = set.getName();
            keysList.add(new KeySortableByOther<Object>(key, new Comparable[]{class_1074.method_4662((String)key, (Object[])new Object[0]).toLowerCase()}));
        }
        Collections.sort(keysList);
        boolean foundCurrent = false;
        String firstSetKey = null;
        for (KeySortableByOther keySortableByOther : keysList) {
            String setKey = (String)keySortableByOther.getKey();
            if (firstSetKey == null) {
                firstSetKey = setKey;
            }
            if (setKey != null && setKey.equals(currentWorld.getCurrentWaypointSetId())) {
                foundCurrent = true;
                continue;
            }
            if (!foundCurrent) continue;
            foundCurrent = false;
            currentWorld.setCurrentWaypointSetId(setKey);
            break;
        }
        if (foundCurrent) {
            currentWorld.setCurrentWaypointSetId(firstSetKey);
        }
        session.getWorldStateUpdater().update();
        session.getWaypointSession().setSetChangedTime(System.currentTimeMillis());
        try {
            session.getWorldManagerIO().saveWorld(currentWorld);
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    @Override
    public void onRelease() {
    }
}

