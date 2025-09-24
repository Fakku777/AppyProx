/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2338
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5348
 */
package xaero.hud.minimap.info.render;

import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_5348;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.misc.Misc;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.render.util.RenderBufferUtil;

public final class InfoDisplayRenderer {
    public static final int DEPTH_OFFSET = 2;
    private final InfoDisplayCompiler compiler;

    private InfoDisplayRenderer(InfoDisplayCompiler compiler) {
        this.compiler = compiler;
    }

    public void render(class_4587 matrixStack, MinimapSession session, Minimap minimap, int height, int size, class_2338 playerPos, int scaledX, int scaledY, float mapScale, class_4597.class_4598 renderTypeBuffer) {
        ModSettings settings = minimap.getModMain().getSettings();
        Iterator iterator = minimap.getInfoDisplays().getManager().getOrderedStream().iterator();
        int interfaceSize = size;
        int scaledHeight = (int)((float)height * mapScale);
        int align = settings.minimapTextAlign;
        boolean under = scaledY + interfaceSize / 2 < scaledHeight / 2;
        int stringY = scaledY + (under ? interfaceSize : -9);
        int bgOpacityMask = settings.infoDisplayBackgroundOpacity * 255 / 100 << 24;
        matrixStack.method_22904(0.0, 0.0, 0.01);
        while (iterator.hasNext()) {
            InfoDisplay infoDisplay = (InfoDisplay)iterator.next();
            List<class_2561> compiledLines = this.compiler.compile(infoDisplay, session, size, playerPos);
            int textColorIndex = infoDisplay.getTextColor();
            int backgroundColorIndex = infoDisplay.getBackgroundColor();
            int textColor = ModSettings.COLORS[textColorIndex < 0 ? 15 : textColorIndex % ModSettings.COLORS.length];
            int backgroundColor = backgroundColorIndex < 0 ? 0 : bgOpacityMask | ModSettings.COLORS[backgroundColorIndex % ModSettings.COLORS.length] & 0xFFFFFF;
            class_4588 backgroundVertexBuffer = renderTypeBuffer.getBuffer(CustomRenderTypes.COLORED_WAYPOINTS_BGS);
            int startIndex = 0;
            int endIndex = compiledLines.size();
            int step = 1;
            if (!under) {
                startIndex = endIndex - 1;
                endIndex = -1;
                step = -1;
            }
            for (int i = startIndex; i != endIndex; i += step) {
                class_2561 s = compiledLines.get(i);
                int stringWidth = class_310.method_1551().field_1772.method_27525((class_5348)s);
                int stringX = scaledX + (align == 0 ? interfaceSize / 2 - stringWidth / 2 : (align == 1 ? 6 : interfaceSize - 6 - stringWidth));
                if (backgroundColor != 0) {
                    RenderBufferUtil.addColoredRect(matrixStack.method_23760().method_23761(), backgroundVertexBuffer, stringX - 1, stringY - 1, stringWidth + 2, 10, backgroundColor);
                }
                Misc.drawNormalText(matrixStack, s, (float)stringX, (float)stringY, textColor, true, renderTypeBuffer);
                stringY += 10 * step;
            }
            compiledLines.clear();
        }
        matrixStack.method_22904(0.0, 0.0, -0.01);
        renderTypeBuffer.method_22993();
    }

    public static final class Builder {
        private Builder() {
        }

        private Builder setDefault() {
            return this;
        }

        public InfoDisplayRenderer build() {
            return new InfoDisplayRenderer(InfoDisplayCompiler.Builder.begin().build());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

