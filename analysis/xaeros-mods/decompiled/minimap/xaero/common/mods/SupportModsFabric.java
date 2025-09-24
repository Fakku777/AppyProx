/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mods;

import xaero.common.IXaeroMinimap;
import xaero.common.mods.SupportAmecs;
import xaero.common.mods.SupportMods;
import xaero.hud.minimap.MinimapLogs;

public class SupportModsFabric
extends SupportMods {
    public SupportAmecs amecs = null;

    public SupportModsFabric(IXaeroMinimap modMain) {
        super(modMain);
        try {
            Class<?> mmClassTest = Class.forName("de.siphalor.amecs.api.KeyModifiers");
            this.amecs = new SupportAmecs(MinimapLogs.LOGGER);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public boolean amecs() {
        return this.amecs != null;
    }
}

