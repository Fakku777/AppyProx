/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10017
 *  net.minecraft.class_10042
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_2960
 *  net.minecraft.class_897
 *  net.minecraft.class_922
 */
package xaero.hud.minimap.radar.icon.cache.id.variant;

import java.lang.reflect.Method;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_2960;
import net.minecraft.class_897;
import net.minecraft.class_922;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.definition.BuiltInRadarIconDefinitions;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;

public class RadarIconVariantHandler {
    private final StringBuilder legacyEntityStringBuilder = new StringBuilder();

    public <T extends class_1297> Object getEntityVariant(RadarIconDefinition iconDefinition, T entity, class_897<? super T, ?> entityRenderer, class_10017 entityRenderState) {
        Object variant = null;
        class_2960 entityTexture = null;
        try {
            class_2960 entityTextureUnchecked;
            class_2960 class_29602;
            if (entityRenderer instanceof class_922) {
                class_922 livingEntityRenderer = (class_922)entityRenderer;
                class_29602 = livingEntityRenderer.method_3885((class_10042)entityRenderState);
            } else {
                class_29602 = null;
            }
            entityTexture = entityTextureUnchecked = class_29602;
        }
        catch (Throwable e) {
            MinimapLogs.LOGGER.error("Exception while fetching entity texture to build its variant ID for " + String.valueOf(class_1299.method_5890((class_1299)entity.method_5864())));
            MinimapLogs.LOGGER.error("The exception is most likely on another mod's end and suppressing it here could lead to more issues. Please report to appropriate mod devs.", e);
        }
        if (iconDefinition != null) {
            Method variantMethod = iconDefinition.getVariantMethod();
            if (variantMethod != null) {
                try {
                    variant = variantMethod.invoke(null, entityTexture, entityRenderer, entity);
                }
                catch (Throwable e) {
                    class_2960 entityId = class_1299.method_5890((class_1299)entity.method_5864());
                    MinimapLogs.LOGGER.error("Exception while using the variant ID method " + iconDefinition.getVariantMethodString() + " defined for " + String.valueOf(entityId));
                    MinimapLogs.LOGGER.error("If the exception is on another mod's end, suppressing it here could lead to more issues. Please report to appropriate mod devs.", e);
                    iconDefinition.setVariantMethod(null);
                }
            } else {
                variant = this.getLegacyVariantId(iconDefinition, entity, entityRenderer);
            }
        }
        if (variant == null) {
            variant = BuiltInRadarIconDefinitions.getVariant(entityTexture, entityRenderer, entity);
        }
        return variant;
    }

    private <T extends class_1297> String getLegacyVariantId(RadarIconDefinition iconDefinition, T entity, class_897<? super T, ?> entityRenderer) {
        Method variantIdBuilderMethod = iconDefinition.getVariantIdBuilderMethod();
        if (variantIdBuilderMethod != null && !variantIdBuilderMethod.equals(BuiltInRadarIconDefinitions.BUILD_VARIANT_ID_STRING_METHOD)) {
            this.legacyEntityStringBuilder.setLength(0);
            try {
                variantIdBuilderMethod.invoke(null, this.legacyEntityStringBuilder, entityRenderer, entity);
                return this.legacyEntityStringBuilder.toString();
            }
            catch (Throwable e) {
                class_2960 entityId = class_1299.method_5890((class_1299)entity.method_5864());
                MinimapLogs.LOGGER.error("Exception while using the variant builder ID method " + iconDefinition.getVariantIdBuilderMethodString() + " defined for " + String.valueOf(entityId));
                MinimapLogs.LOGGER.error("If the exception is on another mod's end, suppressing it here could lead to more issues. Please report to appropriate mod devs.", e);
                iconDefinition.setVariantIdBuilderMethod(null);
                return null;
            }
        }
        Method variantOldIdMethod = iconDefinition.getOldVariantIdMethod();
        if (variantOldIdMethod == null || variantOldIdMethod.equals(BuiltInRadarIconDefinitions.GET_VARIANT_ID_STRING_METHOD)) {
            return null;
        }
        try {
            return (String)variantOldIdMethod.invoke(null, entityRenderer, entity);
        }
        catch (Throwable e) {
            class_2960 entityId = class_1299.method_5890((class_1299)entity.method_5864());
            MinimapLogs.LOGGER.error("Exception while using the variant ID method " + iconDefinition.getOldVariantIdMethodString() + " defined for " + String.valueOf(entityId));
            MinimapLogs.LOGGER.error("If the exception is on another mod's end, suppressing it here could lead to more issues. Please report to appropriate mod devs.", e);
            iconDefinition.setOldVariantIdMethod(null);
            return null;
        }
    }
}

