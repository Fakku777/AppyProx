/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  net.minecraft.class_746
 */
package xaero.hud;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;
import xaero.common.HudMod;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.core.XaeroMinimapCore;
import xaero.hud.controls.key.KeyMappingTickHandler;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.module.HudModule;
import xaero.hud.module.ModuleSession;

public class HudSession {
    protected final HudMod modMain;
    protected KeyMappingTickHandler keyMappingTickHandler;
    private final Map<HudModule<?>, ModuleSession<?>> moduleSessions;
    private HudModule<?> lastModuleSessionRequest;
    private ModuleSession<?> lastModuleSessionPassed;
    protected boolean usable;

    public HudSession(HudMod modMain) {
        this.modMain = modMain;
        this.moduleSessions = new HashMap();
    }

    public <MS extends ModuleSession<MS>> MS getSession(HudModule<MS> module) {
        if (module == this.lastModuleSessionRequest) {
            return (MS)this.lastModuleSessionPassed;
        }
        ModuleSession<?> mappedSession = this.moduleSessions.get(module);
        this.lastModuleSessionRequest = module;
        this.lastModuleSessionPassed = mappedSession;
        return (MS)mappedSession;
    }

    public void init(class_634 connection) throws IOException {
        this.lastModuleSessionRequest = null;
        this.lastModuleSessionPassed = null;
        this.keyMappingTickHandler = new KeyMappingTickHandler(this.modMain.getKeyMappingControllers());
        this.modMain.getHud().getSessionHandler().resetSessions(this.modMain, connection, this.moduleSessions::put);
        this.usable = true;
        MinimapLogs.LOGGER.info("New Xaero hud session initialized!");
    }

    public final void tryCleanup() {
        try {
            this.cleanup();
            MinimapLogs.LOGGER.info("Xaero hud session finalized.");
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("Xaero hud session failed to finalize properly.", t);
        }
        this.moduleSessions.clear();
        this.usable = false;
    }

    protected void cleanup() {
        this.lastModuleSessionRequest = null;
        this.lastModuleSessionPassed = null;
        this.modMain.getHud().getSessionHandler().closeSessions(this.modMain);
    }

    public static HudSession getCurrentSession() {
        HudSession session = HudSession.getForPlayer(class_310.method_1551().field_1724);
        if (session == null && XaeroMinimapCore.currentSession != null && XaeroMinimapCore.currentSession.usable) {
            session = XaeroMinimapCore.currentSession;
        }
        return session;
    }

    public static HudSession getForPlayer(class_746 player) {
        if (player == null || player.field_3944 == null) {
            return null;
        }
        return ((IXaeroMinimapClientPlayNetHandler)player.field_3944).getXaero_minimapSession();
    }

    public KeyMappingTickHandler getKeyMappingTickHandler() {
        return this.keyMappingTickHandler;
    }

    public HudMod getHudMod() {
        return this.modMain;
    }
}

