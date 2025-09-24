/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.graphics.shader;

import xaero.common.graphics.shader.BuiltInCustomUniforms;

public class PositionTexAlphaTestShaderHelper {
    private static Float cachedDiscardAlpha;

    public static void setDiscardAlpha(float alpha) {
        if (cachedDiscardAlpha != null && cachedDiscardAlpha.floatValue() == alpha) {
            return;
        }
        cachedDiscardAlpha = Float.valueOf(alpha);
        BuiltInCustomUniforms.DISCARD_ALPHA.setValue(cachedDiscardAlpha);
    }
}

