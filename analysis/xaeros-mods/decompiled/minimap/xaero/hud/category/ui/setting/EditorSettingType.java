/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.setting;

import xaero.common.misc.ListFactory;
import xaero.hud.category.ui.node.options.range.setting.EditorCompactSettingNode;
import xaero.hud.category.ui.node.options.range.setting.EditorExpandingSettingNode;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNodeBuilder;

public final class EditorSettingType {
    public static final EditorSettingType ITERATION_BUTTON = new EditorSettingType(true, new SettingNodeBuilderFactory(){

        @Override
        public <V> IEditorSettingNodeBuilder<V, ?> apply(ListFactory listFactory) {
            return EditorCompactSettingNode.Builder.begin();
        }
    });
    public static final EditorSettingType SLIDER = new EditorSettingType(true, new SettingNodeBuilderFactory(){

        @Override
        public <V> IEditorSettingNodeBuilder<V, ?> apply(ListFactory listFactory) {
            return EditorCompactSettingNode.Builder.begin().setSlider(true);
        }
    });
    public static final EditorSettingType EXPANDING = new EditorSettingType(true, EditorExpandingSettingNode.Builder::begin);
    private final boolean usingIndices;
    private final SettingNodeBuilderFactory settingNodeBuilderFactory;

    private EditorSettingType(boolean usingIndices, SettingNodeBuilderFactory settingNodeBuilderFactory) {
        this.usingIndices = usingIndices;
        this.settingNodeBuilderFactory = settingNodeBuilderFactory;
    }

    public SettingNodeBuilderFactory getSettingNodeBuilderFactory() {
        return this.settingNodeBuilderFactory;
    }

    public boolean isUsingIndices() {
        return this.usingIndices;
    }

    @FunctionalInterface
    public static interface SettingNodeBuilderFactory {
        public <V> IEditorSettingNodeBuilder<V, ?> apply(ListFactory var1);
    }
}

