/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 *  net.minecraft.class_9273$class_4621
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.class_2960;
import net.minecraft.class_9273;

public class IronGolemVariant {
    private final class_2960 texture;
    private final class_9273.class_4621 cracks;

    public IronGolemVariant(class_2960 texture, class_9273.class_4621 cracks) {
        this.texture = texture;
        this.cracks = cracks;
    }

    public String toString() {
        return String.valueOf(this.texture) + "%" + String.valueOf(this.cracks);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IronGolemVariant that = (IronGolemVariant)o;
        return Objects.equals(this.texture, that.texture) && this.cracks == that.cracks;
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.cracks);
    }
}

