/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2338
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 */
package xaero.hud.minimap.info.render.compile;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.module.MinimapSession;

public final class InfoDisplayCompiler {
    private boolean compiling;
    private int size;
    private final List<class_2561> compiledLines;

    private InfoDisplayCompiler(List<class_2561> compiledLines) {
        this.compiledLines = compiledLines;
    }

    public <T> List<class_2561> compile(InfoDisplay<T> infoDisplay, MinimapSession minimapSession, int size, class_2338 playerPos) {
        if (this.compiling) {
            throw new IllegalStateException();
        }
        this.compiling = true;
        this.size = size;
        this.compiledLines.clear();
        infoDisplay.getCompiler().onCompile(infoDisplay, this, minimapSession, size, playerPos);
        this.compiling = false;
        return this.compiledLines;
    }

    public void addWords(String text) {
        if (!this.compiling) {
            throw new IllegalStateException();
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1772.method_1727(text) <= this.size) {
            this.compiledLines.add((class_2561)class_2561.method_43470((String)text));
        } else {
            String[] words = text.split(" ");
            StringBuilder lineBuilder = new StringBuilder();
            for (int i = 0; i < words.length; ++i) {
                int lineWidth;
                int wordStart = lineBuilder.length();
                if (i > 0) {
                    lineBuilder.append(' ');
                }
                lineBuilder.append(words[i]);
                if (i == 0 || (lineWidth = mc.field_1772.method_1727(lineBuilder.toString())) <= this.size) continue;
                lineBuilder.delete(wordStart, lineBuilder.length());
                this.compiledLines.add((class_2561)class_2561.method_43470((String)lineBuilder.toString()));
                lineBuilder.delete(0, lineBuilder.length());
                lineBuilder.append(words[i]);
            }
            this.compiledLines.add((class_2561)class_2561.method_43470((String)lineBuilder.toString()));
        }
    }

    public void addLine(class_2561 line) {
        if (!this.compiling) {
            throw new IllegalStateException();
        }
        this.compiledLines.add(line);
    }

    public void addLine(String line) {
        this.addLine((class_2561)class_2561.method_43470((String)line));
    }

    public static final class Builder {
        private Builder() {
        }

        private Builder setDefault() {
            return this;
        }

        public InfoDisplayCompiler build() {
            return new InfoDisplayCompiler(new ArrayList<class_2561>());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

