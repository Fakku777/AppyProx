/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_5348
 */
package xaero.common.graphics;

import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_5348;
import xaero.common.misc.TextSplitter;

public class CursorBox
implements Supplier<CursorBox> {
    private static final int BOX_OFFSET_X = 12;
    private static final int BOX_OFFSET_Y = 10;
    private static final int START_WIDTH = 20;
    private static final int USUAL_WIDTH = 200;
    private ArrayList<class_2561> strings;
    private class_2561 directText;
    private boolean directTextReady;
    private String language;
    private String fullCode;
    private class_2583 codeStyle;
    private String plainText;
    private int boxWidth = 20;
    private int startWidth = 20;
    private static final int color = -939524096;
    private boolean customLines;
    private boolean flippedByDefault;
    private boolean autoLinebreak;

    public CursorBox(String code) {
        this(code, class_2583.field_24360);
    }

    public CursorBox(String code, class_2583 codeStyle) {
        this(code, codeStyle, false);
    }

    public CursorBox(String code, class_2583 codeStyle, boolean flippedByDefault) {
        this.fullCode = code;
        this.codeStyle = codeStyle;
        this.flippedByDefault = flippedByDefault;
        this.autoLinebreak = true;
    }

    public CursorBox(class_2561 directText) {
        this(directText, false);
    }

    public CursorBox(class_2561 directText, boolean flippedByDefault) {
        this.directText = directText;
        this.flippedByDefault = flippedByDefault;
        this.autoLinebreak = true;
    }

    public CursorBox(int size) {
        this.strings = new ArrayList();
        for (int i = 0; i < size; ++i) {
            this.strings.add((class_2561)class_2561.method_43470((String)""));
        }
        this.customLines = true;
    }

    public void setStartWidth(int startWidth) {
        this.startWidth = startWidth;
    }

    private String currentLanguage() {
        return class_310.method_1551().method_1526().method_4669();
    }

    public void createLines(class_2561 text) {
        try {
            this.language = this.currentLanguage();
        }
        catch (NullPointerException e) {
            this.language = "en_us";
        }
        this.strings = new ArrayList();
        this.splitWords(this.strings, (class_5348)text);
    }

    public void splitWords(ArrayList<class_2561> dest, class_5348 formattedText) {
        StringBuilder plainTextBuilder = new StringBuilder();
        this.boxWidth = 20 + TextSplitter.splitTextIntoLines(dest, this.startWidth - 20, (this.autoLinebreak ? 200 : Integer.MAX_VALUE) - 20, formattedText, plainTextBuilder);
        this.plainText = plainTextBuilder.toString().replaceAll("(\u00a7[0-9a-g])+", "");
    }

    public class_2561 getLine(int line) {
        return this.strings.get(line);
    }

    private void ensure() {
        try {
            if (!(this.customLines || (this.fullCode != null || this.directTextReady) && this.language != null && this.language.equals(this.currentLanguage()))) {
                if (this.fullCode != null) {
                    this.createLines((class_2561)class_2561.method_43471((String)this.fullCode).method_27696(this.codeStyle));
                } else {
                    this.createLines(this.directText);
                    this.directTextReady = true;
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void drawBox(class_332 guiGraphics, int x, int y, int width, int height) {
        int h;
        int overEdgeY;
        this.ensure();
        int drawX = x + 12;
        int drawY = y + 10;
        int overEdgeX = drawX + this.boxWidth - width;
        if (this.flippedByDefault || overEdgeX > 9) {
            drawX = x - 12 - this.boxWidth;
        } else if (overEdgeX > 0) {
            drawX -= overEdgeX;
        }
        if (drawX < 0) {
            drawX = 0;
        }
        if ((overEdgeY = drawY + (h = 5 + this.strings.size() * 10 + 5) - height) > h / 2) {
            drawY = y - 10 - h;
        } else if (overEdgeY > 0) {
            drawY -= overEdgeY;
        }
        if (drawY < 0) {
            drawY = 0;
        }
        guiGraphics.method_25294(drawX, drawY, drawX + this.boxWidth, drawY + h, -939524096);
        for (int i = 0; i < this.strings.size(); ++i) {
            class_2561 s = this.getLine(i);
            guiGraphics.method_27535(class_310.method_1551().field_1772, s, drawX + 10, drawY + 6 + 10 * i, -1);
        }
    }

    public CursorBox withWidth(int boxWidth) {
        this.boxWidth = boxWidth;
        return this;
    }

    public void setAutoLinebreak(boolean autoLinebreak) {
        this.autoLinebreak = autoLinebreak;
    }

    public String getPlainText() {
        this.ensure();
        return this.plainText;
    }

    public String getFullCode() {
        return this.fullCode;
    }

    @Override
    public CursorBox get() {
        return this;
    }
}

