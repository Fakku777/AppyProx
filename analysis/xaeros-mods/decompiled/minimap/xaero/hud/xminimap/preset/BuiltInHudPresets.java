/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 */
package xaero.hud.xminimap.preset;

import net.minecraft.class_2561;
import net.minecraft.class_2960;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.preset.HudPreset;
import xaero.hud.preset.HudPresetManager;
import xaero.hud.preset.ModulePreset;

public class BuiltInHudPresets {
    public static final HudPreset TOP_LEFT = HudPreset.Builder.begin().setId(class_2960.method_60655((String)"xaerominimap", (String)"top_left")).setName((class_2561)class_2561.method_43471((String)"gui.xaero_preset_topleft")).addModulePreset(ModulePreset.Builder.begin(BuiltInHudModules.MINIMAP).setPlacement(0, 0, false, false, false, false, false).build()).build();
    public static final HudPreset TOP_RIGHT = HudPreset.Builder.begin().setId(class_2960.method_60655((String)"xaerominimap", (String)"top_right")).setName((class_2561)class_2561.method_43471((String)"gui.xaero_preset_topright")).addModulePreset(ModulePreset.Builder.begin(BuiltInHudModules.MINIMAP).setPlacement(0, 0, false, true, false, false, false).build()).build();
    public static final HudPreset BOTTOM_LEFT = HudPreset.Builder.begin().setId(class_2960.method_60655((String)"xaerominimap", (String)"bottom_left")).setName((class_2561)class_2561.method_43471((String)"gui.xaero_preset_bottom_left")).addModulePreset(ModulePreset.Builder.begin(BuiltInHudModules.MINIMAP).setPlacement(0, 0, false, false, true, false, false).build()).build();
    public static final HudPreset BOTTOM_RIGHT = HudPreset.Builder.begin().setId(class_2960.method_60655((String)"xaerominimap", (String)"bottom_right")).setName((class_2561)class_2561.method_43471((String)"gui.xaero_preset_bottom_right")).addModulePreset(ModulePreset.Builder.begin(BuiltInHudModules.MINIMAP).setPlacement(0, 0, false, true, true, false, false).build()).build();

    public static void addAll(HudPresetManager manager) {
        manager.register(TOP_LEFT);
        manager.register(TOP_RIGHT);
        manager.register(BOTTOM_LEFT);
        manager.register(BOTTOM_RIGHT);
    }
}

