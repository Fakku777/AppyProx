/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 *  net.minecraft.class_3675$class_306
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_437
 *  org.lwjgl.glfw.GLFW
 */
package xaero.map.controls;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.WorldMapSession;
import xaero.map.controls.ControlsRegister;
import xaero.map.controls.IKeyBindingHelper;
import xaero.map.controls.KeyEvent;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiWorldMapSettings;
import xaero.map.platform.Services;

public class ControlsHandler {
    private MapProcessor mapProcessor;
    private ArrayList<KeyEvent> keyEvents = new ArrayList();
    private ArrayList<KeyEvent> oldKeyEvents = new ArrayList();

    public ControlsHandler(MapProcessor mapProcessor) {
        this.mapProcessor = mapProcessor;
    }

    private boolean eventExists(class_304 kb) {
        for (KeyEvent o : this.keyEvents) {
            if (o.getKb() != kb) continue;
            return true;
        }
        return this.oldEventExists(kb);
    }

    private boolean oldEventExists(class_304 kb) {
        for (KeyEvent o : this.oldKeyEvents) {
            if (o.getKb() != kb) continue;
            return true;
        }
        return false;
    }

    public static void setKeyState(class_304 kb, boolean pressed) {
        if (kb.method_1434() != pressed) {
            class_304.method_1416((class_3675.class_306)Services.PLATFORM.getKeyBindingHelper().getBoundKeyOf(kb), (boolean)pressed);
        }
    }

    public static boolean isDown(class_304 kb) {
        IKeyBindingHelper keyBindingHelper = Services.PLATFORM.getKeyBindingHelper();
        if (keyBindingHelper.getBoundKeyOf(kb).method_1444() == -1) {
            return false;
        }
        if (keyBindingHelper.getBoundKeyOf(kb).method_1442() == class_3675.class_307.field_1672) {
            return GLFW.glfwGetMouseButton((long)class_310.method_1551().method_22683().method_4490(), (int)keyBindingHelper.getBoundKeyOf(kb).method_1444()) == 1;
        }
        if (keyBindingHelper.getBoundKeyOf(kb).method_1442() == class_3675.class_307.field_1668) {
            return class_3675.method_15987((long)class_310.method_1551().method_22683().method_4490(), (int)keyBindingHelper.getBoundKeyOf(kb).method_1444());
        }
        return false;
    }

    public static boolean isKeyRepeat(class_304 kb) {
        return kb != ControlsRegister.keyOpenMap && kb != ControlsRegister.keyOpenSettings && kb != ControlsRegister.keyToggleDimension;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void keyDown(class_304 kb, boolean tickEnd, boolean isRepeat) {
        class_310 mc = class_310.method_1551();
        if (!tickEnd) {
            if (kb == ControlsRegister.keyOpenMap) {
                mc.method_1507((class_437)new GuiMap(null, null, this.mapProcessor, mc.method_1560()));
            } else if (kb == ControlsRegister.keyOpenSettings) {
                mc.method_1507((class_437)new GuiWorldMapSettings());
            } else if (kb == ControlsRegister.keyQuickConfirm) {
                WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
                MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                Object object = mapProcessor.uiPauseSync;
                synchronized (object) {
                    if (!mapProcessor.isUIPaused()) {
                        mapProcessor.quickConfirmMultiworld();
                    }
                }
            } else if (kb == ControlsRegister.keyToggleDimension) {
                this.mapProcessor.getMapWorld().toggleDimension(!class_437.method_25442());
                String messageType = this.mapProcessor.getMapWorld().getCustomDimensionId() == null ? "gui.xaero_switched_to_current_dimension" : "gui.xaero_switched_to_dimension";
                class_2960 messageDimLoc = this.mapProcessor.getMapWorld().getFutureDimensionId() == null ? null : this.mapProcessor.getMapWorld().getFutureDimensionId().method_29177();
                mc.field_1705.method_1743().method_1812((class_2561)class_2561.method_43469((String)messageType, (Object[])new Object[]{messageDimLoc.toString()}));
            }
        }
    }

    public void keyUp(class_304 kb, boolean tickEnd) {
        if (!tickEnd) {
            // empty if block
        }
    }

    public void handleKeyEvents() {
        KeyEvent ke;
        int i;
        class_310 mc = class_310.method_1551();
        this.onKeyInput(mc);
        for (i = 0; i < this.keyEvents.size(); ++i) {
            ke = this.keyEvents.get(i);
            if (mc.field_1755 == null) {
                this.keyDown(ke.getKb(), ke.isTickEnd(), ke.isRepeat());
            }
            if (!ke.isRepeat()) {
                if (!this.oldEventExists(ke.getKb())) {
                    this.oldKeyEvents.add(ke);
                }
                this.keyEvents.remove(i);
                --i;
                continue;
            }
            if (ControlsHandler.isDown(ke.getKb())) continue;
            this.keyUp(ke.getKb(), ke.isTickEnd());
            this.keyEvents.remove(i);
            --i;
        }
        for (i = 0; i < this.oldKeyEvents.size(); ++i) {
            ke = this.oldKeyEvents.get(i);
            if (ControlsHandler.isDown(ke.getKb())) continue;
            this.keyUp(ke.getKb(), ke.isTickEnd());
            this.oldKeyEvents.remove(i);
            --i;
        }
    }

    public void onKeyInput(class_310 mc) {
        List<class_304> kbs = WorldMap.controlsRegister.keybindings;
        for (int i = 0; i < kbs.size(); ++i) {
            class_304 kb = kbs.get(i);
            try {
                boolean pressed = kb.method_1436();
                while (kb.method_1436()) {
                }
                if (mc.field_1755 != null || this.eventExists(kb) || !pressed) continue;
                this.keyEvents.add(new KeyEvent(kb, false, ControlsHandler.isKeyRepeat(kb), true));
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

