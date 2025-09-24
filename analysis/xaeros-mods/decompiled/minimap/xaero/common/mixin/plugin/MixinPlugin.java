/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.objectweb.asm.tree.ClassNode
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 */
package xaero.common.mixin.plugin;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import xaero.common.platform.Services;

public class MixinPlugin
implements IMixinConfigPlugin {
    private static final Map<String, String> MIXIN_MOD_ID_MAP = ImmutableMap.of((Object)"xaero.common.mixin.MixinBatchableBufferSource", (Object)"immediatelyfast", (Object)"xaero.common.mixin.MixinWorldMapCompositeRenderType", (Object)"xaeroworldmap");

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String modId = MIXIN_MOD_ID_MAP.get(mixinClassName);
        if (modId == null) {
            return true;
        }
        return Services.PLATFORM.checkModForMixin(modId);
    }

    public void onLoad(String mixinPackage) {
    }

    public String getRefMapperConfig() {
        return null;
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public List<String> getMixins() {
        return null;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

