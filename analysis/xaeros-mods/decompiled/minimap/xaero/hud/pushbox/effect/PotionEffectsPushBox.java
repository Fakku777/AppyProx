/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1291
 *  net.minecraft.class_1293
 *  net.minecraft.class_310
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 */
package xaero.hud.pushbox.effect;

import java.util.Collection;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_310;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import xaero.hud.pushbox.PushBox;
import xaero.hud.pushbox.effect.IPotionEffectsPushBox;

public class PotionEffectsPushBox
extends PushBox
implements IPotionEffectsPushBox {
    private boolean hasNegative;

    public PotionEffectsPushBox() {
        super(0, 0, 0, 0, 1.0f, 0.0f, 53);
    }

    @Override
    public int getX(int width, int height) {
        return super.getX(width, height) - this.getW(width, height);
    }

    @Override
    public void update() {
        super.update();
        this.hasNegative = false;
        this.w = PotionEffectsPushBox.calculatePotionDisplayWidth(this);
        this.h = this.hasNegative ? 53 : 27;
    }

    @Override
    public void postUpdate() {
        super.postUpdate();
        this.active = false;
    }

    protected static int calculatePotionDisplayWidth(IPotionEffectsPushBox potionEffectBox) {
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        Collection collection = player.method_6026();
        if (collection == null || collection.isEmpty()) {
            return 0;
        }
        int positiveCount = 0;
        int negativeCount = 0;
        for (class_1293 effectInstance : collection) {
            class_6880 effect;
            if (!effectInstance.method_5592() || (effect = effectInstance.method_5579()) == null) continue;
            if (!((class_1291)effect.comp_349()).method_5573()) {
                potionEffectBox.setHasNegative(true);
                ++negativeCount;
                continue;
            }
            ++positiveCount;
        }
        if (positiveCount + negativeCount == 0) {
            return 0;
        }
        return Math.max(positiveCount, negativeCount) * 25 + 1;
    }

    @Override
    public void setHasNegative(boolean b) {
        this.hasNegative = b;
    }
}

