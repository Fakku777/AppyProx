/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1474$class_1475
 *  net.minecraft.class_1767
 *  net.minecraft.class_2960
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.class_1474;
import net.minecraft.class_1767;
import net.minecraft.class_2960;

public class TropicalFishVariant {
    private final class_2960 texture;
    private final class_1474.class_1475 pattern;
    private final class_1767 baseColor;
    private final class_1767 patternColor;

    public TropicalFishVariant(class_2960 texture, class_1474.class_1475 pattern, class_1767 baseColor, class_1767 patternColor) {
        this.texture = texture;
        this.pattern = pattern;
        this.baseColor = baseColor;
        this.patternColor = patternColor;
    }

    public String toString() {
        return this.texture + "%" + this.pattern + "%" + this.baseColor + "%" + this.patternColor;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TropicalFishVariant that = (TropicalFishVariant)o;
        return Objects.equals(this.texture, that.texture) && this.pattern == that.pattern && this.baseColor == that.baseColor && this.patternColor == that.patternColor;
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.pattern, this.baseColor, this.patternColor);
    }
}

