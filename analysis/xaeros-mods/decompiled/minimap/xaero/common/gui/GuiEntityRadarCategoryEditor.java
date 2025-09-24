/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.common.IXaeroMinimap;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.MinimapProcessor;
import xaero.hud.category.setting.ObjectCategoryDefaultSettingsSetter;
import xaero.hud.category.ui.EditorCategoryNodeConverter;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.minimap.radar.RadarSession;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.category.ui.EditorEntityRadarCategoryNodeConverter;
import xaero.hud.minimap.radar.category.ui.node.EditorEntityRadarCategoryNode;
import xaero.hud.minimap.radar.category.ui.node.EditorEntityRadarCategorySettingsNode;

public class GuiEntityRadarCategoryEditor
extends GuiCategoryEditor<EntityRadarCategory, EditorEntityRadarCategoryNode, EntityRadarCategory.Builder, EditorEntityRadarCategorySettingsNode<?>, EditorEntityRadarCategorySettingsNode.Builder, EditorEntityRadarCategoryNode.Builder> {
    private final EntityRadarCategoryManager entityRadarCategoryManager;

    protected GuiEntityRadarCategoryEditor(IXaeroMinimap modMain, class_437 parent, class_437 escape) {
        super(modMain, parent, escape, (class_2561)class_2561.method_43471((String)"gui.xaero_entity_radar_categories"), EditorEntityRadarCategoryNodeConverter.Builder.begin().build());
        this.entityRadarCategoryManager = modMain.getEntityRadarCategoryManager();
    }

    @Override
    protected EditorEntityRadarCategoryNode constructEditorData(EditorCategoryNodeConverter<EntityRadarCategory, EditorEntityRadarCategoryNode, EntityRadarCategory.Builder, EditorEntityRadarCategorySettingsNode<?>, EditorEntityRadarCategorySettingsNode.Builder, EditorEntityRadarCategoryNode.Builder> dataConverter) {
        EntityRadarCategory rootCategory = this.modMain.getEntityRadarCategoryManager().getRootCategory();
        return dataConverter.convert(rootCategory, true);
    }

    @Override
    protected EditorEntityRadarCategoryNode constructDefaultData(EditorCategoryNodeConverter<EntityRadarCategory, EditorEntityRadarCategoryNode, EntityRadarCategory.Builder, EditorEntityRadarCategorySettingsNode<?>, EditorEntityRadarCategorySettingsNode.Builder, EditorEntityRadarCategoryNode.Builder> dataConverter) {
        EntityRadarCategory rootCategory;
        this.modMain.getSettings().resetEntityRadarBackwardsCompatibilityConfig();
        EntityRadarCategoryManager manager = this.modMain.getEntityRadarCategoryManager();
        if (Files.exists(manager.getSecondaryFilePath(), new LinkOption[0])) {
            try {
                rootCategory = manager.getSecondaryFileIO().loadRootCategory();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            rootCategory = manager.getDefaultCategoryConfigurator().setupDefault(this.modMain.getSettings());
        }
        ObjectCategoryDefaultSettingsSetter defaultSettings = ObjectCategoryDefaultSettingsSetter.Builder.begin().setSettings(EntityRadarCategorySettings.SETTINGS).build();
        defaultSettings.setDefaultsFor(rootCategory, true);
        return dataConverter.convert(rootCategory, true);
    }

    @Override
    protected void onConfigConfirmed(EntityRadarCategory confirmedRootCategory) {
        this.entityRadarCategoryManager.setRootCategory(confirmedRootCategory);
        this.modMain.getMinimap().getMinimapFBORenderer().resetEntityIcons();
        this.entityRadarCategoryManager.save();
        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) {
            return;
        }
        MinimapProcessor minimapProcessor = minimapSession.getMinimapProcessor();
        RadarSession radar = minimapProcessor.getRadarSession();
        radar.update(class_310.method_1551().field_1687, class_310.method_1551().method_1560(), (class_1657)class_310.method_1551().field_1724);
    }
}

