/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.class_10017
 *  net.minecraft.class_1297
 *  net.minecraft.class_583
 *  net.minecraft.class_897
 */
package xaero.hud.minimap.radar.icon.creator.render.form;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_10017;
import net.minecraft.class_1297;
import net.minecraft.class_583;
import net.minecraft.class_897;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;

public interface IRadarIconFormPrerenderer {
    public boolean requiresEntityModel();

    public boolean isFlipped();

    public boolean isOutlined();

    public <S extends class_10017> boolean prerender(MinimapElementGraphics var1, class_897<?, ? super S> var2, S var3, @Nullable class_583<S> var4, class_1297 var5, @Nullable List<ModelRenderTrace> var6, RadarIconCreator.Parameters var7);
}

