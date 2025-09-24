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
package xaero.common.minimap.write;

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
import xaero.common.IXaeroMinimap;
import xaero.common.cache.BlockStateShortShapeCache;
import xaero.common.minimap.highlight.HighlighterRegistry;
import xaero.common.minimap.write.MinimapWriter;
import xaero.hud.minimap.module.MinimapSession;

public class MinimapWriterFabric
extends MinimapWriter {
    public MinimapWriterFabric(IXaeroMinimap modMain, MinimapSession minimapSession, BlockStateShortShapeCache blockStateShortShapeCache, HighlighterRegistry highlighterRegistry) {
        super(modMain, minimapSession, blockStateShortShapeCache, highlighterRegistry);
    }

    @Override
    protected boolean blockStateHasTranslucentRenderType(class_2680 blockState) {
        return class_4696.method_23679((class_2680)blockState) == class_11515.field_60926;
    }

    @Override
    protected int getBlockStateLightEmission(class_2680 state, class_1937 world, class_2338 pos) {
        return state.method_26213();
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

