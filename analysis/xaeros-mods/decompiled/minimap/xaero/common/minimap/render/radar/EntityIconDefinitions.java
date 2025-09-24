/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_2960
 *  net.minecraft.class_897
 */
package xaero.common.minimap.render.radar;

import net.minecraft.class_1297;
import net.minecraft.class_2960;
import net.minecraft.class_897;
import xaero.hud.minimap.radar.icon.definition.BuiltInRadarIconDefinitions;

@Deprecated
public class EntityIconDefinitions {
    public static <E extends class_1297> Object getVariant(class_2960 entityTexture, class_897<? super E, ?> entityRenderer, E entity) {
        return BuiltInRadarIconDefinitions.getVariant(entityTexture, entityRenderer, entity);
    }

    public static void buildVariantIdString(StringBuilder stringBuilder, class_897 entityRenderer, class_1297 entity) {
    }

    public static String getVariantString(class_897 entityRenderer, class_1297 entity) {
        return null;
    }
}

