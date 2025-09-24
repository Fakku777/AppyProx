/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1299
 */
package xaero.hud.minimap.radar.icon.cache;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_1299;
import xaero.hud.minimap.radar.icon.cache.RadarIconEntityCache;

public class RadarIconCache {
    private final Map<class_1299<?>, RadarIconEntityCache> iconCacheMap = new HashMap();

    public RadarIconEntityCache getEntityCache(class_1299<?> entityType) {
        RadarIconEntityCache result = this.iconCacheMap.get(entityType);
        if (result == null) {
            result = new RadarIconEntityCache(entityType);
            this.iconCacheMap.put(entityType, result);
        }
        return result;
    }

    public void clear() {
        this.iconCacheMap.clear();
    }
}

