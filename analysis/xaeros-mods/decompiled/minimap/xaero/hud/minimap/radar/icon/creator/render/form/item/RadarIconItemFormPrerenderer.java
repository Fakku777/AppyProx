/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.class_10017
 *  net.minecraft.class_10444
 *  net.minecraft.class_1297
 *  net.minecraft.class_1533
 *  net.minecraft.class_1542
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1935
 *  net.minecraft.class_2960
 *  net.minecraft.class_308$class_11274
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4608
 *  net.minecraft.class_583
 *  net.minecraft.class_6880$class_6883
 *  net.minecraft.class_7923
 *  net.minecraft.class_811
 *  net.minecraft.class_897
 */
package xaero.hud.minimap.radar.icon.creator.render.form.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_10017;
import net.minecraft.class_10444;
import net.minecraft.class_1297;
import net.minecraft.class_1533;
import net.minecraft.class_1542;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_2960;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_583;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_811;
import net.minecraft.class_897;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.creator.render.form.IRadarIconFormPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.item.RadarIconItemForm;

public class RadarIconItemFormPrerenderer
implements IRadarIconFormPrerenderer {
    @Override
    public boolean requiresEntityModel() {
        return false;
    }

    @Override
    public boolean isFlipped() {
        return false;
    }

    @Override
    public boolean isOutlined() {
        return true;
    }

    @Override
    public <S extends class_10017> boolean prerender(MinimapElementGraphics guiGraphics, class_897<?, ? super S> entityRenderer, S entityRenderState, @Nullable class_583<S> entityModel, class_1297 entity, @Nullable List<ModelRenderTrace> traceResult, RadarIconCreator.Parameters parameters) {
        RadarIconItemForm itemForm = (RadarIconItemForm)parameters.form;
        class_1799 itemStack = this.getItemToRender(entity, itemForm);
        if (itemStack == null || itemStack.method_7960()) {
            return false;
        }
        class_10444 itemStackRenderState = new class_10444();
        class_310.method_1551().method_65386().method_65598(itemStackRenderState, itemStack, class_811.field_4317, null, null, 0);
        class_310.method_1551().field_1773.method_71114().method_71034(itemStackRenderState.method_65608() ? class_308.class_11274.field_60027 : class_308.class_11274.field_60026);
        class_4587 matrixStack = guiGraphics.pose();
        int halfIcon = 32;
        matrixStack.method_46416((float)halfIcon, (float)halfIcon, 1.0f);
        float scale = parameters.scale;
        if (scale < 1.0f) {
            matrixStack.method_22905(scale, scale, 1.0f);
        }
        matrixStack.method_46416(0.0f, 0.0f, -300.0f);
        matrixStack.method_22905(16.0f, -16.0f, 16.0f);
        itemStackRenderState.method_65604(matrixStack, (class_4597)guiGraphics.getBufferSource(), 0xF000F0, class_4608.field_21444);
        guiGraphics.flush();
        return true;
    }

    private class_1799 getItemToRender(class_1297 entity, RadarIconItemForm itemForm) {
        class_2960 itemKey = itemForm.getItemKey();
        if (itemKey != null) {
            class_1792 item;
            class_6880.class_6883 itemReference = class_7923.field_41178.method_10223(itemKey).orElse(null);
            class_1792 class_17922 = item = itemReference == null || !itemReference.method_40227() ? null : (class_1792)itemReference.comp_349();
            if (item == null) {
                return null;
            }
            return new class_1799((class_1935)item);
        }
        class_1799 selfStack = this.getSelfItem(entity);
        if (selfStack == null) {
            return null;
        }
        return new class_1799((class_1935)selfStack.method_7909());
    }

    private class_1799 getSelfItem(class_1297 entity) {
        if (entity instanceof class_1542) {
            class_1542 itemEntity = (class_1542)entity;
            return itemEntity.method_6983();
        }
        if (entity instanceof class_1533) {
            class_1533 itemFrame = (class_1533)entity;
            return itemFrame.method_6940();
        }
        return entity.method_31480();
    }
}

