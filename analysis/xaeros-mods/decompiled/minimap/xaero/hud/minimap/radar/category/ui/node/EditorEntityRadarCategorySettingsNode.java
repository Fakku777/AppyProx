/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 */
package xaero.hud.minimap.radar.category.ui.node;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import xaero.common.graphics.CursorBox;
import xaero.common.misc.ListFactory;
import xaero.hud.category.rule.ObjectCategoryRule;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.EditorFilterSettingsNode;
import xaero.hud.category.ui.node.options.EditorExpandingOptionsNode;
import xaero.hud.category.ui.node.options.EditorOptionsNode;
import xaero.hud.category.ui.node.options.EditorSimpleButtonNode;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNode;
import xaero.hud.category.ui.node.options.text.EditorTextFieldOptionsNode;
import xaero.hud.category.ui.node.rule.EditorExcludeListNode;
import xaero.hud.category.ui.node.rule.EditorIncludeListNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;
import xaero.hud.minimap.radar.category.EntityRadarCategoryConstants;
import xaero.hud.minimap.radar.category.rule.EntityRadarCategoryHardRules;
import xaero.hud.minimap.radar.category.rule.EntityRadarListRuleTypes;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;

public final class EditorEntityRadarCategorySettingsNode<SETTING_DATA extends EditorOptionsNode<?>>
extends EditorFilterSettingsNode<class_1297, class_1657, SETTING_DATA> {
    private EditorEntityRadarCategorySettingsNode(Map<ObjectCategorySetting<?>, SETTING_DATA> settings, List<SETTING_DATA> settingList, @Nonnull EditorSimpleButtonNode deleteButton, @Nonnull EditorSimpleButtonNode protectionButton, @Nonnull EditorTextFieldOptionsNode nameOption, ListFactory listFactory, boolean rootSettings, EditorExpandingOptionsNode<ObjectCategoryRule<class_1297, class_1657>> baseRule, EditorIncludeListNode includeList, EditorExcludeListNode excludeList, boolean movable, @Nonnull EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, boolean protection) {
        super(settings, settingList, deleteButton, protectionButton, nameOption, listFactory, rootSettings, baseRule, includeList, excludeList, movable, listEntryFactory, tooltipSupplier, protection);
    }

    public static final class Builder
    extends EditorFilterSettingsNode.Builder<class_1297, class_1657, EditorEntityRadarCategorySettingsNode<?>, Builder> {
        private Builder() {
            super(EntityRadarCategoryConstants.MAP_FACTORY, EntityRadarCategoryConstants.LIST_FACTORY, EntityRadarCategorySettings.SETTINGS_LIST, EntityRadarCategoryHardRules.HARD_RULES_LIST);
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.getIncludeListBuilder().setTooltipSupplier((parent, bd) -> new CursorBox("gui.xaero_box_category_include_list2"));
            this.getExcludeListBuilder().setTooltipSupplier((parent, bd) -> new CursorBox("gui.xaero_box_category_exclude_list2"));
            this.baseRuleBuilder.setTooltipSupplier((parent, bd) -> new CursorBox("gui.xaero_box_category_hard_include2"));
            this.baseRuleBuilder.setCurrentValue(EntityRadarCategoryHardRules.IS_NOTHING);
            this.getIncludeListBuilder().getIncludeInSuperToggleDataBuilder().setTooltipSupplier((parent, data) -> new CursorBox("gui.xaero_box_category_include_list_include_in_super2"));
            CursorBox listHelp = new CursorBox("gui.xaero_box_category_list_add");
            IEditorDataTooltipSupplier helpTooltipSupplier = (parent, data) -> data.isExpanded() ? listHelp : null;
            this.getIncludeListBuilder().setHelpTooltipSupplier(helpTooltipSupplier);
            this.getExcludeListBuilder().setHelpTooltipSupplier(helpTooltipSupplier);
            EditorTextFieldOptionsNode.Builder includeListAdderBuilder = this.getIncludeListBuilder().getAdderBuilder();
            EditorTextFieldOptionsNode.Builder excludeListAdderBuilder = this.getExcludeListBuilder().getAdderBuilder();
            includeListAdderBuilder.setAllowCustomInput(true);
            excludeListAdderBuilder.setAllowCustomInput(true);
            this.getIncludeListBuilder().setDefaultListRuleType(EntityRadarListRuleTypes.ENTITY_TYPE);
            this.getIncludeListBuilder().setListRuleTypes(EntityRadarListRuleTypes.TYPE_LIST);
            this.getExcludeListBuilder().setDefaultListRuleType(EntityRadarListRuleTypes.ENTITY_TYPE);
            this.getExcludeListBuilder().setListRuleTypes(EntityRadarListRuleTypes.TYPE_LIST);
            return this;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }

        @Override
        protected EditorEntityRadarCategorySettingsNode<?> buildInternally(List<IEditorSettingNode<?>> builtSettingData, Map<ObjectCategorySetting<?>, IEditorSettingNode<?>> builtSettingsDataMap) {
            EditorEntityRadarCategorySettingsNode result = new EditorEntityRadarCategorySettingsNode(builtSettingsDataMap, builtSettingData, this.deleteButtonBuilder.build(), this.protectionButtonBuilder.build(), this.nameOptionBuilder.build(), this.listFactory, this.rootSettings, (EditorExpandingOptionsNode<ObjectCategoryRule<class_1297, class_1657>>)this.baseRuleBuilder.build(), this.buildIncludeList(), this.buildExcludeList(), this.movable, this.listEntryFactory, this.tooltipSupplier, this.protection);
            return result;
        }
    }
}

