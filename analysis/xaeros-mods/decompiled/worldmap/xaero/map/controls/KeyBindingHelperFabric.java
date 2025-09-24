/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.class_304
 *  net.minecraft.class_3675$class_306
 */
package xaero.map.controls;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import xaero.map.controls.IKeyBindingHelper;
import xaero.map.mods.SupportModsFabric;

public class KeyBindingHelperFabric
implements IKeyBindingHelper {
    @Override
    public class_3675.class_306 getBoundKeyOf(class_304 kb) {
        return KeyBindingHelper.getBoundKeyOf((class_304)kb);
    }

    @Override
    public boolean modifiersAreActive(class_304 kb, int keyConflictContext) {
        return !SupportModsFabric.amecs() || SupportModsFabric.amecs.modifiersArePressed(kb);
    }
}

