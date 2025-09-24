/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.mods;

import xaero.map.WorldMap;
import xaero.map.mods.SupportAmecs;
import xaero.map.mods.SupportMods;

public class SupportModsFabric
extends SupportMods {
    public static SupportAmecs amecs = null;

    @Override
    public void load() {
        super.load();
        try {
            Class<?> mmClassTest = Class.forName("de.siphalor.amecs.api.KeyModifiers");
            amecs = new SupportAmecs(WorldMap.LOGGER);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public static boolean amecs() {
        return amecs != null;
    }
}

