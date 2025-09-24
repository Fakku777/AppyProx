/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_437;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.TextureUtils;
import xaero.common.gui.GuiWaypointsOptions;
import xaero.common.gui.MySmallButton;
import xaero.common.gui.ScreenBase;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.render.util.GuiRenderUtil;

public class GuiWorldTpCommand
extends ScreenBase {
    private MySmallButton confirmButton;
    private class_342 commandFormatTextField;
    private class_342 rotationCommandFormatTextField;
    private boolean usingDefault;
    private String commandFormat;
    private String rotationCommandFormat;
    private MinimapWorldRootContainer rootContainer;

    public GuiWorldTpCommand(IXaeroMinimap modMain, class_437 parent, class_437 escape, MinimapWorldRootContainer rootContainer) {
        super(modMain, parent, escape, (class_2561)class_2561.method_43471((String)"gui.xaero_world_teleport_command"));
        this.rootContainer = rootContainer;
        this.commandFormat = rootContainer.getConfig().getServerTeleportCommandFormat() == null ? modMain.getSettings().defaultWaypointTPCommandFormat : rootContainer.getConfig().getServerTeleportCommandFormat();
        this.rotationCommandFormat = rootContainer.getConfig().getServerTeleportCommandRotationFormat() == null ? modMain.getSettings().defaultWaypointTPCommandRotationFormat : rootContainer.getConfig().getServerTeleportCommandRotationFormat();
        this.usingDefault = rootContainer.getConfig().isUsingDefaultTeleportCommand();
    }

    @Override
    public void method_25426() {
        super.method_25426();
        this.parent.method_25410(this.field_22787, this.field_22789, this.field_22790);
        this.commandFormatTextField = new class_342(this, this.field_22793, this.field_22789 / 2 - 100, this.field_22790 / 7 + 50, 200, 20, (class_2561)class_2561.method_43471((String)"gui.xaero_world_teleport_command")){

            public void method_1867(String textToWrite) {
                if (this.field_22763) {
                    super.method_1867(textToWrite);
                }
            }

            public boolean method_25402(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
                if (this.field_22763) {
                    return super.method_25402(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
                }
                return false;
            }

            public void method_1878(int p_146175_1_) {
                if (this.field_22763) {
                    super.method_1878(p_146175_1_);
                }
            }

            public void method_1877(int p_146177_1_) {
                if (this.field_22763) {
                    super.method_1877(p_146177_1_);
                }
            }
        };
        this.commandFormatTextField.method_1880(128);
        this.rotationCommandFormatTextField = new class_342(this, this.field_22793, this.field_22789 / 2 - 100, this.field_22790 / 7 + 98, 200, 20, (class_2561)class_2561.method_43471((String)"gui.xaero_world_teleport_command_with_rotation")){

            public void method_1867(String textToWrite) {
                if (this.field_22763) {
                    super.method_1867(textToWrite);
                }
            }

            public boolean method_25402(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
                if (this.field_22763) {
                    return super.method_25402(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
                }
                return false;
            }

            public void method_1878(int p_146175_1_) {
                if (this.field_22763) {
                    super.method_1878(p_146175_1_);
                }
            }

            public void method_1877(int p_146177_1_) {
                if (this.field_22763) {
                    super.method_1877(p_146177_1_);
                }
            }
        };
        this.rotationCommandFormatTextField.method_1880(128);
        this.commandFormatTextField.field_22763 = !this.usingDefault;
        this.rotationCommandFormatTextField.field_22763 = !this.usingDefault;
        this.commandFormatTextField.method_1852(this.commandFormat);
        this.rotationCommandFormatTextField.method_1852(this.rotationCommandFormat);
        this.method_25429(this.commandFormatTextField);
        this.method_25429(this.rotationCommandFormatTextField);
        this.confirmButton = new MySmallButton(200, this.field_22789 / 2 - 155, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43469((String)"gui.xaero_confirm", (Object[])new Object[0]), b -> {
            if (this.commandFormat.equals(this.modMain.getSettings().defaultWaypointTPCommandFormat) && this.rotationCommandFormat.equals(this.modMain.getSettings().defaultWaypointTPCommandRotationFormat)) {
                this.usingDefault = true;
                this.commandFormat = null;
                this.rotationCommandFormat = null;
            }
            this.rootContainer.getConfig().setUsingDefaultTeleportCommand(this.usingDefault);
            this.rootContainer.getConfig().setServerTeleportCommandFormat(this.commandFormat);
            this.rootContainer.getConfig().setServerTeleportCommandRotationFormat(this.rotationCommandFormat);
            this.rootContainer.getSession().getWorldManagerIO().getRootConfigIO().save(this.rootContainer);
            this.goBack();
        });
        this.method_37063((class_364)this.confirmButton);
        this.method_37063((class_364)new MySmallButton(201, this.field_22789 / 2 + 5, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43469((String)"gui.xaero_cancel", (Object[])new Object[0]), b -> this.goBack()));
        this.method_37063((class_364)new MySmallButton(202, this.field_22789 / 2 - 75, this.field_22790 / 7 + 8, (class_2561)class_2561.method_43470((String)(class_1074.method_4662((String)"gui.xaero_use_default", (Object[])new Object[0]) + ": " + ModSettings.getTranslation(this.usingDefault))), b -> {
            this.usingDefault = !this.usingDefault;
            this.commandFormatTextField.field_22763 = !this.usingDefault;
            this.rotationCommandFormatTextField.field_22763 = !this.usingDefault;
            this.method_25423(this.field_22787, this.field_22789, this.field_22790);
        }));
    }

    public void method_25420(class_332 guiGraphics, int i, int j, float f) {
        if (this.parent instanceof GuiWaypointsOptions) {
            ((GuiWaypointsOptions)this.parent).parent.method_47413(guiGraphics, 0, 0, f);
            GuiRenderUtil.flushGUI();
        }
        TextureUtils.clearRenderTargetDepth(this.field_22787.method_1522(), 1.0f);
        super.method_25420(guiGraphics, i, j, f);
        GuiRenderUtil.flushGUI();
        super.method_25420(guiGraphics, i, j, f);
        guiGraphics.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 20, -1);
        guiGraphics.method_25300(this.field_22793, "{x} {y} {z} {name}", this.field_22789 / 2, this.field_22790 / 7 + 36, -5592406);
        guiGraphics.method_25300(this.field_22793, "{x} {y} {z} {name} {yaw}", this.field_22789 / 2, this.field_22790 / 7 + 84, -5592406);
    }

    @Override
    public void method_25394(class_332 guiGraphics, int mouseX, int mouseY, float partial) {
        super.method_25394(guiGraphics, mouseX, mouseY, partial);
        if (this.usingDefault) {
            this.commandFormatTextField.method_1852(this.modMain.getSettings().defaultWaypointTPCommandFormat);
            this.rotationCommandFormatTextField.method_1852(this.modMain.getSettings().defaultWaypointTPCommandRotationFormat);
            this.commandFormatTextField.method_1868(-11184811);
            this.rotationCommandFormatTextField.method_1868(-11184811);
        }
        this.commandFormatTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
        this.rotationCommandFormatTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
        if (this.usingDefault) {
            this.commandFormatTextField.method_1852(this.commandFormat);
            this.rotationCommandFormatTextField.method_1852(this.rotationCommandFormat);
            this.commandFormatTextField.method_1868(-1);
            this.rotationCommandFormatTextField.method_1868(-1);
        }
    }

    public void method_25393() {
        this.commandFormat = this.commandFormatTextField.method_1882();
        this.rotationCommandFormat = this.rotationCommandFormatTextField.method_1882();
        this.confirmButton.field_22763 = this.commandFormat != null && this.commandFormat.length() > 0 && this.rotationCommandFormat != null && this.rotationCommandFormat.length() > 0 || this.usingDefault;
    }

    public boolean method_25404(int par1, int par2, int par3) {
        if (par1 == 257 && (this.commandFormatTextField.method_25370() || this.rotationCommandFormatTextField.method_25370()) && this.commandFormat != null && this.commandFormat.length() > 0 && this.rotationCommandFormat != null && this.rotationCommandFormat.length() > 0) {
            this.confirmButton.method_25348(0.0, 0.0);
        }
        return super.method_25404(par1, par2, par3);
    }
}

