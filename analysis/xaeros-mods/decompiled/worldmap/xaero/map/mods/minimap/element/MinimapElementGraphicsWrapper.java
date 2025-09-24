/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597$class_4598
 *  xaero.hud.minimap.element.render.MinimapElementGraphics
 */
package xaero.map.mods.minimap.element;

import net.minecraft.class_4587;
import net.minecraft.class_4597;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.map.element.MapElementGraphics;

public class MinimapElementGraphicsWrapper
extends MinimapElementGraphics {
    private MapElementGraphics graphics;

    public MinimapElementGraphicsWrapper() {
        super(null, null);
    }

    public MinimapElementGraphicsWrapper setGraphics(MapElementGraphics graphics) {
        this.graphics = graphics;
        return this;
    }

    public class_4587 pose() {
        return this.graphics.pose();
    }

    public class_4597.class_4598 getBufferSource() {
        return this.graphics.getBufferSource();
    }
}

