/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  net.minecraft.class_156
 *  net.minecraft.class_1921
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9799
 */
package xaero.map.graphics;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import xaero.map.graphics.CustomRenderTypes;

public class CustomVertexConsumers {
    private final SortedMap<class_1921, class_9799> builders = (SortedMap)class_156.method_654((Object)new Object2ObjectLinkedOpenHashMap(), map -> {
        CustomVertexConsumers.checkedAddToMap((Object2ObjectLinkedOpenHashMap<class_1921, class_9799>)map, CustomRenderTypes.MAP_COLOR_FILLER, new class_9799(256));
        CustomVertexConsumers.checkedAddToMap((Object2ObjectLinkedOpenHashMap<class_1921, class_9799>)map, CustomRenderTypes.MAP_FRAME, new class_9799(256));
        CustomVertexConsumers.checkedAddToMap((Object2ObjectLinkedOpenHashMap<class_1921, class_9799>)map, CustomRenderTypes.GUI, new class_9799(256));
        CustomVertexConsumers.checkedAddToMap((Object2ObjectLinkedOpenHashMap<class_1921, class_9799>)map, CustomRenderTypes.GUI_PREMULTIPLIED, new class_9799(256));
        CustomVertexConsumers.checkedAddToMap((Object2ObjectLinkedOpenHashMap<class_1921, class_9799>)map, CustomRenderTypes.MAP_COLOR_OVERLAY, new class_9799(256));
        CustomVertexConsumers.checkedAddToMap((Object2ObjectLinkedOpenHashMap<class_1921, class_9799>)map, CustomRenderTypes.MAP, new class_9799(256));
        CustomVertexConsumers.checkedAddToMap((Object2ObjectLinkedOpenHashMap<class_1921, class_9799>)map, CustomRenderTypes.MAP_ELEMENT_TEXT_BG, new class_9799(42));
        CustomVertexConsumers.checkedAddToMap((Object2ObjectLinkedOpenHashMap<class_1921, class_9799>)map, CustomRenderTypes.MAP_BRANCH, new class_9799(42));
    });
    private class_4597.class_4598 renderTypeBuffers = class_4597.method_22992(this.builders, (class_9799)new class_9799(256));

    public class_4597.class_4598 getRenderTypeBuffers() {
        return this.renderTypeBuffers;
    }

    private static void checkedAddToMap(Object2ObjectLinkedOpenHashMap<class_1921, class_9799> map, class_1921 layer, class_9799 bb) {
        if (map.containsKey((Object)layer)) {
            throw new RuntimeException("Duplicate render layers!");
        }
        map.put((Object)layer, (Object)bb);
    }
}

