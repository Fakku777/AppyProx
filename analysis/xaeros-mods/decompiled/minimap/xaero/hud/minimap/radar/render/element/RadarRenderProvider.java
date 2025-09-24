/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.hud.minimap.radar.render.element;

import java.util.Iterator;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.render.element.RadarRenderContext;
import xaero.hud.minimap.radar.state.RadarList;

public final class RadarRenderProvider
extends MinimapElementRenderProvider<class_1297, RadarRenderContext> {
    private boolean used;
    private class_1297 renderEntity;
    private Iterator<RadarList> entityLists;
    private RadarList currentList;
    private RadarList listForContext;
    private int currentListIndex;

    @Override
    public void begin(MinimapElementRenderLocation location, RadarRenderContext context) {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        this.used = true;
        this.renderEntity = class_310.method_1551().method_1560();
        context.reversedOrder = MinimapKeyMappings.REVERSE_ENTITY_RADAR.method_1434();
        class_437 screenBU = class_310.method_1551().field_1755;
        class_310.method_1551().field_1755 = null;
        context.playerListDown = class_310.method_1551().field_1690.field_1907.method_1434() || MinimapKeyMappings.ALTERNATIVE_LIST_PLAYERS.method_1434();
        class_310.method_1551().field_1755 = screenBU;
        this.entityLists = minimapSession.getRadarSession().getState().getRadarLists().iterator();
        this.currentList = null;
        this.listForContext = null;
        this.currentListIndex = 0;
    }

    private void ensureList(MinimapElementRenderLocation location, RadarRenderContext context) {
        block0: while (this.currentList == null || this.currentListIndex >= this.currentList.size() || this.currentListIndex < 0) {
            do {
                if (!this.entityLists.hasNext()) {
                    this.currentList = null;
                    this.currentListIndex = 0;
                    break block0;
                }
                this.currentList = this.entityLists.next();
                int n = this.currentListIndex = context.reversedOrder ? this.currentList.size() - 1 : 0;
            } while ((location == MinimapElementRenderLocation.IN_MINIMAP || location == MinimapElementRenderLocation.OVER_MINIMAP) && location == MinimapElementRenderLocation.OVER_MINIMAP != this.shouldRenderOverMinimap(this.currentList.getCategory(), context));
        }
    }

    private boolean shouldRenderOverMinimap(EntityRadarCategory category, RadarRenderContext context) {
        int settingValue = category.getSettingValue(EntityRadarCategorySettings.RENDER_OVER_MINIMAP).intValue();
        return settingValue == 2 || settingValue == 1 && context.playerListDown;
    }

    @Override
    public boolean hasNext(MinimapElementRenderLocation location, RadarRenderContext context) {
        this.ensureList(location, context);
        if (this.currentList == null) {
            return false;
        }
        return !context.reversedOrder && this.currentListIndex < this.currentList.size() || context.reversedOrder && this.currentListIndex >= 0;
    }

    @Override
    public class_1297 setupContextAndGetNext(MinimapElementRenderLocation location, RadarRenderContext context) {
        class_1297 result;
        this.ensureList(location, context);
        if (this.listForContext != this.currentList) {
            context.entityCategory = this.currentList.getCategory();
            this.setupContextForCategory(context.entityCategory, context);
            this.listForContext = this.currentList;
        }
        if ((result = this.getNext(location, context)) == null) {
            return null;
        }
        this.setupContextForEntity(result, context);
        return result;
    }

    @Override
    public class_1297 getNext(MinimapElementRenderLocation location, RadarRenderContext context) {
        class_1297 result = this.currentList.get(this.currentListIndex);
        this.currentListIndex += context.reversedOrder ? -1 : 1;
        if (this.renderEntity == result) {
            return null;
        }
        return result;
    }

    @Override
    public void end(MinimapElementRenderLocation location, RadarRenderContext context) {
        this.used = false;
        this.renderEntity = null;
        context.entityCategory = null;
    }

    public void setupContextForCategory(EntityRadarCategory entityCategory, RadarRenderContext context) {
        context.iconScale = entityCategory.getSettingValue(EntityRadarCategorySettings.ICON_SCALE);
        context.dotSize = entityCategory.getSettingValue(EntityRadarCategorySettings.DOT_SIZE).intValue();
        context.dotScale = 1.0 + 0.5 * (double)(context.dotSize - 1);
        int icons = entityCategory.getSettingValue(EntityRadarCategorySettings.ICONS).intValue();
        context.iconsForCategory = icons == 1 && context.playerListDown || icons == 2;
    }

    public void setupContextForEntity(class_1297 entity, RadarRenderContext context) {
        context.icon = context.iconsForCategory;
    }

    public boolean isUsed() {
        return this.used;
    }
}

