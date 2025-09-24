/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.class_156
 *  net.minecraft.class_2960
 *  net.minecraft.class_5148
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.class_156;
import net.minecraft.class_2960;
import net.minecraft.class_5148;

public class HorseVariant {
    public static final Map<class_5148, class_2960> HORSE_MARKINGS = (Map)class_156.method_654((Object)Maps.newEnumMap(class_5148.class), map -> {
        map.put(class_5148.field_23808, null);
        map.put(class_5148.field_23809, class_2960.method_60654((String)"textures/entity/horse/horse_markings_white.png"));
        map.put(class_5148.field_23810, class_2960.method_60654((String)"textures/entity/horse/horse_markings_whitefield.png"));
        map.put(class_5148.field_23811, class_2960.method_60654((String)"textures/entity/horse/horse_markings_whitedots.png"));
        map.put(class_5148.field_23812, class_2960.method_60654((String)"textures/entity/horse/horse_markings_blackdots.png"));
    });
    private final class_2960 texture;
    private final class_5148 markings;

    public HorseVariant(class_2960 texture, class_5148 markings) {
        this.texture = texture;
        this.markings = markings;
    }

    public String toString() {
        return String.valueOf(this.texture) + "%" + String.valueOf(HORSE_MARKINGS.get(this.markings));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HorseVariant that = (HorseVariant)o;
        return Objects.equals(this.texture, that.texture) && this.markings == that.markings;
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.markings);
    }
}

