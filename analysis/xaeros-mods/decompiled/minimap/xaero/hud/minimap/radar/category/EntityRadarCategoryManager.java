/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.GsonBuilder
 *  javax.annotation.Nonnull
 */
package xaero.hud.minimap.radar.category;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import xaero.common.HudMod;
import xaero.hud.category.rule.resolver.ObjectCategoryRuleResolver;
import xaero.hud.category.serialization.data.ObjectCategoryDataGsonSerializer;
import xaero.hud.category.setting.ObjectCategoryDefaultSettingsSetter;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.EntityRadarCategoryConstants;
import xaero.hud.minimap.radar.category.EntityRadarCategoryFileIO;
import xaero.hud.minimap.radar.category.EntityRadarDefaultCategories;
import xaero.hud.minimap.radar.category.serialization.EntityRadarCategorySerializationHandler;
import xaero.hud.minimap.radar.category.serialization.data.EntityRadarCategoryData;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;

public final class EntityRadarCategoryManager {
    private final Path mainFilePath;
    private final Path secondaryFilePath;
    private EntityRadarCategoryFileIO mainFileIO;
    private EntityRadarCategoryFileIO secondaryFileIO;
    private EntityRadarCategory rootCategory;
    private EntityRadarDefaultCategories defaultCategoryConfigurator;
    private ObjectCategoryRuleResolver ruleResolver;

    private EntityRadarCategoryManager(@Nonnull Path mainFilePath, @Nonnull Path secondaryFilePath) {
        this.mainFilePath = mainFilePath;
        this.secondaryFilePath = secondaryFilePath;
    }

    public void init() throws IOException {
        ObjectCategoryDataGsonSerializer.Builder<EntityRadarCategoryData> dataSerializerBuilder = ObjectCategoryDataGsonSerializer.Builder.begin(new GsonBuilder().setPrettyPrinting().create(), EntityRadarCategoryData.class);
        EntityRadarCategorySerializationHandler.Builder serializationHandlerBuilder = EntityRadarCategorySerializationHandler.Builder.begin(dataSerializerBuilder);
        EntityRadarCategoryFileIO.Builder fileIOBuilder = EntityRadarCategoryFileIO.Builder.begin(serializationHandlerBuilder).setSaveLocationPath(this.mainFilePath);
        this.mainFileIO = fileIOBuilder.build();
        fileIOBuilder = EntityRadarCategoryFileIO.Builder.begin(serializationHandlerBuilder).setSaveLocationPath(this.secondaryFilePath);
        this.secondaryFileIO = fileIOBuilder.build();
        this.defaultCategoryConfigurator = EntityRadarDefaultCategories.Builder.begin().build();
        this.ruleResolver = ObjectCategoryRuleResolver.Builder.begin().build();
        ObjectCategoryDefaultSettingsSetter defaultSettings = ObjectCategoryDefaultSettingsSetter.Builder.begin().setSettings(EntityRadarCategorySettings.SETTINGS).build();
        EntityRadarCategory root = null;
        if (Files.exists(this.mainFilePath, new LinkOption[0])) {
            root = this.mainFileIO.loadRootCategory();
        }
        if (root == null && Files.exists(this.secondaryFilePath, new LinkOption[0])) {
            root = this.secondaryFileIO.loadRootCategory();
        }
        if (root == null) {
            root = this.defaultCategoryConfigurator.setupDefault(HudMod.INSTANCE.getSettings());
        }
        defaultSettings.setDefaultsFor(root, true);
        this.mainFileIO.saveRootCategory(root);
        HudMod.INSTANCE.getSettings().resetEntityRadarBackwardsCompatibilityConfig();
        this.rootCategory = root;
    }

    public ObjectCategoryRuleResolver getRuleResolver() {
        return this.ruleResolver;
    }

    public EntityRadarCategory getRootCategory() {
        return this.rootCategory;
    }

    public EntityRadarDefaultCategories getDefaultCategoryConfigurator() {
        return this.defaultCategoryConfigurator;
    }

    public void setRootCategory(EntityRadarCategory rootCategory) {
        this.rootCategory = rootCategory;
    }

    public Path getSecondaryFilePath() {
        return this.secondaryFilePath;
    }

    public EntityRadarCategoryFileIO getSecondaryFileIO() {
        return this.secondaryFileIO;
    }

    public void save() {
        this.mainFileIO.saveRootCategory(this.rootCategory);
    }

    public static final class Builder {
        private Path mainFilePath;
        private Path secondaryFilePath;

        private Builder() {
        }

        public Builder setMainFilePath(Path mainFilePath) {
            this.mainFilePath = mainFilePath;
            return this;
        }

        public Builder setSecondaryFilePath(Path secondaryFilePath) {
            this.secondaryFilePath = secondaryFilePath;
            return this;
        }

        public Builder setDefault() {
            this.setMainFilePath(EntityRadarCategoryConstants.CONFIG_PATH);
            this.setSecondaryFilePath(EntityRadarCategoryConstants.DEFAULT_CONFIG_PATH);
            return this;
        }

        public EntityRadarCategoryManager build() {
            if (this.mainFilePath == null || this.secondaryFilePath == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return new EntityRadarCategoryManager(this.mainFilePath, this.secondaryFilePath);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

