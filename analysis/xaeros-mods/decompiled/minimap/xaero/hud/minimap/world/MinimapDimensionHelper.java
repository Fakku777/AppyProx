/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_5321
 *  net.minecraft.class_746
 *  net.minecraft.class_7924
 */
package xaero.hud.minimap.world;

import java.util.Set;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_746;
import net.minecraft.class_7924;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;

public class MinimapDimensionHelper {
    public double getDimensionDivision(MinimapWorld minimapWorld) {
        if (class_310.method_1551().field_1687 == null) {
            return 1.0;
        }
        double dimCoordinateScale = this.getDimCoordinateScale(minimapWorld);
        return class_310.method_1551().field_1687.method_8597().comp_646() / dimCoordinateScale;
    }

    public double getDimCoordinateScale(MinimapWorld minimapWorld) {
        if (minimapWorld == null) {
            return 1.0;
        }
        MinimapWorldRootContainer rootContainer = minimapWorld.getContainer().getRoot();
        class_5321<class_1937> dimKey = minimapWorld.getDimId();
        if (dimKey == null) {
            return 1.0;
        }
        return rootContainer.getDimensionScale(dimKey);
    }

    public String getDimensionDirectoryName(class_5321<class_1937> dimKey) {
        if (dimKey == class_1937.field_25179) {
            return "dim%0";
        }
        if (dimKey == class_1937.field_25180) {
            return "dim%-1";
        }
        if (dimKey == class_1937.field_25181) {
            return "dim%1";
        }
        class_2960 identifier = dimKey.method_29177();
        return "dim%" + identifier.method_12836() + "$" + identifier.method_12832().replace('/', '%');
    }

    public class_5321<class_1937> findDimensionKeyForOldName(class_746 player, String oldName) {
        Set allDimensions = player.field_3944.method_29356();
        for (class_5321 dk : allDimensions) {
            if (!oldName.equals(dk.method_29177().method_12832().replaceAll("[^a-zA-Z0-9_]+", ""))) continue;
            return dk;
        }
        return null;
    }

    public class_5321<class_1937> getDimensionKeyForDirectoryName(String dirName) {
        String dimIdPart = dirName.substring(4);
        if (dimIdPart.equals("0")) {
            return class_1937.field_25179;
        }
        if (dimIdPart.equals("1")) {
            return class_1937.field_25181;
        }
        if (dimIdPart.equals("-1")) {
            return class_1937.field_25180;
        }
        String[] idArgs = dimIdPart.split("\\$");
        if (idArgs.length < 2) {
            return null;
        }
        return class_5321.method_29179((class_5321)class_7924.field_41223, (class_2960)class_2960.method_60655((String)idArgs[0], (String)idArgs[1].replace('%', '/')));
    }
}

