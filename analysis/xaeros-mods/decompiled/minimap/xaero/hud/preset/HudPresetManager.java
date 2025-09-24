/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 */
package xaero.hud.preset;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.class_2960;
import xaero.hud.preset.HudPreset;

public class HudPresetManager {
    private final Map<class_2960, HudPreset> presets = new LinkedHashMap<class_2960, HudPreset>();

    public void register(HudPreset preset) {
        this.presets.put(preset.getId(), preset);
    }

    public Iterable<HudPreset> getPresets() {
        return this.presets.values();
    }
}

