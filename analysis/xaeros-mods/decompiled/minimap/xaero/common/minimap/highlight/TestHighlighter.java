/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 *  net.minecraft.class_5321
 */
package xaero.common.minimap.highlight;

import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_5321;
import xaero.common.minimap.highlight.ChunkHighlighter;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;

public class TestHighlighter
extends ChunkHighlighter {
    public TestHighlighter() {
        super(true);
    }

    @Override
    public boolean regionHasHighlights(class_5321<class_1937> dimension, int regionX, int regionZ) {
        return true;
    }

    @Override
    protected int[] getColors(class_5321<class_1937> dimension, int chunkX, int chunkZ) {
        if (!this.chunkIsHighlit(dimension, chunkX, chunkZ)) {
            return null;
        }
        int centerColor = -11184777;
        int sideColor = -11184692;
        this.resultStore[0] = centerColor;
        this.resultStore[1] = (chunkZ & 3) == 0 ? sideColor : centerColor;
        this.resultStore[2] = (chunkX & 3) == 3 ? sideColor : centerColor;
        this.resultStore[3] = (chunkZ & 3) == 3 ? sideColor : centerColor;
        this.resultStore[4] = (chunkX & 3) == 0 ? sideColor : centerColor;
        return this.resultStore;
    }

    @Override
    public boolean chunkIsHighlit(class_5321<class_1937> dimension, int chunkX, int chunkZ) {
        return (chunkX >> 2 & 1) == (chunkZ >> 2 & 1);
    }

    @Override
    public void addChunkHighlightTooltips(InfoDisplayCompiler compiler, class_5321<class_1937> dimension, int chunkX, int chunkZ, int width) {
        compiler.addLine((class_2561)class_2561.method_43470((String)"subtle!"));
    }
}

