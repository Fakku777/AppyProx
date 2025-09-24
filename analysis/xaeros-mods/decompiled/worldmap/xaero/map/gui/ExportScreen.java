/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 */
package xaero.map.gui;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import xaero.map.MapProcessor;
import xaero.map.file.export.PNGExportResult;
import xaero.map.gui.ConfigSettingEntry;
import xaero.map.gui.GuiSettings;
import xaero.map.gui.ISettingEntry;
import xaero.map.gui.MapTileSelection;
import xaero.map.gui.MySmallButton;
import xaero.map.settings.ModOptions;

public class ExportScreen
extends GuiSettings {
    private static final class_2561 EXPORTING_MESSAGE = class_2561.method_43471((String)"gui.xaero_export_screen_exporting");
    private final MapProcessor mapProcessor;
    private PNGExportResult result;
    private int stage;
    private final MapTileSelection selection;
    public boolean fullExport;

    public ExportScreen(class_437 backScreen, class_437 escScreen, MapProcessor mapProcessor, MapTileSelection selection) {
        super((class_2561)class_2561.method_43471((String)"gui.xaero_export_screen"), backScreen, escScreen);
        this.mapProcessor = mapProcessor;
        this.selection = selection;
        this.entries = new ISettingEntry[]{new ConfigSettingEntry(ModOptions.FULL_EXPORT), new ConfigSettingEntry(ModOptions.MULTIPLE_IMAGES_EXPORT), new ConfigSettingEntry(ModOptions.NIGHT_EXPORT), new ConfigSettingEntry(ModOptions.EXPORT_HIGHLIGHTS), new ConfigSettingEntry(ModOptions.EXPORT_SCALE_DOWN_SQUARE)};
        this.canSearch = false;
        this.shouldAddBackButton = false;
    }

    @Override
    public void method_25426() {
        if (this.stage > 0) {
            return;
        }
        super.method_25426();
        this.method_37063((class_364)new MySmallButton(this.field_22789 / 2 - 155, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43469((String)"gui.xaero_confirm", (Object[])new Object[0]), b -> {
            this.stage = 1;
            this.method_25423(this.field_22787, this.field_22789, this.field_22790);
        }));
        this.method_37063((class_364)new MySmallButton(this.field_22789 / 2 + 5, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43469((String)"gui.xaero_cancel", (Object[])new Object[0]), b -> this.goBack()));
    }

    @Override
    public void method_25420(class_332 guiGraphics, int i, int j, float f) {
        this.renderEscapeScreen(guiGraphics, 0, 0, f);
        super.method_25420(guiGraphics, i, j, f);
    }

    @Override
    public void method_25394(class_332 guiGraphics, int par1, int par2, float par3) {
        super.method_25394(guiGraphics, par1, par2, par3);
        if (this.result != null) {
            guiGraphics.method_27534(this.field_22787.field_1772, this.result.getMessage(), this.field_22789 / 2, 20, -1);
        }
        if (this.stage > 0) {
            guiGraphics.method_27534(this.field_22787.field_1772, EXPORTING_MESSAGE, this.field_22789 / 2, this.field_22790 / 6 + 68, -1);
            if (this.stage == 1) {
                this.stage = 2;
                return;
            }
        }
        if (this.stage != 2) {
            return;
        }
        if (this.mapProcessor.getMapSaveLoad().exportPNG(this, this.fullExport ? null : this.selection)) {
            this.stage = 3;
            this.result = null;
            for (class_364 c : this.method_25396()) {
                if (!(c instanceof class_4185)) continue;
                ((class_4185)c).field_22763 = false;
            }
            return;
        }
        this.stage = 0;
        this.method_25423(this.field_22787, this.field_22789, this.field_22790);
    }

    public void onExportDone(PNGExportResult result) {
        this.result = result;
        this.stage = 0;
    }

    public MapTileSelection getSelection() {
        return this.selection;
    }
}

