/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 */
package xaero.hud.minimap.radar.state;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1297;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;

public final class RadarList
implements Comparable<RadarList> {
    private EntityRadarCategory category;
    private final List<class_1297> entities;

    private RadarList(List<class_1297> entities) {
        this.entities = entities;
        this.category = null;
    }

    public EntityRadarCategory getCategory() {
        return this.category;
    }

    public RadarList setCategory(EntityRadarCategory category) {
        this.category = category;
        return this;
    }

    public void clearEntities() {
        this.entities.clear();
    }

    public boolean add(class_1297 entity) {
        return this.entities.add(entity);
    }

    public class_1297 get(int index) {
        return this.entities.get(index);
    }

    public int size() {
        return this.entities.size();
    }

    public Iterable<class_1297> getEntities() {
        return this.entities;
    }

    @Override
    public int compareTo(RadarList o) {
        return this.category.getSettingValue(EntityRadarCategorySettings.RENDER_ORDER).compareTo(o.category.getSettingValue(EntityRadarCategorySettings.RENDER_ORDER));
    }

    public static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public RadarList build() {
            return new RadarList(new ArrayList<class_1297>());
        }

        public static Builder getDefault() {
            return new Builder().setDefault();
        }
    }
}

