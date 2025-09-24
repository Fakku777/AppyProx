/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10017
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_2561
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_3879
 *  net.minecraft.class_4588
 *  net.minecraft.class_630
 *  net.minecraft.class_897
 *  net.minecraft.class_898
 *  org.lwjgl.opengl.GL11
 */
package xaero.hud.minimap.radar.icon;

import net.minecraft.class_10017;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_3879;
import net.minecraft.class_4588;
import net.minecraft.class_630;
import net.minecraft.class_897;
import net.minecraft.class_898;
import org.lwjgl.opengl.GL11;
import xaero.common.icon.XaeroIcon;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.cache.RadarIconCache;
import xaero.hud.minimap.radar.icon.cache.RadarIconEntityCache;
import xaero.hud.minimap.radar.icon.cache.id.RadarIconKey;
import xaero.hud.minimap.radar.icon.cache.id.armor.RadarIconArmor;
import xaero.hud.minimap.radar.icon.cache.id.armor.RadarIconArmorHandler;
import xaero.hud.minimap.radar.icon.cache.id.variant.RadarIconVariantHandler;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinitionManager;
import xaero.hud.minimap.radar.icon.definition.form.RadarIconBasicForms;
import xaero.hud.minimap.radar.icon.definition.form.RadarIconForm;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;

public class RadarIconManager {
    public static final XaeroIcon FAILED = new XaeroIcon(null, 0, 0);
    public static final XaeroIcon DOT = new XaeroIcon(null, 0, 0);
    private boolean canPrerender;
    private final RadarIconCreator iconCreator;
    private final RadarIconModelConfig defaultModelConfig;
    private final RadarIconDefinitionManager definitionManager;
    private final RadarIconVariantHandler variantHandler;
    private final RadarIconArmorHandler armorHandler;
    private final RadarIconCache iconCache;

    public RadarIconManager(RadarIconCreator iconCreator) {
        this.iconCreator = iconCreator;
        this.definitionManager = new RadarIconDefinitionManager();
        this.variantHandler = new RadarIconVariantHandler();
        this.iconCache = new RadarIconCache();
        this.definitionManager.reloadResources();
        this.defaultModelConfig = new RadarIconModelConfig();
        this.armorHandler = new RadarIconArmorHandler();
    }

    public <T extends class_1297> XaeroIcon get(T entity, float scale, boolean debug, boolean debugEntityVariantIds, MinimapElementGraphics guiGraphics, class_276 defaultFramebuffer) {
        class_1299 entityType = entity.method_5864();
        RadarIconDefinition iconDefinition = this.definitionManager.get(class_1299.method_5890((class_1299)entityType));
        class_898 renderManager = class_310.method_1551().method_1561();
        class_897 entityRenderer = renderManager.method_3953(entity);
        return this.get(entity, entityType, iconDefinition, entityRenderer, scale, debug, debugEntityVariantIds, guiGraphics, defaultFramebuffer);
    }

    private <T extends class_1297, S extends class_10017> XaeroIcon get(T entity, class_1299<?> entityType, RadarIconDefinition iconDefinition, class_897<? super T, S> entityRenderer, float scale, boolean debug, boolean debugEntityVariantIds, MinimapElementGraphics guiGraphics, class_276 defaultFramebuffer) {
        RadarIconForm iconForm;
        String entityVariantString;
        class_10017 entityRenderState = entityRenderer.method_62425(entity, 1.0f);
        Object variant = this.variantHandler.getEntityVariant(iconDefinition, entity, entityRenderer, entityRenderState);
        while (GL11.glGetError() != 0) {
        }
        if (variant == null) {
            return null;
        }
        RadarIconArmor armor = null;
        if (entity instanceof class_1309 && !(entity instanceof class_1657)) {
            armor = this.armorHandler.getArmor((class_1309)entity);
        }
        RadarIconEntityCache entityIconCache = this.iconCache.getEntityCache(entityType);
        RadarIconKey iconKey = new RadarIconKey(variant, armor);
        XaeroIcon cachedValue = entityIconCache.get(iconKey);
        if (entityIconCache.isInvalidVariantClass()) {
            return FAILED;
        }
        if (cachedValue != null) {
            return cachedValue;
        }
        String variantMapKey = entityVariantString = entityIconCache.getVariantString(iconKey);
        if (iconDefinition != null) {
            RadarIconForm radarIconForm = iconForm = variantMapKey == null ? null : iconDefinition.getVariantForm(variantMapKey);
            if (iconForm == null) {
                variantMapKey = "default";
                iconForm = iconDefinition.getVariantForm(variantMapKey);
            }
        } else {
            RadarIconForm radarIconForm = iconForm = entity instanceof class_1309 ? RadarIconBasicForms.DEFAULT_MODEL : RadarIconBasicForms.SELF_ITEM;
        }
        if (debugEntityVariantIds && entityVariantString != null && (this.canPrerender || iconForm == RadarIconBasicForms.DOT)) {
            class_310.method_1551().field_1705.method_1743().method_1812((class_2561)class_2561.method_43470((String)entityVariantString));
        }
        if (iconForm == RadarIconBasicForms.DOT) {
            entityIconCache.add(iconKey, DOT);
            return DOT;
        }
        if (!this.canPrerender) {
            return null;
        }
        RadarIconCreator.Parameters parameters = new RadarIconCreator.Parameters(variant, this.defaultModelConfig, iconForm, scale, debug);
        cachedValue = this.iconCreator.create(guiGraphics, entityRenderer, entityRenderState, entity, defaultFramebuffer, parameters);
        entityIconCache.add(iconKey, cachedValue);
        this.canPrerender = false;
        return cachedValue;
    }

    public void reset() {
        this.iconCreator.reset();
        this.iconCache.clear();
        MinimapLogs.LOGGER.info("Radar icon manager reset!");
    }

    public void resetResources() {
        this.definitionManager.reloadResources();
    }

    public void allowPrerender() {
        this.canPrerender = true;
    }

    public void onModelRenderTrace(class_3879 model, class_4588 vertexConsumer, int color) {
        this.iconCreator.getRenderTracer().onModelRender(model, vertexConsumer, color);
    }

    public void onModelPartRenderTrace(class_630 modelRenderer, int color) {
        this.iconCreator.getRenderTracer().onModelPartRender(modelRenderer, color);
    }
}

