/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11244
 *  net.minecraft.class_11246
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_408
 *  net.minecraft.class_418
 */
package xaero.hud.minimap.module;

import net.minecraft.class_11244;
import net.minecraft.class_11246;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_408;
import net.minecraft.class_418;
import xaero.common.HudMod;
import xaero.common.core.IGuiGraphics;
import xaero.common.effect.Effects;
import xaero.common.gui.IScreenBase;
import xaero.common.misc.Misc;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.render.module.IModuleRenderer;
import xaero.hud.render.module.ModuleRenderContext;

public class MinimapRenderer
implements IModuleRenderer<MinimapSession> {
    @Override
    public void render(MinimapSession session, ModuleRenderContext c, class_332 guiGraphics, float partialTicks) {
        class_310 mc = class_310.method_1551();
        if (Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_MINIMAP) || Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_MINIMAP_HARMFUL) || session.getProcessor().getNoMinimapMessageReceived()) {
            return;
        }
        if (session.getHideMinimapUnderScreen() && mc.field_1755 != null && !(mc.field_1755 instanceof IScreenBase) && !(mc.field_1755 instanceof class_408) && !(mc.field_1755 instanceof class_418) || session.getHideMinimapUnderF3() && mc.method_53526().method_53536()) {
            return;
        }
        session.getProcessor().getDepthTraceListener().update(c.x, c.y, c.screenWidth, c.screenHeight, c.screenScale, session.getConfiguredWidth(), c.w, partialTicks, HudMod.INSTANCE.getHudRenderer().getCustomVertexConsumers());
        class_11246 guiRenderState = ((IGuiGraphics)guiGraphics).xaero_mm_getGuiRenderState();
        guiRenderState.method_70919((class_11244)session.getProcessor().getDepthTracer());
        guiRenderState.method_70919((class_11244)session.getProcessor().getDepthSkipper());
    }
}

