/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_332
 */
package xaero.hud.render.module;

import net.minecraft.class_332;
import xaero.hud.module.ModuleSession;
import xaero.hud.render.module.ModuleRenderContext;

public interface IModuleRenderer<MS extends ModuleSession<MS>> {
    public void render(MS var1, ModuleRenderContext var2, class_332 var3, float var4);
}

