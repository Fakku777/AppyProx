/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_339
 *  net.minecraft.class_342
 *  net.minecraft.class_4185
 */
package xaero.map.gui;

import java.io.IOException;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiMap;
import xaero.map.gui.TooltipButton;
import xaero.map.settings.ModOptions;
import xaero.map.world.MapDimension;

public class GuiCaveModeOptions {
    private MapDimension dimension;
    private boolean enabled;
    private class_339 caveModeStartSlider;
    private class_342 caveModeStartField;
    private boolean shouldUpdateSlider;

    public void onInit(GuiMap screen, MapProcessor mapProcessor) {
        this.caveModeStartSlider = null;
        this.caveModeStartField = null;
        this.dimension = mapProcessor.getMapWorld().getFutureDimension();
        boolean bl = this.enabled = this.enabled && this.dimension != null;
        if (this.enabled && this.dimension != null) {
            this.updateSlider(screen);
            this.updateField(screen);
            CursorBox caveModeTypeButtonTooltip = new CursorBox("gui.xaero_wm_box_cave_mode_type");
            screen.addButton(new TooltipButton(20, screen.field_22790 - 62, 150, 20, this.getCaveModeTypeButtonMessage(), b -> this.onCaveModeTypeButton(b, screen), () -> caveModeTypeButtonTooltip));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onCaveModeTypeButton(class_4185 b, GuiMap screen) {
        this.dimension.toggleCaveModeType(true);
        Object object = screen.getMapProcessor().uiSync;
        synchronized (object) {
            this.dimension.saveConfigUnsynced();
        }
        b.method_25355(this.getCaveModeTypeButtonMessage());
    }

    private class_342 createField(GuiMap screen) {
        class_342 field = new class_342(class_310.method_1551().field_1772, 172, screen.field_22790 - 40, 50, 20, (class_2561)class_2561.method_43471((String)"gui.xaero_wm_cave_mode_start"));
        field.method_1880(7);
        field.method_1852((String)(WorldMap.settings.caveModeStart == Integer.MAX_VALUE ? "" : "" + WorldMap.settings.caveModeStart));
        field.method_1863(text -> {
            try {
                WorldMap.settings.caveModeStart = text.isEmpty() || text.equalsIgnoreCase("auto") ? Integer.MAX_VALUE : Integer.parseInt(text);
                this.shouldUpdateSlider = true;
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            try {
                WorldMap.settings.saveSettings();
            }
            catch (IOException e) {
                WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
            }
        });
        return field;
    }

    private class_339 createSlider(GuiMap screen) {
        return ModOptions.CAVE_MODE_START.getXOption().createButton(20, screen.field_22790 - 40, 150);
    }

    private void updateField(GuiMap screen) {
        if (this.caveModeStartField == null) {
            this.caveModeStartField = this.createField(screen);
            screen.addButton(this.caveModeStartField);
        } else {
            this.caveModeStartField = this.createField(screen);
            screen.replaceRenderableWidget((class_339)this.caveModeStartField, (class_339)this.caveModeStartField);
        }
    }

    private void updateSlider(GuiMap screen) {
        if (this.caveModeStartSlider == null) {
            this.caveModeStartSlider = this.createSlider(screen);
            screen.addButton(this.caveModeStartSlider);
        } else {
            this.caveModeStartSlider = this.createSlider(screen);
            screen.replaceRenderableWidget(this.caveModeStartSlider, this.caveModeStartSlider);
        }
    }

    public void toggle(GuiMap screen) {
        this.enabled = WorldMap.settings.isCaveMapsAllowed() && !this.enabled;
        screen.method_25423(class_310.method_1551(), screen.field_22789, screen.field_22790);
    }

    public void onCaveModeStartSet(GuiMap screen) {
        if (this.enabled) {
            this.updateField(screen);
        }
    }

    public void tick(GuiMap screen) {
        if (this.shouldUpdateSlider) {
            this.updateSlider(screen);
            this.shouldUpdateSlider = false;
        }
        if (this.enabled) {
            this.caveModeStartField.method_1887(this.caveModeStartField.method_1882().isEmpty() ? class_1074.method_4662((String)"gui.xaero_wm_cave_mode_start_auto", (Object[])new Object[0]) : "");
        }
    }

    public void unfocusAll() {
        if (this.caveModeStartField != null) {
            this.caveModeStartField.method_25365(false);
        }
        if (this.caveModeStartSlider != null) {
            this.caveModeStartSlider.method_25365(false);
        }
    }

    private class_2561 getCaveModeTypeButtonMessage() {
        return class_2561.method_43470((String)(class_1074.method_4662((String)"gui.xaero_wm_cave_mode_type", (Object[])new Object[0]) + ": " + class_1074.method_4662((String)(this.dimension == null ? "N/A" : (this.dimension.getCaveModeType() == 0 ? "gui.xaero_off" : (this.dimension.getCaveModeType() == 1 ? "gui.xaero_wm_cave_mode_type_layered" : "gui.xaero_wm_cave_mode_type_full"))), (Object[])new Object[0])));
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

