/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package xaero.map.element.render;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class ElementRenderLocation {
    private static final Int2ObjectMap<ElementRenderLocation> ALL = new Int2ObjectOpenHashMap();
    public static final ElementRenderLocation UNKNOWN = new ElementRenderLocation(-1);
    public static final ElementRenderLocation IN_MINIMAP = new ElementRenderLocation(0);
    public static final ElementRenderLocation OVER_MINIMAP = new ElementRenderLocation(1);
    public static final ElementRenderLocation IN_WORLD = new ElementRenderLocation(2);
    public static final ElementRenderLocation WORLD_MAP = new ElementRenderLocation(3);
    public static final ElementRenderLocation WORLD_MAP_MENU = new ElementRenderLocation(4);
    private final int index;

    public ElementRenderLocation(int index) {
        this.index = index;
        ALL.put(index, (Object)this);
    }

    public int getIndex() {
        return this.index;
    }

    public static ElementRenderLocation fromIndex(int location) {
        ElementRenderLocation result = (ElementRenderLocation)ALL.get(location);
        if (result == null) {
            return UNKNOWN;
        }
        return result;
    }
}

