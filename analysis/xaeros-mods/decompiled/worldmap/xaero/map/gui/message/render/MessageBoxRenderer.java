/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_5348
 *  org.joml.Matrix3x2fStack
 */
package xaero.map.gui.message.render;

import java.util.Iterator;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5348;
import org.joml.Matrix3x2fStack;
import xaero.map.gui.message.Message;
import xaero.map.gui.message.MessageBox;

public class MessageBoxRenderer {
    private final int OPAQUE_FOR = 5000;
    private final int FADE_FOR = 3000;

    public void render(class_332 guiGraphics, MessageBox messageBox, class_327 font, int x, int y, boolean rightAlign) {
        Message message;
        int passed;
        float opacity;
        int alphaInt;
        Matrix3x2fStack matrixStack = guiGraphics.method_51448();
        long time = System.currentTimeMillis();
        matrixStack.pushMatrix();
        matrixStack.translate((float)x, (float)y);
        int index = 0;
        Iterator<Message> iterator = messageBox.getIterator();
        while (iterator.hasNext() && (alphaInt = (int)((opacity = (passed = (int)(time - (message = iterator.next()).getAdditionTime())) < 5000 ? 1.0f : (float)(3000 - (passed - 5000)) / 3000.0f) * 255.0f)) > 3) {
            int textColor = 0xFFFFFF | alphaInt << 24;
            int bgColor = (int)(0.5f * (float)alphaInt) << 24;
            int textWidth = font.method_27525((class_5348)message.getText());
            int textX = rightAlign ? -textWidth - 1 : 2;
            int textY = -index * 10 - 4;
            int bgWidth = textWidth + 3;
            guiGraphics.method_25294(textX - 2, textY - 1, textX - 2 + bgWidth, textY + 9, bgColor);
            guiGraphics.method_27535(font, message.getText(), textX, textY, textColor);
            ++index;
        }
        matrixStack.popMatrix();
    }
}

