/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_3532
 */
package xaero.hud.minimap.radar.color;

import net.minecraft.class_2561;
import net.minecraft.class_3532;

public enum RadarColor {
    BLACK((class_2561)class_2561.method_43471((String)"gui.xaero_black"), '0', -16777216),
    DARK_BLUE((class_2561)class_2561.method_43471((String)"gui.xaero_dark_blue"), '1', -16777046),
    DARK_GREEN((class_2561)class_2561.method_43471((String)"gui.xaero_dark_green"), '2', -16733696),
    DARK_AQUA((class_2561)class_2561.method_43471((String)"gui.xaero_dark_aqua"), '3', -16733526),
    DARK_RED((class_2561)class_2561.method_43471((String)"gui.xaero_dark_red"), '4', -5636096),
    DARK_PURPLE((class_2561)class_2561.method_43471((String)"gui.xaero_dark_purple"), '5', -5635926),
    GOLD((class_2561)class_2561.method_43471((String)"gui.xaero_gold"), '6', -22016),
    GRAY((class_2561)class_2561.method_43471((String)"gui.xaero_gray"), '7', -5592406),
    DARK_GRAY((class_2561)class_2561.method_43471((String)"gui.xaero_dark_gray"), '8', -11184811),
    BLUE((class_2561)class_2561.method_43471((String)"gui.xaero_blue"), '9', -11184641),
    GREEN((class_2561)class_2561.method_43471((String)"gui.xaero_green"), 'a', -11141291),
    AQUA((class_2561)class_2561.method_43471((String)"gui.xaero_aqua"), 'b', -11141121),
    RED((class_2561)class_2561.method_43471((String)"gui.xaero_red"), 'c', -65536),
    PURPLE((class_2561)class_2561.method_43471((String)"gui.xaero_purple"), 'd', -43521),
    YELLOW((class_2561)class_2561.method_43471((String)"gui.xaero_yellow"), 'e', -171),
    WHITE((class_2561)class_2561.method_43471((String)"gui.xaero_white"), 'f', -1);

    private final class_2561 name;
    private final char format;
    private final int hex;

    private RadarColor(class_2561 name, char format, int hex) {
        this.name = name;
        this.format = format;
        this.hex = hex;
    }

    public class_2561 getName() {
        return this.name;
    }

    public char getFormat() {
        return this.format;
    }

    public int getHex() {
        return this.hex;
    }

    public static RadarColor fromIndex(int index) {
        if (index == -1) {
            return null;
        }
        return RadarColor.values()[class_3532.method_15340((int)index, (int)0, (int)(RadarColor.values().length - 1))];
    }

    public static RadarColor getRandom() {
        return RadarColor.fromIndex((int)(Math.random() * (double)RadarColor.values().length));
    }
}

