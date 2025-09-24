/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 */
package xaero.hud.minimap.radar.icon.definition;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_2960;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinitionReloader;

public class RadarIconDefinitionManager {
    private final Map<class_2960, RadarIconDefinition> definitions = new HashMap<class_2960, RadarIconDefinition>();
    private final RadarIconDefinitionReloader reloader = new RadarIconDefinitionReloader();

    public RadarIconDefinition get(class_2960 key) {
        return this.definitions.get(key);
    }

    public void reloadResources() {
        this.reloader.reloadResources(this.definitions);
    }
}

