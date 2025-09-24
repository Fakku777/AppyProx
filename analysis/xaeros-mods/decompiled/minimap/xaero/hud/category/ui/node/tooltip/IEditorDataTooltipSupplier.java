/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.tooltip;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import xaero.common.graphics.CursorBox;
import xaero.hud.category.ui.node.EditorNode;

public interface IEditorDataTooltipSupplier
extends BiFunction<EditorNode, EditorNode, Supplier<CursorBox>> {
}

