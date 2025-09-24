/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.class_2960;

public class TamableVariant {
    private final class_2960 texture;
    private final boolean tame;

    public TamableVariant(class_2960 texture, boolean tame) {
        this.texture = texture;
        this.tame = tame;
    }

    public String toString() {
        return String.valueOf(this.texture) + "%" + this.tame;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TamableVariant that = (TamableVariant)o;
        return this.tame == that.tame && Objects.equals(this.texture, that.texture);
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.tame);
    }
}

