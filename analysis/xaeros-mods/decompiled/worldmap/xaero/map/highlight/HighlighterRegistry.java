/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.highlight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import xaero.map.highlight.AbstractHighlighter;

public class HighlighterRegistry {
    private List<AbstractHighlighter> highlighters = new ArrayList<AbstractHighlighter>();

    public void register(AbstractHighlighter highlighter) {
        this.highlighters.add(highlighter);
    }

    public void end() {
        this.highlighters = Collections.unmodifiableList(this.highlighters);
    }

    public List<AbstractHighlighter> getHighlighters() {
        return this.highlighters;
    }
}

