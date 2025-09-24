/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.class_1074
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_310
 *  net.minecraft.class_339
 *  net.minecraft.class_410
 *  net.minecraft.class_437
 *  net.minecraft.class_5250
 *  net.minecraft.class_5251
 */
package xaero.hud.category.ui.node;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.class_1074;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_339;
import net.minecraft.class_410;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import xaero.common.graphics.CursorBox;
import xaero.common.misc.ListFactory;
import xaero.common.misc.MapFactory;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryWidget;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.entry.widget.EditorButton;
import xaero.hud.category.ui.node.EditorCategoryNode;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.options.EditorOptionsNode;
import xaero.hud.category.ui.node.options.EditorSimpleButtonNode;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNode;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNodeBuilder;
import xaero.hud.category.ui.node.options.text.EditorTextFieldOptionsNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public class EditorSettingsNode<SETTING_DATA extends EditorOptionsNode<?>>
extends EditorNode {
    private final Map<ObjectCategorySetting<?>, SETTING_DATA> settings;
    private final List<SETTING_DATA> settingList;
    private boolean toBeDeleted;
    private final EditorSimpleButtonNode deleteButton;
    private final EditorSimpleButtonNode protectionButton;
    private final EditorTextFieldOptionsNode nameOption;
    private final ListFactory listFactory;
    private final boolean rootSettings;
    private boolean protection;

    protected EditorSettingsNode(@Nonnull Map<ObjectCategorySetting<?>, SETTING_DATA> settings, @Nonnull List<SETTING_DATA> settingList, @Nonnull EditorSimpleButtonNode deleteButton, @Nonnull EditorSimpleButtonNode protectionButton, @Nonnull EditorTextFieldOptionsNode nameOption, @Nonnull ListFactory listFactory, boolean rootSettings, boolean movable, @Nonnull EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, boolean protection) {
        super(movable, listEntryFactory, tooltipSupplier);
        this.settings = settings;
        this.settingList = settingList;
        this.listFactory = listFactory;
        this.rootSettings = rootSettings;
        this.deleteButton = deleteButton;
        this.protectionButton = protectionButton;
        this.nameOption = nameOption;
        this.protection = protection;
    }

    public Map<ObjectCategorySetting<?>, SETTING_DATA> getSettings() {
        return this.settings;
    }

    public IEditorSettingNode<?> getSettingData(ObjectCategorySetting<?> setting) {
        return (IEditorSettingNode)this.settings.get(setting);
    }

    public boolean isRootSettings() {
        return this.rootSettings;
    }

    public boolean isToBeDeleted() {
        return this.toBeDeleted;
    }

    public void setToBeDeleted() {
        this.toBeDeleted = true;
    }

    public boolean getProtection() {
        return this.protection;
    }

    public void setProtected(boolean protection) {
        this.protection = protection;
    }

    @Override
    public List<EditorNode> getSubNodes() {
        List<EditorNode> result = this.listFactory.get();
        result.addAll(this.settingList);
        if (!this.protection) {
            result.add(this.nameOption);
        }
        result.add(this.deleteButton);
        result.add(this.protectionButton);
        return result;
    }

    @Override
    public String getDisplayName() {
        return class_1074.method_4662((String)"gui.xaero_category_settings", (Object[])new Object[0]);
    }

    public EditorTextFieldOptionsNode getNameOption() {
        return this.nameOption;
    }

    public static final class FinalBuilder
    extends Builder<EditorSettingsNode<?>, FinalBuilder> {
        private FinalBuilder(MapFactory mapFactory, ListFactory listFactory, List<ObjectCategorySetting<?>> allSettings) {
            super(mapFactory, listFactory, allSettings);
        }

        @Override
        protected EditorSettingsNode<?> buildInternally(List<IEditorSettingNode<?>> builtSettingData, Map<ObjectCategorySetting<?>, IEditorSettingNode<?>> builtSettingsDataMap) {
            EditorSettingsNode result = new EditorSettingsNode(builtSettingsDataMap, builtSettingData, this.deleteButtonBuilder.build(), this.protectionButtonBuilder.build(), this.nameOptionBuilder.build(), this.listFactory, this.rootSettings, this.movable, this.listEntryFactory, this.tooltipSupplier, this.protection);
            return result;
        }
    }

    public static abstract class Builder<SD extends EditorSettingsNode<?>, SDB extends Builder<SD, SDB>>
    extends EditorNode.Builder<Builder<SD, SDB>> {
        protected final SDB self = this;
        protected final Map<ObjectCategorySetting<?>, IEditorSettingNodeBuilder<?, ?>> settingMap;
        protected final List<IEditorSettingNodeBuilder<?, ?>> settingList;
        protected final EditorSimpleButtonNode.Builder deleteButtonBuilder;
        protected final EditorSimpleButtonNode.Builder protectionButtonBuilder;
        protected final EditorTextFieldOptionsNode.Builder nameOptionBuilder;
        protected final MapFactory mapFactory;
        protected final ListFactory listFactory;
        protected boolean rootSettings;
        protected boolean protection;

        protected Builder(MapFactory mapFactory, ListFactory listFactory, List<ObjectCategorySetting<?>> allSettings) {
            this.settingMap = mapFactory.get();
            this.settingList = listFactory.get();
            this.deleteButtonBuilder = EditorSimpleButtonNode.Builder.begin();
            this.protectionButtonBuilder = EditorSimpleButtonNode.Builder.begin();
            this.nameOptionBuilder = EditorTextFieldOptionsNode.Builder.begin(listFactory);
            this.mapFactory = mapFactory;
            this.listFactory = listFactory;
            for (ObjectCategorySetting<?> setting : allSettings) {
                this.addSetting(setting);
            }
        }

        private <V> void addSetting(ObjectCategorySetting<V> setting) {
            IEditorSettingNodeBuilder builder = setting.getSettingUIType().getSettingNodeBuilderFactory().apply(this.listFactory).setSetting(setting);
            this.settingMap.put(setting, builder);
            this.settingList.add(builder);
        }

        @Override
        public SDB setDefault() {
            super.setDefault();
            for (IEditorSettingNodeBuilder<?, ?> builder : this.settingList) {
                builder.setSettingValue(null);
            }
            this.setRootSettings(false);
            this.nameOptionBuilder.setDefault();
            this.deleteButtonBuilder.setDefault().setDisplayName(class_1074.method_4662((String)"gui.xaero_category_delete", (Object[])new Object[0])).setCallback((parent, bd, rl) -> {
                EditorSettingsNode settings = (EditorSettingsNode)parent;
                class_310 mc = class_310.method_1551();
                class_437 configScreen = mc.field_1755;
                class_5250 confirmSecondLine = class_2561.method_43471((String)settings.getNameOption().getResult()).method_27696(class_2583.field_24360.method_27703(class_5251.method_27718((class_124)class_124.field_1061)));
                mc.method_1507((class_437)new class_410(result -> {
                    if (result) {
                        settings.setToBeDeleted();
                    }
                    mc.method_1507(configScreen);
                }, (class_2561)class_2561.method_43471((String)"gui.xaero_category_delete_confirm"), (class_2561)confirmSecondLine));
            }).setIsActiveSupplier((parent, data) -> !((EditorSettingsNode)parent).getProtection());
            this.protectionButtonBuilder.setDefault().setDisplayName("").setCallback((parent, bd, rl) -> {
                EditorSettingsNode settings = (EditorSettingsNode)parent;
                boolean currentlyProtected = settings.getProtection();
                class_310 mc = class_310.method_1551();
                class_437 configScreen = mc.field_1755;
                class_5250 confirmFirstLine = class_2561.method_43471((String)(currentlyProtected ? "gui.xaero_category_disable_protection_confirm" : "gui.xaero_category_enable_protection_confirm"));
                class_124 confirmSecondLineColor = currentlyProtected ? class_124.field_1061 : class_124.field_1060;
                class_5250 confirmSecondLine = class_2561.method_43471((String)settings.getNameOption().getResult()).method_27696(class_2583.field_24360.method_27703(class_5251.method_27718((class_124)confirmSecondLineColor)));
                mc.method_1507((class_437)new class_410(result -> {
                    if (result) {
                        settings.setProtected(!settings.getProtection());
                    }
                    mc.method_1507(configScreen);
                }, (class_2561)confirmFirstLine, (class_2561)confirmSecondLine));
            }).setMessageSupplier((parent, bd) -> () -> class_1074.method_4662((String)(((EditorSettingsNode)parent).getProtection() ? "gui.xaero_category_disable_protection" : "gui.xaero_category_enable_protection"), (Object[])new Object[0])).setIsActiveSupplier((parent, bd) -> !((EditorSettingsNode)parent).isRootSettings()).setTooltipSupplier((parent, bd) -> new CursorBox((class_2561)class_2561.method_43471((String)"gui.xaero_box_category_protection")));
            this.setTooltipSupplier((parent, data) -> {
                if (!(parent instanceof EditorCategoryNode)) {
                    return null;
                }
                EditorCategoryNode category = (EditorCategoryNode)parent;
                class_5250 displayNameComponent = class_2561.method_43471((String)category.getDisplayName());
                CursorBox tooltip = new CursorBox((class_2561)class_2561.method_43469((String)"gui.xaero_box_category_settings", (Object[])new Object[]{displayNameComponent}));
                tooltip.setAutoLinebreak(false);
                return tooltip;
            });
            return this.self;
        }

        @Override
        protected EditorListRootEntry.CenteredEntryFactory getCenteredEntryFactory(EditorNode data, EditorNode parent, int index, GuiCategoryEditor.SettingRowList rowList) {
            return (x, y, width, height, root) -> {
                EditorButton button = new EditorButton(parent, true, 216, 20, data, rowList);
                return new EditorListEntryWidget(x, y, width, height, index, rowList, root, (class_339)button, data.getTooltipSupplier(parent));
            };
        }

        public <T> SDB setSettingValue(ObjectCategorySetting<T> setting, T value) {
            IEditorSettingNodeBuilder<?, ?> settingBuilder = this.settingMap.get(setting);
            settingBuilder.setSettingValue(value);
            return this.self;
        }

        public SDB setRootSettings(boolean rootSettings) {
            this.rootSettings = rootSettings;
            return this.self;
        }

        public SDB setProtection(boolean protection) {
            this.protection = protection;
            return this.self;
        }

        public EditorTextFieldOptionsNode.Builder getNameOptionBuilder() {
            return this.nameOptionBuilder;
        }

        public EditorSimpleButtonNode.Builder getDeleteButtonBuilder() {
            return this.deleteButtonBuilder;
        }

        @Override
        protected EditorNode buildInternally() {
            if (this.nameOptionBuilder.needsInputStringValidator()) {
                this.nameOptionBuilder.setInputStringValidator(s -> true);
            }
            List builtSettingData = this.settingList.stream().map(b -> b.setRootSettings(this.rootSettings)).map(IEditorSettingNodeBuilder::build).collect(this.listFactory::get, (rec$, x$0) -> ((List)rec$).add(x$0), List::addAll);
            Map<ObjectCategorySetting<?>, IEditorSettingNode<?>> builtSettingsDataMap = this.mapFactory.get();
            for (IEditorSettingNode sd : builtSettingData) {
                if (!(sd instanceof EditorOptionsNode)) {
                    throw new IllegalStateException("illegal setting data class! " + String.valueOf(sd.getClass()));
                }
                builtSettingsDataMap.put(sd.getSetting(), sd);
            }
            return this.buildInternally(builtSettingData, builtSettingsDataMap);
        }

        protected abstract SD buildInternally(List<IEditorSettingNode<?>> var1, Map<ObjectCategorySetting<?>, IEditorSettingNode<?>> var2);
    }
}

