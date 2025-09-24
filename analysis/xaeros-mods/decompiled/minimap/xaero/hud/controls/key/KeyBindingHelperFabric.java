/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.class_304
 *  net.minecraft.class_3675$class_306
 */
package xaero.hud.controls.key;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import xaero.common.HudMod;
import xaero.common.mods.SupportModsFabric;
import xaero.hud.controls.key.IKeyBindingHelper;

public class KeyBindingHelperFabric
implements IKeyBindingHelper {
    @Override
    public class_3675.class_306 getBoundKeyOf(class_304 kb) {
        return KeyBindingHelper.getBoundKeyOf((class_304)kb);
    }

    @Override
    public boolean modifiersAreActive(class_304 kb, int keyConflictContext) {
        return !((SupportModsFabric)HudMod.INSTANCE.getSupportMods()).amecs() || ((SupportModsFabric)HudMod.INSTANCE.getSupportMods()).amecs.modifiersArePressed(kb);
    }
}

