/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
 */
package xaero.map.profile;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import xaero.map.WorldMap;

public class SimpleProfiler {
    private final Object2LongMap<String> sections = new Object2LongOpenHashMap();
    private String currentSection;
    private long previousNanoTime;

    public void reset() {
        this.sections.clear();
        this.currentSection = null;
    }

    private void addTime(String sectionName, long time) {
        long current = this.sections.getOrDefault((Object)sectionName, 0L);
        this.sections.put((Object)sectionName, current + time);
    }

    public void section(String sectionName) {
        long currentTime = System.nanoTime();
        if (this.currentSection != null) {
            long passed = currentTime - this.previousNanoTime;
            this.addTime(this.currentSection, passed);
        }
        this.previousNanoTime = currentTime;
        this.currentSection = sectionName;
    }

    public void end() {
        this.section(null);
    }

    public void debug() {
        this.sections.forEach((sectionName, time) -> WorldMap.LOGGER.info(sectionName + " : " + time + " (" + (double)(time / 100000L) / 10.0 + " ms)"));
    }
}

