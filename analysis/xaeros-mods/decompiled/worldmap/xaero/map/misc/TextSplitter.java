/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 *  net.minecraft.class_5348
 *  net.minecraft.class_5348$class_5246
 *  net.minecraft.class_8828
 */
package xaero.map.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_5348;
import net.minecraft.class_8828;

public class TextSplitter {
    public static int splitTextIntoLines(List<class_2561> dest, int minWidth, int widthLimit, class_5348 formattedText, StringBuilder plainTextBuilder) {
        SplitProgress progress = new SplitProgress();
        int spaceWidth = class_310.method_1551().field_1772.method_1727(" ");
        progress.resultWidth = minWidth;
        class_5348.class_5246 consumer = (style, text) -> {
            boolean endsWithSpace;
            boolean isEnd;
            boolean bl = isEnd = style == null;
            if (!isEnd && plainTextBuilder != null) {
                plainTextBuilder.append(text);
            }
            if (endsWithSpace = ((String)text).endsWith(" ")) {
                text = (String)text + ".";
            }
            String[] parts = ((String)text).split(" ");
            for (int i = 0; i < parts.length; ++i) {
                boolean isNewLine;
                boolean canAddMultiword = isEnd || i < parts.length - 1;
                String part = isEnd || endsWithSpace && i == parts.length - 1 ? "" : parts[i];
                int partWidth = class_310.method_1551().field_1772.method_1727(part);
                if (!canAddMultiword) {
                    progress.buildMultiword(part, partWidth, style);
                    continue;
                }
                int wordWidth = partWidth + progress.multiwordWidth;
                int wordTakesWidth = wordWidth + (!progress.firstWord ? spaceWidth : 0);
                if (progress.lineWidth + wordTakesWidth <= widthLimit) {
                    progress.resultWidth = Math.max(progress.resultWidth, Math.min(widthLimit, progress.lineWidth + wordTakesWidth));
                }
                if (progress.firstWord && progress.lineWidth + wordTakesWidth > progress.resultWidth) {
                    progress.resultWidth = progress.lineWidth + wordTakesWidth;
                }
                boolean bl2 = isNewLine = progress.multiword == null && part.equals("\n");
                if (!isNewLine && progress.lineWidth + wordTakesWidth <= progress.resultWidth) {
                    progress.confirmWord(part, style, wordTakesWidth);
                    continue;
                }
                progress.confirmComponent();
                dest.add(progress.line);
                progress.nextLine();
                if (isNewLine) continue;
                --i;
            }
            return Optional.empty();
        };
        formattedText.method_27658(consumer, class_2583.field_24360.method_10977(class_124.field_1068));
        if (progress.multiword != null) {
            consumer.accept(null, "end");
        } else if (progress.stringBuilder.length() > 0) {
            progress.confirmComponent();
        }
        if (progress.line != null) {
            dest.add(progress.line);
        }
        if (progress.resultWidth > minWidth) {
            --progress.resultWidth;
        }
        return progress.resultWidth;
    }

    public static class SplitProgress {
        int multiwordWidth;
        List<class_5250> multiword = null;
        boolean firstWord = true;
        class_2561 line = null;
        StringBuilder stringBuilder = new StringBuilder();
        int lineWidth;
        class_2583 lastStyle;
        int resultWidth;

        public void buildMultiword(String wordPart, int width, class_2583 style) {
            class_5250 wordPartComponent = class_2561.method_43470((String)wordPart).method_27696(style);
            if (this.multiword == null) {
                this.multiword = new ArrayList<class_5250>();
            }
            this.multiword.add(wordPartComponent);
            this.multiwordWidth += width;
        }

        private void confirmWordPart(String part, class_2583 style) {
            if (this.lastStyle != null && !Objects.equals(style, this.lastStyle)) {
                this.confirmComponent();
            }
            this.stringBuilder.append(part);
            this.lastStyle = style;
        }

        public void confirmWord(String lastPart, class_2583 lastPartStyle, int width) {
            if (!this.firstWord) {
                this.stringBuilder.append(" ");
            }
            if (this.multiword != null) {
                for (class_2561 class_25612 : this.multiword) {
                    String text = ((class_8828)class_25612.method_10851()).comp_737();
                    class_2583 style = class_25612.method_10866();
                    this.confirmWordPart(text, style);
                }
                this.multiword = null;
                this.multiwordWidth = 0;
            }
            this.confirmWordPart(lastPart, lastPartStyle);
            this.lineWidth += width;
            this.firstWord = false;
        }

        public void confirmComponent() {
            class_5250 comp = class_2561.method_43470((String)this.stringBuilder.toString()).method_27696(this.lastStyle == null ? class_2583.field_24360 : this.lastStyle);
            if (this.line != null) {
                if (this.stringBuilder.length() > 0) {
                    this.line.method_10855().add(comp);
                }
            } else {
                this.line = comp;
            }
            this.stringBuilder.delete(0, this.stringBuilder.length());
        }

        public void nextLine() {
            this.firstWord = true;
            this.line = null;
            this.lastStyle = null;
            this.lineWidth = 0;
        }
    }
}

