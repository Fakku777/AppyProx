/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_634
 *  org.apache.commons.lang3.function.TriFunction
 */
package xaero.hud.module;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_634;
import org.apache.commons.lang3.function.TriFunction;
import xaero.common.HudMod;
import xaero.hud.HudSession;
import xaero.hud.module.ModuleSession;
import xaero.hud.module.ModuleTransform;
import xaero.hud.pushbox.PushboxHandler;
import xaero.hud.render.module.IModuleRenderer;

public final class HudModule<MS extends ModuleSession<MS>> {
    private final class_2960 id;
    private final class_2561 displayName;
    private final TriFunction<HudMod, HudModule<MS>, class_634, MS> sessionFactory;
    private final Supplier<IModuleRenderer<MS>> rendererFactory;
    private final Function<class_437, class_437> configScreenFactory;
    private IModuleRenderer<MS> renderer;
    private ModuleTransform transform;
    private ModuleTransform unconfirmedTransform;
    private PushboxHandler.State pushState;
    private boolean active;

    public HudModule(class_2960 id, class_2561 displayName, TriFunction<HudMod, HudModule<MS>, class_634, MS> sessionFactory, Supplier<IModuleRenderer<MS>> rendererFactory, Function<class_437, class_437> configScreenFactory) {
        this.displayName = displayName;
        this.active = true;
        this.id = id;
        this.sessionFactory = sessionFactory;
        this.rendererFactory = rendererFactory;
        this.configScreenFactory = configScreenFactory;
        this.transform = new ModuleTransform();
        this.pushState = new PushboxHandler.State();
    }

    public class_2960 getId() {
        return this.id;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MS getCurrentSession() {
        HudSession hudSession = HudSession.getCurrentSession();
        if (hudSession == null) {
            return null;
        }
        return hudSession.getSession(this);
    }

    public IModuleRenderer<MS> getRenderer() {
        if (this.renderer == null) {
            this.renderer = this.rendererFactory.get();
        }
        return this.renderer;
    }

    public ModuleTransform getUsedTransform() {
        if (class_310.method_1551().field_1755 != null) {
            return this.getUnconfirmedTransform();
        }
        if (this.unconfirmedTransform != null) {
            this.cancelTransform();
        }
        return this.transform;
    }

    public ModuleTransform getUnconfirmedTransform() {
        if (this.unconfirmedTransform == null) {
            this.unconfirmedTransform = this.transform.copy();
        }
        return this.unconfirmedTransform;
    }

    public void confirmTransform() {
        if (this.unconfirmedTransform == null) {
            return;
        }
        this.transform = this.unconfirmedTransform;
        this.unconfirmedTransform = null;
    }

    public ModuleTransform getConfirmedTransform() {
        return this.transform;
    }

    public void setTransform(ModuleTransform transform) {
        this.transform = transform;
        this.unconfirmedTransform = null;
    }

    public void cancelTransform() {
        this.unconfirmedTransform = null;
    }

    public PushboxHandler.State getPushState() {
        return this.pushState;
    }

    public class_2561 getDisplayName() {
        return this.displayName;
    }

    public Function<class_437, class_437> getConfigScreenFactory() {
        return this.configScreenFactory;
    }

    TriFunction<HudMod, HudModule<MS>, class_634, MS> getSessionFactory() {
        return this.sessionFactory;
    }

    void setRenderer(IModuleRenderer<MS> renderer) {
        this.renderer = renderer;
    }
}

