/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1792
 *  net.minecraft.class_2960
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.util.Objects;
import net.minecraft.class_1792;
import net.minecraft.class_2960;

public class LlamaVariant {
    private final class_2960 texture;
    private final boolean trader;
    private final class_1792 swag;

    public LlamaVariant(class_2960 texture, boolean trader, class_1792 swag) {
        this.texture = texture;
        this.trader = trader;
        this.swag = swag;
    }

    public String toString() {
        return String.valueOf(this.texture) + "%" + this.trader + "%" + String.valueOf(this.swag);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LlamaVariant that = (LlamaVariant)o;
        return this.trader == that.trader && Objects.equals(this.texture, that.texture) && this.swag == that.swag;
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.trader, this.swag);
    }
}

