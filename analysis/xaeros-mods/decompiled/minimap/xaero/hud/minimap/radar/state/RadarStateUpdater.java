/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_268
 *  net.minecraft.class_638
 */
package xaero.hud.minimap.radar.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_268;
import net.minecraft.class_638;
import xaero.common.HudMod;
import xaero.common.effect.Effects;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.common.misc.Misc;
import xaero.hud.category.rule.resolver.ObjectCategoryRuleResolver;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.state.RadarList;
import xaero.hud.minimap.radar.state.RadarState;

public class RadarStateUpdater {
    private final EntityRadarCategoryManager categoryManager;
    private final RadarState state;
    private class_1297 lastRenderEntity;
    private final Map<EntityRadarCategory, RadarList> updateMap;

    public RadarStateUpdater(EntityRadarCategoryManager categoryManager, RadarState state) {
        this.categoryManager = categoryManager;
        this.state = state;
        this.updateMap = new HashMap<EntityRadarCategory, RadarList>();
    }

    public void update(class_638 world, class_1297 renderEntity, class_1657 player) {
        if (renderEntity == null) {
            renderEntity = this.lastRenderEntity;
        }
        List<RadarList> radarLists = this.state.getUpdatableLists();
        EntityRadarCategory rootCategory = this.categoryManager.getRootCategory();
        this.ensureCategories(this.state, rootCategory, radarLists);
        radarLists.forEach(RadarList::clearEntities);
        if (HudMod.INSTANCE.isFairPlay()) {
            return;
        }
        if (!HudMod.INSTANCE.getSettings().getEntityRadar() && !this.isWorldMapRadarEnabled()) {
            return;
        }
        if (world == null) {
            return;
        }
        if (renderEntity == null) {
            return;
        }
        if (player == null) {
            return;
        }
        if (Misc.hasEffect(player, Effects.NO_RADAR)) {
            return;
        }
        if (Misc.hasEffect(player, Effects.NO_RADAR_HARMFUL)) {
            return;
        }
        if (!MinimapClientWorldDataHelper.getWorldData((class_638)world).getSyncedRules().allowRadarOnServer) {
            return;
        }
        ObjectCategoryRuleResolver categoryRuleResolver = this.categoryManager.getRuleResolver();
        Iterable worldEntities = world.method_18112();
        boolean shouldHideInvisible = HudMod.INSTANCE.getSettings().radarHideInvisibleEntities;
        for (class_1297 entity : worldEntities) {
            int heightLimit;
            double offY;
            EntityRadarCategory entityCategory;
            if (entity == null || shouldHideInvisible && this.isInvisibleTo(entity, player) || (entityCategory = categoryRuleResolver.resolve(rootCategory, entity, player)) == null || !entityCategory.getSettingValue(EntityRadarCategorySettings.DISPLAYED).booleanValue() || (offY = renderEntity.method_23318() - entity.method_23318()) * offY > (double)((heightLimit = entityCategory.getSettingValue(EntityRadarCategorySettings.HEIGHT_LIMIT).intValue()) * heightLimit)) continue;
            RadarList radarList = this.updateMap.get(entityCategory);
            int entityNumber = entityCategory.getSettingValue(EntityRadarCategorySettings.ENTITY_NUMBER).intValue();
            if (entityNumber != 0 && radarList.size() >= entityNumber) continue;
            radarList.add(entity);
        }
    }

    private void ensureCategories(RadarState state, EntityRadarCategory rootCategory, List<RadarList> radarLists) {
        boolean reversedOrder = MinimapKeyMappings.REVERSE_ENTITY_RADAR.method_1434();
        if (state.getListsGeneratedForConfig() != rootCategory) {
            this.updateMap.clear();
            radarLists.clear();
            this.traceAddCategories(rootCategory, radarLists);
            Collections.sort(radarLists);
            state.setListsGeneratedForConfig(rootCategory);
            state.setListsReversedOrder(false);
        }
        if (state.getListsReversedOrder() == reversedOrder) {
            return;
        }
        Collections.reverse(radarLists);
        state.setListsReversedOrder(reversedOrder);
    }

    private void traceAddCategories(EntityRadarCategory category, List<RadarList> radarLists) {
        category.getDirectSubCategoryIterator().forEachRemaining(sb -> this.traceAddCategories((EntityRadarCategory)sb, radarLists));
        RadarList radarList = RadarList.Builder.getDefault().build().setCategory(category);
        this.updateMap.put(category, radarList);
        radarLists.add(radarList);
    }

    private boolean isWorldMapRadarEnabled() {
        if (!HudMod.INSTANCE.getSupportMods().worldmap()) {
            return false;
        }
        return HudMod.INSTANCE.getSupportMods().worldmapSupport.worldMapIsRenderingRadar();
    }

    private boolean isInvisibleTo(class_1297 entity, class_1657 player) {
        return entity.method_5756(player) || this.shouldHideForSneaking(entity, player);
    }

    private boolean shouldHideForSneaking(class_1297 e, class_1657 p) {
        if (!e.method_5715()) {
            return false;
        }
        class_268 team = e.method_5781();
        return team == null || team != p.method_5781();
    }

    public void setLastRenderViewEntity(class_1297 lastRenderEntity) {
        this.lastRenderEntity = lastRenderEntity;
    }
}

