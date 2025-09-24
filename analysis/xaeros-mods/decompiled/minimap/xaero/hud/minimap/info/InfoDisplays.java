/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info;

import java.util.ArrayList;
import xaero.hud.minimap.info.BuiltInInfoDisplays;
import xaero.hud.minimap.info.InfoDisplayIO;
import xaero.hud.minimap.info.InfoDisplayManager;
import xaero.hud.minimap.info.render.InfoDisplayRenderer;

public class InfoDisplays {
    private final InfoDisplayManager manager = InfoDisplayManager.Builder.begin().build();
    private final InfoDisplayIO io;
    private final InfoDisplayRenderer renderer;

    public InfoDisplays() {
        BuiltInInfoDisplays.forEach(this.manager::add);
        this.manager.setOrder(new ArrayList<String>());
        this.io = new InfoDisplayIO(this.manager);
        this.renderer = InfoDisplayRenderer.Builder.begin().build();
    }

    public InfoDisplayManager getManager() {
        return this.manager;
    }

    public InfoDisplayIO getIo() {
        return this.io;
    }

    public InfoDisplayRenderer getRenderer() {
        return this.renderer;
    }
}

