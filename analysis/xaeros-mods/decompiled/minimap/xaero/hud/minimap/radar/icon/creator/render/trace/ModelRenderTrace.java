/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.minecraft.class_1058
 *  net.minecraft.class_2960
 *  net.minecraft.class_3879
 *  net.minecraft.class_630
 */
package xaero.hud.minimap.radar.icon.creator.render.trace;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.HashMap;
import java.util.Set;
import net.minecraft.class_1058;
import net.minecraft.class_2960;
import net.minecraft.class_3879;
import net.minecraft.class_630;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelPartRenderTrace;

public class ModelRenderTrace {
    public final class_3879 model;
    public final class_2960 renderTexture;
    public final class_1058 renderAtlasSprite;
    public final RenderPipeline layerPipeline;
    public int color;
    public boolean allVisible;
    private HashMap<class_630, ModelPartRenderTrace> visibleParts;

    public ModelRenderTrace(class_3879 model, class_2960 renderTexture, class_1058 renderAtlasSprite, RenderPipeline layerPipeline, int color) {
        this.model = model;
        this.renderTexture = renderTexture;
        this.renderAtlasSprite = renderAtlasSprite;
        this.layerPipeline = layerPipeline;
        this.color = color;
    }

    public String toString() {
        return String.valueOf(this.model) + " " + String.valueOf(this.layerPipeline.getLocation());
    }

    public void addVisibleModelPart(class_630 part, int color) {
        if (this.visibleParts == null) {
            this.visibleParts = new HashMap();
        }
        this.visibleParts.put(part, new ModelPartRenderTrace(part, color));
    }

    public ModelPartRenderTrace getModelPartRenderInfo(class_630 part) {
        ModelPartRenderTrace mprdi;
        ModelPartRenderTrace modelPartRenderTrace = mprdi = this.visibleParts == null ? null : this.visibleParts.get(part);
        if (mprdi == null && this.allVisible) {
            mprdi = new ModelPartRenderTrace(part, this.color);
        }
        return mprdi;
    }

    public boolean isEmpty() {
        return !this.allVisible && (this.visibleParts == null || this.visibleParts.isEmpty());
    }

    public boolean sameVisibility(ModelRenderTrace other) {
        HashMap<class_630, ModelPartRenderTrace> otherVisibleParts;
        if (this.visibleParts == null != ((otherVisibleParts = other.visibleParts) == null)) {
            return false;
        }
        if (this.visibleParts == null) {
            return true;
        }
        if (this.visibleParts.size() != otherVisibleParts.size()) {
            return false;
        }
        Set<class_630> keySet = this.visibleParts.keySet();
        for (class_630 key : keySet) {
            if (otherVisibleParts.containsKey(key)) continue;
            return false;
        }
        return true;
    }
}

