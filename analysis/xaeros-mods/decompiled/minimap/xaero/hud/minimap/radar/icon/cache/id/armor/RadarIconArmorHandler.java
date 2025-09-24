/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1498
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_3489
 *  net.minecraft.class_8053
 *  net.minecraft.class_8054
 *  net.minecraft.class_8056
 *  net.minecraft.class_9334
 */
package xaero.hud.minimap.radar.icon.cache.id.armor;

import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1498;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_3489;
import net.minecraft.class_8053;
import net.minecraft.class_8054;
import net.minecraft.class_8056;
import net.minecraft.class_9334;
import xaero.hud.minimap.radar.icon.cache.id.armor.RadarIconArmor;

public class RadarIconArmorHandler {
    public RadarIconArmor getArmor(class_1309 livingEntity) {
        class_1304 relevantArmourSlot = livingEntity instanceof class_1498 ? class_1304.field_6174 : class_1304.field_6169;
        class_1799 armorItemStack = livingEntity.method_6118(relevantArmourSlot);
        if (armorItemStack == null || armorItemStack == class_1799.field_8037) {
            return null;
        }
        class_1792 armorItem = armorItemStack.method_7909();
        if (!armorItemStack.method_31573(class_3489.field_41890)) {
            return new RadarIconArmor(armorItem, null, null);
        }
        if (!armorItemStack.method_57826(class_9334.field_49607)) {
            return new RadarIconArmor(armorItem, null, null);
        }
        class_8053 trim = (class_8053)armorItemStack.method_58694(class_9334.field_49607);
        class_8054 trimMaterial = (class_8054)trim.comp_3179().comp_349();
        class_8056 trimPattern = (class_8056)trim.comp_3180().comp_349();
        return new RadarIconArmor(armorItem, trimMaterial, trimPattern);
    }
}

