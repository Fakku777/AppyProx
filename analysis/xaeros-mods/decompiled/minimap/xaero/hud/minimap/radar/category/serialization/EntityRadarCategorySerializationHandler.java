/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 */
package xaero.hud.minimap.radar.category.serialization;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import xaero.hud.category.rule.ObjectCategoryHardRule;
import xaero.hud.category.rule.ObjectCategoryListRuleType;
import xaero.hud.category.serialization.FilterObjectCategorySerializationHandler;
import xaero.hud.category.serialization.data.ObjectCategoryDataSerializer;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.EntityRadarCategoryConstants;
import xaero.hud.minimap.radar.category.rule.EntityRadarCategoryHardRules;
import xaero.hud.minimap.radar.category.rule.EntityRadarListRuleTypes;
import xaero.hud.minimap.radar.category.serialization.data.EntityRadarCategoryData;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;

public final class EntityRadarCategorySerializationHandler
extends FilterObjectCategorySerializationHandler<class_1297, class_1657, EntityRadarCategoryData, EntityRadarCategory, EntityRadarCategory.Builder, EntityRadarCategoryData.Builder> {
    private EntityRadarCategorySerializationHandler(ObjectCategoryDataSerializer<EntityRadarCategoryData> serializer, Supplier<EntityRadarCategoryData.Builder> dataBuilderFactory, Supplier<EntityRadarCategory.Builder> objectCategoryBuilderFactory, Function<String, ObjectCategorySetting<?>> settingTypeGetter, Function<String, ObjectCategoryHardRule<class_1297, class_1657>> hardRuleGetter, ObjectCategoryListRuleType<class_1297, class_1657, ?> defaultListRuleType, Function<String, ObjectCategoryListRuleType<class_1297, class_1657, ?>> listRuleTypeGetter, String listRuleTypePrefixSeparator) {
        super(serializer, dataBuilderFactory, objectCategoryBuilderFactory, settingTypeGetter, hardRuleGetter, defaultListRuleType, listRuleTypeGetter, listRuleTypePrefixSeparator);
    }

    public static final class Builder
    extends FilterObjectCategorySerializationHandler.Builder<class_1297, class_1657, EntityRadarCategoryData, EntityRadarCategory, EntityRadarCategory.Builder, EntityRadarCategoryData.Builder, EntityRadarCategorySerializationHandler, Builder> {
        private Builder(ObjectCategoryDataSerializer.Builder<EntityRadarCategoryData> serializerBuilder) {
            super(serializerBuilder);
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setHardRuleGetter(EntityRadarCategoryHardRules.HARD_RULES::get);
            this.setDataBuilderFactory(EntityRadarCategoryConstants.DATA_BUILDER_FACTORY);
            this.setObjectCategoryBuilderFactory(EntityRadarCategoryConstants.CATEGORY_BUILDER_FACTORY);
            this.setSettingTypeGetter(EntityRadarCategorySettings.SETTINGS::get);
            this.setDefaultListRuleType(EntityRadarListRuleTypes.ENTITY_TYPE);
            this.setListRuleTypeGetter(EntityRadarListRuleTypes.TYPE_MAP::get);
            return this;
        }

        @Override
        protected EntityRadarCategorySerializationHandler buildInternally() {
            return new EntityRadarCategorySerializationHandler(this.serializerBuilder.build(), this.dataBuilderFactory, this.objectCategoryBuilderFactory, (Function<String, ObjectCategorySetting<?>>)this.settingTypeGetter, this.hardRuleGetter, this.defaultListRuleType, this.listRuleTypeGetter, this.listRuleTypePrefixSeparator);
        }

        public static Builder begin(ObjectCategoryDataSerializer.Builder<EntityRadarCategoryData> serializerBuilder) {
            return new Builder(serializerBuilder).setDefault();
        }
    }
}

