/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4587
 */
package xaero.map;

import net.minecraft.class_4587;
import xaero.map.graphics.CustomVertexConsumers;
import xaero.map.region.texture.BranchTextureRenderer;

public class WorldMapClientOnly {
    public BranchTextureRenderer branchTextureRenderer;
    public CustomVertexConsumers customVertexConsumers;
    private class_4587 mapScreenPoseStack;

    public void preInit(String modId) {
    }

    public void postInit() {
        this.branchTextureRenderer = new BranchTextureRenderer();
        this.customVertexConsumers = new CustomVertexConsumers();
        this.mapScreenPoseStack = new class_4587();
    }

    public class_4587 getMapScreenPoseStack() {
        return this.mapScreenPoseStack;
    }
}

