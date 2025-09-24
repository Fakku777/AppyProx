/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 *  net.minecraft.class_3675$class_306
 *  net.minecraft.class_3675$class_307
 *  org.lwjgl.glfw.GLFW
 */
package xaero.hud.controls.util;

import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import org.lwjgl.glfw.GLFW;
import xaero.common.platform.Services;
import xaero.hud.controls.key.IKeyBindingHelper;

public class ControlsUtil {
    public static void setKeyState(class_304 keyMapping, boolean pressed) {
        class_304.method_1416((class_3675.class_306)Services.PLATFORM.getKeyBindingHelper().getBoundKeyOf(keyMapping), (boolean)pressed);
    }

    public static boolean isPhysicallyDown(class_304 keyMapping) {
        IKeyBindingHelper keyBindingHelper = Services.PLATFORM.getKeyBindingHelper();
        if (keyBindingHelper.getBoundKeyOf(keyMapping).method_1444() == -1) {
            return false;
        }
        if (keyBindingHelper.getBoundKeyOf(keyMapping).method_1442() == class_3675.class_307.field_1672) {
            return GLFW.glfwGetMouseButton((long)class_310.method_1551().method_22683().method_4490(), (int)keyBindingHelper.getBoundKeyOf(keyMapping).method_1444()) == 1;
        }
        if (keyBindingHelper.getBoundKeyOf(keyMapping).method_1442() == class_3675.class_307.field_1668) {
            return class_3675.method_15987((long)class_310.method_1551().method_22683().method_4490(), (int)keyBindingHelper.getBoundKeyOf(keyMapping).method_1444());
        }
        return false;
    }
}

