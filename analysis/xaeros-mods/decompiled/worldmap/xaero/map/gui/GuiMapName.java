/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 */
package xaero.map.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.gui.MySmallButton;
import xaero.map.gui.ScreenBase;
import xaero.map.world.MapDimension;

public class GuiMapName
extends ScreenBase {
    private class_342 nameTextField;
    private MapDimension mapDimension;
    private String editingMWId;
    private String currentNameFieldContent;
    private MapProcessor mapProcessor;
    private class_4185 confirmButton;

    public GuiMapName(MapProcessor mapProcessor, class_437 par1GuiScreen, class_437 escape, MapDimension mapDimension, String editingMWId) {
        super(par1GuiScreen, escape, (class_2561)class_2561.method_43471((String)"gui.xaero_map_name"));
        this.mapDimension = mapDimension;
        this.editingMWId = editingMWId;
        this.currentNameFieldContent = editingMWId == null ? "" : mapDimension.getMultiworldName(editingMWId);
        this.mapProcessor = mapProcessor;
    }

    @Override
    public void method_25426() {
        super.method_25426();
        if (this.nameTextField != null) {
            this.currentNameFieldContent = this.nameTextField.method_1882();
        }
        this.nameTextField = new class_342(this.field_22793, this.field_22789 / 2 - 100, 60, 200, 20, (class_2561)class_2561.method_43471((String)"gui.xaero_map_name"));
        this.nameTextField.method_1852(this.currentNameFieldContent);
        this.method_25395((class_364)this.nameTextField);
        this.method_37063((class_364)this.nameTextField);
        this.confirmButton = new MySmallButton(this.field_22789 / 2 - 155, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43469((String)"gui.xaero_confirm", (Object[])new Object[0]), b -> {
            if (this.canConfirm()) {
                Object object = this.mapProcessor.uiSync;
                synchronized (object) {
                    if (this.mapProcessor.getMapWorld() == this.mapDimension.getMapWorld()) {
                        Object mwIdFixed;
                        String unfilteredName = this.nameTextField.method_1882();
                        if (this.editingMWId == null) {
                            Object mwId = unfilteredName.toLowerCase().replaceAll("[^a-z0-9]+", "");
                            if (((String)mwId).isEmpty()) {
                                mwId = "map";
                            }
                            mwId = "cm$" + (String)mwId;
                            boolean mwAdded = false;
                            mwIdFixed = mwId;
                            int fix = 1;
                            while (!mwAdded) {
                                mwAdded = this.mapDimension.addMultiworldChecked((String)mwIdFixed);
                                if (mwAdded) continue;
                                mwIdFixed = (String)mwId + ++fix;
                            }
                            Path dimensionFolderPath = this.mapDimension.getMainFolderPath();
                            Path multiworldFolderPath = dimensionFolderPath.resolve((String)mwIdFixed);
                            try {
                                Files.createDirectories(multiworldFolderPath, new FileAttribute[0]);
                            }
                            catch (IOException e) {
                                WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                            }
                            this.mapDimension.setMultiworldUnsynced((String)mwIdFixed);
                        } else {
                            mwIdFixed = this.editingMWId;
                        }
                        this.mapDimension.setMultiworldName((String)mwIdFixed, unfilteredName);
                        this.mapDimension.saveConfigUnsynced();
                        this.goBack();
                    }
                }
            }
        });
        this.method_37063((class_364)this.confirmButton);
        this.method_37063((class_364)new MySmallButton(this.field_22789 / 2 + 5, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43469((String)"gui.xaero_cancel", (Object[])new Object[0]), b -> this.goBack()));
        this.updateConfirmButton();
    }

    protected void method_56131() {
    }

    private boolean canConfirm() {
        return this.nameTextField.method_1882().length() > 0;
    }

    private void updateConfirmButton() {
        this.confirmButton.field_22763 = this.canConfirm();
    }

    public boolean method_25404(int par1, int par2, int par3) {
        boolean result = super.method_25404(par1, par2, par3);
        if (par1 == 257 && this.canConfirm()) {
            this.confirmButton.method_25348(0.0, 0.0);
            return true;
        }
        return result;
    }

    public void method_25393() {
        this.updateConfirmButton();
    }

    public void method_25420(class_332 guiGraphics, int par1, int par2, float par3) {
        this.renderEscapeScreen(guiGraphics, 0, 0, par3);
        super.method_25420(guiGraphics, par1, par2, par3);
        guiGraphics.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 20, -1);
        this.nameTextField.method_25394(guiGraphics, par1, par2, par3);
    }

    @Override
    public void method_25394(class_332 guiGraphics, int par1, int par2, float par3) {
        super.method_25394(guiGraphics, par1, par2, par3);
    }
}

