/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_437
 */
package xaero.map.gui;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_437;
import xaero.map.WorldMapSession;
import xaero.map.gui.MySmallButton;
import xaero.map.gui.ScreenBase;
import xaero.map.mods.SupportMods;
import xaero.map.world.MapWorld;

public class GuiMapTpCommand
extends ScreenBase {
    private MySmallButton confirmButton;
    private class_342 commandFormatTextField;
    private class_342 dimensionCommandFormatTextField;
    private String commandFormat;
    private String dimensionCommandFormat;
    private class_2561 waypointCommandHint = class_2561.method_43471((String)"gui.xaero_wm_teleport_command_waypoints_hint");

    public GuiMapTpCommand(class_437 parent, class_437 escape) {
        super(parent, escape, (class_2561)class_2561.method_43471((String)"gui.xaero_wm_teleport_command"));
    }

    @Override
    public void method_25426() {
        super.method_25426();
        WorldMapSession session = WorldMapSession.getCurrentSession();
        MapWorld mapWorld = session.getMapProcessor().getMapWorld();
        if (this.commandFormat == null) {
            this.commandFormat = mapWorld.getTeleportCommandFormat();
        }
        if (this.dimensionCommandFormat == null) {
            this.dimensionCommandFormat = mapWorld.getDimensionTeleportCommandFormat();
        }
        this.commandFormatTextField = new class_342(this.field_22793, this.field_22789 / 2 - 100, this.field_22790 / 7 + 20, 200, 20, (class_2561)class_2561.method_43471((String)"gui.xaero_wm_teleport_command"));
        this.commandFormatTextField.method_1880(128);
        this.commandFormatTextField.method_1852(this.commandFormat);
        this.dimensionCommandFormatTextField = new class_342(this.field_22793, this.field_22789 / 2 - 100, this.field_22790 / 7 + 50, 200, 20, (class_2561)class_2561.method_43471((String)"gui.xaero_wm_dimension_teleport_command"));
        this.dimensionCommandFormatTextField.method_1880(256);
        this.dimensionCommandFormatTextField.method_1852(this.dimensionCommandFormat);
        this.method_25429(this.commandFormatTextField);
        this.method_25429(this.dimensionCommandFormatTextField);
        if (SupportMods.minimap()) {
            this.method_37063((class_364)new MySmallButton(this.field_22789 / 2 - 75, this.field_22790 / 7 + 118, (class_2561)class_2561.method_43471((String)"gui.xaero_wm_teleport_command_waypoints"), b -> SupportMods.xaeroMinimap.openWaypointWorldTeleportCommandScreen(this, this.escape)));
        }
        this.confirmButton = new MySmallButton(this.field_22789 / 2 - 155, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43471((String)"gui.xaero_confirm"), b -> {
            this.updateFormat();
            if (this.canConfirm()) {
                mapWorld.setTeleportCommandFormat(this.commandFormat);
                mapWorld.setDimensionTeleportCommandFormat(this.dimensionCommandFormat);
                mapWorld.saveConfig();
                this.goBack();
            }
        });
        this.method_37063((class_364)this.confirmButton);
        this.method_37063((class_364)new MySmallButton(this.field_22789 / 2 + 5, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43471((String)"gui.xaero_cancel"), b -> this.goBack()));
    }

    public void method_25420(class_332 guiGraphics, int i, int j, float f) {
        this.renderEscapeScreen(guiGraphics, 0, 0, f);
        super.method_25420(guiGraphics, i, j, f);
        guiGraphics.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 20, -1);
        if (SupportMods.minimap()) {
            guiGraphics.method_27534(this.field_22793, this.waypointCommandHint, this.field_22789 / 2, this.field_22790 / 7 + 104, -5592406);
        }
        guiGraphics.method_25300(this.field_22793, "{x} {y} {z} {d}", this.field_22789 / 2, this.field_22790 / 7 + 6, -5592406);
    }

    @Override
    public void method_25394(class_332 guiGraphics, int mouseX, int mouseY, float partial) {
        super.method_25394(guiGraphics, mouseX, mouseY, partial);
        this.commandFormatTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
        this.dimensionCommandFormatTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
    }

    private boolean canConfirm() {
        return this.commandFormat != null && this.commandFormat.length() > 0 && this.dimensionCommandFormat != null && this.dimensionCommandFormat.length() > 0;
    }

    private void updateFormat() {
        this.commandFormat = this.commandFormatTextField.method_1882();
        this.dimensionCommandFormat = this.dimensionCommandFormatTextField.method_1882();
    }

    public void method_25393() {
        this.updateFormat();
        this.confirmButton.field_22763 = this.canConfirm();
    }

    public boolean method_25404(int par1, int par2, int par3) {
        if (par1 == 257 && this.commandFormat != null && this.commandFormat.length() > 0) {
            this.confirmButton.method_25348(0.0, 0.0);
        }
        return super.method_25404(par1, par2, par3);
    }
}

