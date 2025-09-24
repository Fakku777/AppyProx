/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_339
 */
package xaero.common.settings;

import net.minecraft.class_2561;
import net.minecraft.class_339;
import xaero.common.settings.ModOptions;

public abstract class Option {
    protected final ModOptions option;
    private final class_2561 caption;

    public Option(ModOptions option) {
        this.option = option;
        this.caption = class_2561.method_43471((String)option.getEnumStringRaw());
    }

    public class_2561 getCaption() {
        return this.caption;
    }

    public abstract class_339 createButton(int var1, int var2, int var3);
}

