/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1291
 *  net.minecraft.class_6880
 */
package xaero.map.effects;

import java.util.function.Function;
import net.minecraft.class_1291;
import net.minecraft.class_6880;
import xaero.map.effects.Effects;
import xaero.map.effects.WorldMapStatusEffect;

public class EffectsRegister {
    public void registerEffects(Function<WorldMapStatusEffect, class_6880<class_1291>> registry) {
        Effects.init();
        Effects.NO_WORLD_MAP = registry.apply(Effects.NO_WORLD_MAP_UNHELD);
        Effects.NO_WORLD_MAP_HARMFUL = registry.apply(Effects.NO_WORLD_MAP_HARMFUL_UNHELD);
        Effects.NO_CAVE_MAPS = registry.apply(Effects.NO_CAVE_MAPS_UNHELD);
        Effects.NO_CAVE_MAPS_HARMFUL = registry.apply(Effects.NO_CAVE_MAPS_HARMFUL_UNHELD);
    }
}

