/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.file;

import java.io.File;

public interface MapRegionInfo {
    public boolean shouldCache();

    public File getRegionFile();

    public File getCacheFile();

    public String getWorldId();

    public String getDimId();

    public String getMwId();

    public int getRegionX();

    public int getRegionZ();

    public void setShouldCache(boolean var1, String var2);

    public void setCacheFile(File var1);

    public boolean hasLookedForCache();
}

