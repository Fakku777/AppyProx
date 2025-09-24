/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_2960
 *  net.minecraft.class_5321
 *  net.minecraft.class_7924
 */
package xaero.map.world;

import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

public class MapConnectionNode {
    private final class_5321<class_1937> dimId;
    private final String mw;
    private String cachedString;

    public MapConnectionNode(class_5321<class_1937> dimId, String mw) {
        this.dimId = dimId;
        this.mw = mw;
    }

    public String toString() {
        if (this.cachedString == null) {
            this.cachedString = this.dimId.method_29177().toString().replace(':', '$') + "/" + this.mw;
        }
        return this.cachedString;
    }

    public String getNamedString(MapWorld mapWorld) {
        MapDimension dim = mapWorld.getDimension(this.dimId);
        return String.valueOf(this.dimId.method_29177()) + "/" + dim.getMultiworldName(this.mw);
    }

    public static MapConnectionNode fromString(String s) {
        class_2960 dimLocation;
        int dividerIndex = s.lastIndexOf(47);
        if (dividerIndex == -1) {
            return null;
        }
        String dimString = s.substring(0, dividerIndex);
        try {
            dimLocation = dimString.equals("0") ? class_1937.field_25179.method_29177() : (dimString.equals("-1") ? class_1937.field_25180.method_29177() : (dimString.equals("1") ? class_1937.field_25181.method_29177() : class_2960.method_60654((String)dimString.replace('$', ':'))));
        }
        catch (Throwable t) {
            return null;
        }
        String mwString = s.substring(dividerIndex + 1);
        return new MapConnectionNode((class_5321<class_1937>)class_5321.method_29179((class_5321)class_7924.field_41223, (class_2960)dimLocation), mwString);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || !(another instanceof MapConnectionNode)) {
            return false;
        }
        MapConnectionNode anotherNode = (MapConnectionNode)another;
        return this.dimId.equals(anotherNode.dimId) && this.mw.equals(anotherNode.mw);
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public class_5321<class_1937> getDimId() {
        return this.dimId;
    }

    public String getMw() {
        return this.mw;
    }
}

