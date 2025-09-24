/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 */
package xaero.common.minimap.render;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import xaero.common.graphics.CustomVertexConsumers;
import xaero.common.minimap.MinimapProcessor;

public class MinimapDepthTraceListener
implements FloatConsumer {
    private final MinimapProcessor processor;
    private int x;
    private int y;
    private int width;
    private int height;
    private double scale;
    private int size;
    private int boxSize;
    private float partial;
    private CustomVertexConsumers cvc;

    public MinimapDepthTraceListener(MinimapProcessor processor) {
        this.processor = processor;
    }

    public void update(int x, int y, int width, int height, double scale, int size, int boxSize, float partial, CustomVertexConsumers cvc) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.size = size;
        this.boxSize = boxSize;
        this.partial = partial;
        this.cvc = cvc;
    }

    public void accept(float depth) {
        this.processor.onRender(this.x, this.y, this.width, this.height, this.scale, this.size, this.boxSize, this.partial, this.cvc, depth);
    }
}

