/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10583
 *  net.minecraft.class_1267
 *  net.minecraft.class_1297
 *  net.minecraft.class_1496
 *  net.minecraft.class_1569
 *  net.minecraft.class_1588
 *  net.minecraft.class_1657
 *  net.minecraft.class_2561
 *  net.minecraft.class_2940
 *  net.minecraft.class_310
 *  net.minecraft.class_3419
 *  net.minecraft.class_4019
 *  net.minecraft.class_4836
 *  net.minecraft.class_6025
 *  net.minecraft.class_8828
 */
package xaero.hud.minimap.radar.util;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_10583;
import net.minecraft.class_1267;
import net.minecraft.class_1297;
import net.minecraft.class_1496;
import net.minecraft.class_1569;
import net.minecraft.class_1588;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_2940;
import net.minecraft.class_310;
import net.minecraft.class_3419;
import net.minecraft.class_4019;
import net.minecraft.class_4836;
import net.minecraft.class_6025;
import net.minecraft.class_8828;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.misc.Misc;
import xaero.hud.minimap.MinimapLogs;

public class RadarUtils {
    private static class_2940<Optional<UUID>> FOX_TRUSTED_UUID_SECONDARY;
    private static class_2940<Optional<UUID>> FOX_TRUSTED_UUID_MAIN;

    public static double getMaxDistance(MinimapProcessor minimap, boolean circle) {
        int cullingSize = minimap.getMinimapSize() / 2 + 48;
        if (!circle) {
            cullingSize = (int)((double)cullingSize * Math.sqrt(2.0));
        }
        return (double)(cullingSize * cullingSize) / (minimap.getMinimapZoom() * minimap.getMinimapZoom());
    }

    public static boolean isHostileException(class_1297 e) {
        if (e instanceof class_4836) {
            return ((class_4836)e).method_6109();
        }
        return false;
    }

    public static boolean isTamed(class_1297 e, class_1657 p) {
        if (e instanceof class_1496) {
            class_1496 horse = (class_1496)e;
            return horse.method_6727();
        }
        if (e instanceof class_6025) {
            class_6025 ownable = (class_6025)e;
            class_10583 ownerReference = ownable.method_66287();
            return ownerReference != null && p.method_5667().equals(ownerReference.method_66263());
        }
        if (e instanceof class_4019) {
            class_4019 fox = (class_4019)e;
            if (FOX_TRUSTED_UUID_SECONDARY != null && p.method_5667().equals(((Optional)fox.method_5841().method_12789(FOX_TRUSTED_UUID_SECONDARY)).orElse(null))) {
                return true;
            }
            return FOX_TRUSTED_UUID_MAIN != null && p.method_5667().equals(((Optional)fox.method_5841().method_12789(FOX_TRUSTED_UUID_MAIN)).orElse(null));
        }
        return false;
    }

    public static boolean isHostile(class_1297 e) {
        if (class_310.method_1551().field_1687.method_8407() == class_1267.field_5801) {
            return false;
        }
        if (RadarUtils.isHostileException(e)) {
            return false;
        }
        return e instanceof class_1588 || e instanceof class_1569 || e.method_5634() == class_3419.field_15251;
    }

    public static String getCustomName(class_1297 e, boolean nullable) {
        class_2561 c = e.method_5797();
        if (c != null && c.method_10851() instanceof class_8828) {
            return ((class_8828)c.method_10851()).comp_737();
        }
        return nullable ? null : "{non-plain}";
    }

    static {
        Field foxTrustSecondaryField = null;
        Field foxTrustMainField = null;
        try {
            foxTrustSecondaryField = Misc.getFieldReflection(class_4019.class, "DATA_TRUSTED_ID_0", "field_17951", "Lnet/minecraft/class_2940;", "f_28439_");
        }
        catch (Exception e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        try {
            foxTrustMainField = Misc.getFieldReflection(class_4019.class, "DATA_TRUSTED_ID_1", "field_17952", "Lnet/minecraft/class_2940;", "f_28440_");
        }
        catch (Exception e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        if (foxTrustSecondaryField != null) {
            FOX_TRUSTED_UUID_SECONDARY = (class_2940)Misc.getReflectFieldValue(0, foxTrustSecondaryField);
        }
        if (foxTrustMainField != null) {
            FOX_TRUSTED_UUID_MAIN = (class_2940)Misc.getReflectFieldValue(0, foxTrustMainField);
        }
    }
}

