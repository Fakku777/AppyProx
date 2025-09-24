/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package xaero.hud.category.setting;

import java.util.Map;
import javax.annotation.Nonnull;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.setting.ObjectCategorySetting;

public final class ObjectCategoryDefaultSettingsSetter {
    private final Map<String, ObjectCategorySetting<?>> settings;

    private ObjectCategoryDefaultSettingsSetter(@Nonnull Map<String, ObjectCategorySetting<?>> settings) {
        this.settings = settings;
    }

    public void setDefaultsFor(ObjectCategory<?, ?> category, boolean onlyNew) {
        this.settings.forEach((k, setting) -> {
            if (onlyNew && category.getSettingValue(setting) != null) {
                return;
            }
            this.setForSetting(category, (ObjectCategorySetting)setting);
        });
    }

    private <T> void setForSetting(ObjectCategory<?, ?> category, ObjectCategorySetting<T> setting) {
        category.setSettingValue(setting, setting.getDefaultValue());
    }

    public static final class Builder {
        private Map<String, ObjectCategorySetting<?>> settings;

        private Builder() {
        }

        public Builder setDefault() {
            this.setSettings(null);
            return this;
        }

        public Builder setSettings(Map<String, ObjectCategorySetting<?>> settings) {
            this.settings = settings;
            return this;
        }

        public ObjectCategoryDefaultSettingsSetter build() {
            if (this.settings == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return new ObjectCategoryDefaultSettingsSetter(this.settings);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

