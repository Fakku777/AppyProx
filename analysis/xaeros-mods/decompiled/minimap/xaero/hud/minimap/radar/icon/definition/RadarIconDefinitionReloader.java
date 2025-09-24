/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonSyntaxException
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  net.minecraft.class_7923
 */
package xaero.hud.minimap.radar.icon.definition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_7923;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;

public class RadarIconDefinitionReloader {
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public void reloadResources(Map<class_2960, RadarIconDefinition> iconDefinitions) {
        MinimapLogs.LOGGER.info("Reloading radar icon resources...");
        Set entityIds = class_7923.field_41177.method_10235();
        int attempts = 5;
        for (int i = 0; i < attempts; ++i) {
            try {
                this.reloadResourcesAttempt(iconDefinitions, this.gson, entityIds);
                break;
            }
            catch (IOException ioe) {
                if (i != attempts - 1) continue;
                throw new RuntimeException(ioe);
            }
        }
        MinimapLogs.LOGGER.info("Reloaded radar icon resources!");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void reloadResourcesAttempt(Map<class_2960, RadarIconDefinition> iconDefinitions, Gson gson, Set<class_2960> entityIds) throws IOException {
        iconDefinitions.clear();
        for (class_2960 id : entityIds) {
            InputStream resourceInput = null;
            BufferedReader reader = null;
            String entityDefinitionJson = null;
            try {
                class_3298 resource;
                Optional oResource = class_310.method_1551().method_1478().method_14486(class_2960.method_60655((String)"xaerominimap", (String)("entity/icon/definition/" + id.method_12836() + "/" + id.method_12832() + ".json")));
                if (!oResource.isPresent() || (resource = (class_3298)oResource.get()) == null) continue;
                resourceInput = resource.method_14482();
                reader = new BufferedReader(new InputStreamReader(resourceInput));
                StringBuilder stringBuilder = new StringBuilder();
                reader.lines().forEach(line -> {
                    stringBuilder.append((String)line);
                    stringBuilder.append('\n');
                });
                entityDefinitionJson = stringBuilder.toString();
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
                if (resourceInput == null) continue;
                resourceInput.close();
                continue;
            }
            try {
                RadarIconDefinition radarIconDefinition = (RadarIconDefinition)gson.fromJson(entityDefinitionJson, RadarIconDefinition.class);
                radarIconDefinition.construct(id);
                iconDefinitions.put(id, radarIconDefinition);
            }
            catch (JsonSyntaxException jse) {
                MinimapLogs.LOGGER.error("Json syntax exception when loading the radar icon definition for " + String.valueOf(id) + ".", (Throwable)jse);
            }
        }
    }
}

