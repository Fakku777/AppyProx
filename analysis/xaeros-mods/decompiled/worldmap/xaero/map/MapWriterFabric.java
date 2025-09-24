/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1058
 *  net.minecraft.class_1087
 *  net.minecraft.class_10889
 *  net.minecraft.class_11515
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2680
 *  net.minecraft.class_4696
 *  net.minecraft.class_773
 *  net.minecraft.class_777
 */
package xaero.map;

import java.util.List;
import net.minecraft.class_1058;
import net.minecraft.class_1087;
import net.minecraft.class_10889;
import net.minecraft.class_11515;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4696;
import net.minecraft.class_773;
import net.minecraft.class_777;
import xaero.map.MapWriter;
import xaero.map.biome.BiomeGetter;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.region.OverlayManager;

public class MapWriterFabric
extends MapWriter {
    public MapWriterFabric(OverlayManager overlayManager, BlockStateShortShapeCache blockStateShortShapeCache, BiomeGetter biomeGetter) {
        super(overlayManager, blockStateShortShapeCache, biomeGetter);
    }

    @Override
    protected boolean blockStateHasTranslucentRenderType(class_2680 blockState) {
        return class_4696.method_23679((class_2680)blockState) == class_11515.field_60926;
    }

    @Override
    protected List<class_777> getQuads(class_1087 model, class_1937 level, class_2338 pos, class_2680 state, class_2350 direction) {
        this.reusableBlockModelPartList.clear();
        model.method_68513(this.usedRandom, this.reusableBlockModelPartList);
        if (this.reusableBlockModelPartList.isEmpty()) {
            return null;
        }
        return ((class_10889)this.reusableBlockModelPartList.getFirst()).method_68509(direction);
    }

    @Override
    protected class_1058 getParticleIcon(class_773 bms, class_1087 model, class_1937 level, class_2338 pos, class_2680 state) {
        return bms.method_3339(state);
    }
}

