/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_304
 */
package xaero.hud.xminimap.controls.key;

import java.util.function.Consumer;
import net.minecraft.class_304;
import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.xminimap.controls.key.function.XMinimapKeyMappingFunctions;

public class XMinimapKeyMappings {
    public static class_304 SETTINGS = new class_304("gui.xaero_minimap_settings", 89, "Xaero's Minimap");

    public static void registerAll(KeyMappingControllerManager controllerManager, Consumer<class_304> registry) {
        controllerManager.registerController(SETTINGS, true, registry);
        XMinimapKeyMappingFunctions.registerAll(controllerManager);
    }
}

