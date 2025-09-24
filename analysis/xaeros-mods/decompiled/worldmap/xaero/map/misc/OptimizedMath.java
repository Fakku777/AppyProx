/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package xaero.map.misc;

import net.minecraft.class_4587;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class OptimizedMath {
    public static final Vector3f XP = new Vector3f(1.0f, 0.0f, 0.0f);
    public static final Vector3f YP = new Vector3f(0.0f, 1.0f, 0.0f);
    public static final Vector3f ZP = new Vector3f(0.0f, 0.0f, 1.0f);

    public static int myFloor(double d) {
        int asInt = (int)d;
        if ((double)asInt != d && d < 0.0) {
            --asInt;
        }
        return asInt;
    }

    public static void rotatePose(class_4587 poseStack, float degrees, Vector3fc vector) {
        class_4587.class_4665 pose = poseStack.method_23760();
        pose.method_23761().rotate(degrees * ((float)Math.PI / 180), vector);
        pose.method_23762().rotate(degrees * ((float)Math.PI / 180), vector);
    }
}

