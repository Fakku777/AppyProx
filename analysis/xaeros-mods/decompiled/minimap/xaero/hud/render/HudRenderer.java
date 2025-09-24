/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 */
package xaero.hud.render;

import net.minecraft.class_310;
import net.minecraft.class_332;
import xaero.common.graphics.CustomVertexConsumers;
import xaero.common.graphics.OpenGlHelper;
import xaero.hud.Hud;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleSession;
import xaero.hud.preset.HudPreset;
import xaero.hud.pushbox.PushboxHandler;
import xaero.hud.render.module.IModuleRenderer;
import xaero.hud.render.module.ModuleRenderContext;

public final class HudRenderer {
    private final PushboxHandler pushboxHandler;
    private final CustomVertexConsumers customVertexConsumers;

    public HudRenderer(PushboxHandler pushboxHandler) {
        this.pushboxHandler = pushboxHandler;
        this.customVertexConsumers = new CustomVertexConsumers();
    }

    public void render(Hud hud, class_332 guiGraphics, float partialTicks) {
        OpenGlHelper.clearErrors();
        class_310 mc = class_310.method_1551();
        int screenWidth = mc.method_22683().method_4486();
        int screenHeight = mc.method_22683().method_4502();
        double screenScale = mc.method_22683().method_4495();
        ModuleRenderContext renderContext = new ModuleRenderContext(screenWidth, screenHeight, screenScale);
        this.pushboxHandler.updateAll(hud.getPushboxManager());
        if (mc.field_1755 == null) {
            for (HudPreset hudPreset : hud.getPresetManager().getPresets()) {
                hudPreset.cancel();
            }
        }
        for (HudModule hudModule : hud.getModuleManager().getModules()) {
            this.renderModule(hudModule, hud, renderContext, guiGraphics, partialTicks);
        }
        this.pushboxHandler.postUpdateAll(hud.getPushboxManager());
    }

    private <MS extends ModuleSession<MS>> void renderModule(HudModule<MS> module, Hud hud, ModuleRenderContext c, class_332 guiGraphics, float partialTicks) {
        MS session = module.getCurrentSession();
        ((ModuleSession)session).prePotentialRender();
        if (!((ModuleSession)session).isActive()) {
            return;
        }
        if (module.getUsedTransform().fromOldSystem) {
            hud.getOldSystemCompatibility().convertTransform(module.getUsedTransform(), (ModuleSession<?>)session, c);
        }
        IModuleRenderer<MS> renderer = module.getRenderer();
        PushboxHandler.State currentPushState = module.getPushState();
        currentPushState.resetForModule((ModuleSession<?>)session, c.screenWidth, c.screenHeight, c.screenScale);
        this.pushboxHandler.applyScreenEdges(currentPushState, c.screenWidth, c.screenHeight, c.screenScale);
        this.pushboxHandler.applyPushboxes(hud.getPushboxManager(), currentPushState, c.screenWidth, c.screenHeight, c.screenScale);
        c.x = currentPushState.x;
        c.y = currentPushState.y;
        c.w = ((ModuleSession)session).getWidth(c.screenScale);
        c.h = ((ModuleSession)session).getHeight(c.screenScale);
        c.flippedVertically = ((ModuleSession)session).shouldFlipVertically(c.screenHeight, c.screenScale);
        c.flippedHorizontally = ((ModuleSession)session).shouldFlipHorizontally(c.screenWidth, c.screenScale);
        renderer.render(session, c, guiGraphics, partialTicks);
    }

    public PushboxHandler getPushboxHandler() {
        return this.pushboxHandler;
    }

    public CustomVertexConsumers getCustomVertexConsumers() {
        return this.customVertexConsumers;
    }
}

