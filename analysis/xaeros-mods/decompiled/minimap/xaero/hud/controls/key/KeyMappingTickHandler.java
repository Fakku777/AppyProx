/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_304
 */
package xaero.hud.controls.key;

import net.minecraft.class_304;
import xaero.hud.controls.key.KeyMappingController;
import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.controls.util.ControlsUtil;

public class KeyMappingTickHandler {
    public static boolean DISABLE_KEY_MAPPING_OVERRIDES;
    private final KeyMappingControllerManager controllerManager;

    public KeyMappingTickHandler(KeyMappingControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public void tick() {
        for (KeyMappingController controller : this.controllerManager) {
            this.handleExtraPresses(controller);
        }
        for (KeyMappingController controller : this.controllerManager) {
            this.handleRelease(controller);
        }
        for (KeyMappingController controller : this.controllerManager) {
            this.handlePress(controller);
        }
    }

    private void handlePress(KeyMappingController controller) {
        boolean startingPress = false;
        if (!controller.isPressed()) {
            boolean updatedPressedValue = this.getUpdatedPressedValue(controller);
            if (!updatedPressedValue) {
                return;
            }
            startingPress = true;
            controller.setPressed(true);
        }
        this.handlePressFunctions(controller, startingPress);
    }

    private void handleExtraPresses(KeyMappingController controller) {
        if (!controller.isPressed()) {
            return;
        }
        while (controller.isXaeroKey() && controller.getKeyMapping().method_1436()) {
            for (KeyMappingFunction func : controller) {
                if (func.isHeld()) continue;
                func.onRelease();
            }
            for (KeyMappingFunction func : controller) {
                if (func.isHeld()) continue;
                func.onPress();
            }
        }
    }

    private void handleRelease(KeyMappingController controller) {
        if (!controller.isPressed()) {
            return;
        }
        if (this.isPhysicallyDown(controller)) {
            return;
        }
        this.handleReleaseFunctions(controller);
        controller.setPressed(false);
    }

    private void handlePressFunctions(KeyMappingController controller, boolean startingPress) {
        for (KeyMappingFunction func : controller) {
            if (!startingPress && !func.isHeld()) continue;
            func.onPress();
        }
    }

    private void handleReleaseFunctions(KeyMappingController controller) {
        for (KeyMappingFunction func : controller) {
            func.onRelease();
        }
    }

    private boolean getUpdatedPressedValue(KeyMappingController controller) {
        class_304 keyMapping = controller.getKeyMapping();
        if (!controller.isXaeroKey()) {
            return this.isPhysicallyDown(controller);
        }
        boolean result = keyMapping.method_1436();
        if (!result) {
            return false;
        }
        while (keyMapping.method_1436()) {
            this.handlePressFunctions(controller, true);
            this.handleReleaseFunctions(controller);
        }
        return true;
    }

    private boolean isPhysicallyDown(KeyMappingController controller) {
        class_304 keyMapping = controller.getKeyMapping();
        if (!controller.isXaeroKey()) {
            DISABLE_KEY_MAPPING_OVERRIDES = true;
            boolean down = keyMapping.method_1434();
            DISABLE_KEY_MAPPING_OVERRIDES = false;
            return down;
        }
        return ControlsUtil.isPhysicallyDown(keyMapping);
    }
}

