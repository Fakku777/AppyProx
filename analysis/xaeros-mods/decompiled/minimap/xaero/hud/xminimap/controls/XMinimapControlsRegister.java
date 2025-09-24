/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_304
 */
package xaero.hud.xminimap.controls;

import java.util.function.Consumer;
import net.minecraft.class_304;
import xaero.hud.controls.ControlsRegister;
import xaero.hud.xminimap.controls.key.XMinimapKeyMappings;

public class XMinimapControlsRegister
extends ControlsRegister {
    @Override
    public void registerKeybindings(Consumer<class_304> registry) {
        XMinimapKeyMappings.registerAll(this.keyMappingControllers, registry);
        super.registerKeybindings(registry);
    }
}

