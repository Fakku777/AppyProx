/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_630
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.part;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_630;
import xaero.hud.minimap.radar.icon.creator.render.form.model.RadarIconModelPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.RadarIconModelPartPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.resolver.RadarIconModelFieldResolver;

public class ResolvedFieldModelPartRenderer
implements RadarIconModelFieldResolver.Listener {
    private class_4587 matrixStack;
    private class_4588 vertexConsumer;
    private boolean justOne;
    private class_630 mainPart;
    private RadarIconModelPartPrerenderer modelPartPrerenderer;
    private RadarIconModelPrerenderer.Parameters parameters;
    private boolean stop;

    public void prepare(class_4587 matrixStack, class_4588 vertexConsumer, boolean justOne, class_630 mainPart, RadarIconModelPrerenderer.Parameters parameters, RadarIconModelPartPrerenderer modelPartPrerenderer) {
        this.matrixStack = matrixStack;
        this.vertexConsumer = vertexConsumer;
        this.justOne = justOne;
        this.mainPart = mainPart;
        this.parameters = parameters;
        this.modelPartPrerenderer = modelPartPrerenderer;
        this.stop = false;
    }

    @Override
    public boolean isFieldAllowed(Field f) {
        try {
            f.getType().asSubclass(class_630.class);
        }
        catch (ClassCastException cce) {
            try {
                f.getType().asSubclass(class_630[].class);
            }
            catch (ClassCastException cce1) {
                try {
                    f.getType().asSubclass(Collection.class);
                }
                catch (ClassCastException cce2) {
                    try {
                        f.getType().asSubclass(Map.class);
                    }
                    catch (ClassCastException cce3) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldStop() {
        return this.stop;
    }

    @Override
    public void onFieldResolved(Object[] resolved, String matchedFilterElement) {
        class_4587 matrixStack = this.matrixStack;
        class_4588 vertexConsumer = this.vertexConsumer;
        boolean justOne = this.justOne;
        RadarIconModelPartPrerenderer modelPartPrerenderer = this.modelPartPrerenderer;
        for (Object o : resolved) {
            if (!(o instanceof class_630)) continue;
            class_630 part = (class_630)o;
            if (this.mainPart == null) {
                this.mainPart = part;
            }
            modelPartPrerenderer.renderPart(matrixStack, vertexConsumer, part, this.mainPart, this.parameters);
            if (!justOne) continue;
            this.stop = true;
            break;
        }
    }

    public class_630 getMainPart() {
        return this.mainPart;
    }
}

