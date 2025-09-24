/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package xaero.hud.category.ui.node.options;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.options.EditorOptionNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public abstract class EditorOptionsNode<V>
extends EditorNode {
    protected EditorOptionNode<V> currentValue;
    protected Supplier<String> messageSupplier;
    protected final String displayName;
    private final IOptionsNodeIsActiveSupplier isActiveSupplier;

    protected EditorOptionsNode(@Nonnull String displayName, boolean movable, EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, IOptionsNodeIsActiveSupplier isActiveSupplier) {
        super(movable, listEntryFactory, tooltipSupplier);
        this.displayName = displayName;
        this.isActiveSupplier = isActiveSupplier;
    }

    public EditorOptionNode<V> getCurrentValue() {
        return this.currentValue;
    }

    public void setCurrentValue(EditorOptionNode<V> currentValue) {
        this.currentValue = currentValue;
    }

    public final Supplier<String> getMessageSupplier() {
        if (this.messageSupplier == null) {
            this.messageSupplier = () -> this.isExpanded() ? this.displayName : String.format("%s: %s", this.displayName, this.currentValue.getDisplayName());
        }
        return this.messageSupplier;
    }

    public IOptionsNodeIsActiveSupplier getIsActiveSupplier() {
        return this.isActiveSupplier;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @FunctionalInterface
    public static interface IOptionsNodeIsActiveSupplier {
        public boolean get(EditorNode var1, EditorOptionsNode<?> var2);
    }

    public static abstract class Builder<V, B extends Builder<V, B>>
    extends EditorNode.Builder<B> {
        protected B self = this;
        protected V currentValue;
        protected String displayName;
        protected IOptionsNodeIsActiveSupplier isActiveSupplier;

        protected Builder() {
        }

        @Override
        public B setDefault() {
            super.setDefault();
            this.setCurrentValue(null);
            this.setDisplayName(null);
            this.setIsActiveSupplier((p, d) -> true);
            return this.self;
        }

        public B setCurrentValue(V currentValue) {
            this.currentValue = currentValue;
            return this.self;
        }

        public B setDisplayName(String displayName) {
            this.displayName = displayName;
            return this.self;
        }

        public B setIsActiveSupplier(IOptionsNodeIsActiveSupplier isActiveSupplier) {
            this.isActiveSupplier = isActiveSupplier;
            return this.self;
        }

        @Override
        public EditorOptionsNode<V> build() {
            if (this.displayName == null) {
                throw new IllegalStateException("required fields not set!");
            }
            EditorOptionsNode result = (EditorOptionsNode)super.build();
            return result;
        }

        @Override
        protected abstract EditorOptionsNode<V> buildInternally();
    }
}

