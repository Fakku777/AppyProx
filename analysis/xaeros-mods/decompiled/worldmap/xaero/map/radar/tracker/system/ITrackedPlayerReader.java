/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_5321
 */
package xaero.map.radar.tracker.system;

import java.util.UUID;
import net.minecraft.class_1937;
import net.minecraft.class_5321;

public interface ITrackedPlayerReader<P> {
    public UUID getId(P var1);

    public double getX(P var1);

    public double getY(P var1);

    public double getZ(P var1);

    public class_5321<class_1937> getDimension(P var1);
}

