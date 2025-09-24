/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.siphalor.amecs.api.KeyBindingUtils
 *  de.siphalor.amecs.api.KeyModifiers
 *  net.minecraft.class_304
 *  org.apache.logging.log4j.Logger
 */
package xaero.common.mods;

import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifiers;
import net.minecraft.class_304;
import org.apache.logging.log4j.Logger;

public class SupportAmecs {
    public SupportAmecs(Logger logger) {
    }

    public boolean modifiersArePressed(class_304 keyBinding) {
        KeyModifiers modifiers = KeyBindingUtils.getBoundModifiers((class_304)keyBinding);
        return KeyModifiers.getCurrentlyPressed().contains(modifiers);
    }
}

