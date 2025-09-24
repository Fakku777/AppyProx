/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mods;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import xaero.common.IXaeroMinimap;
import xaero.common.mods.SupportFramedBlocks;
import xaero.common.mods.SupportIris;
import xaero.common.mods.SupportXaeroWorldmap;
import xaero.common.mods.pac.SupportOpenPartiesAndClaims;
import xaero.hud.minimap.MinimapLogs;

public abstract class SupportMods {
    public SupportXaeroWorldmap worldmapSupport = null;
    public SupportOpenPartiesAndClaims xaeroPac;
    private IXaeroMinimap modMain;
    public boolean optifine;
    public boolean vivecraft;
    public boolean iris;
    public boolean ftbTeams;
    public SupportIris supportIris;
    public SupportFramedBlocks supportFramedBlocks;

    public boolean worldmap() {
        return this.worldmapSupport != null;
    }

    public boolean pac() {
        return this.xaeroPac != null;
    }

    public boolean shouldUseWorldMapChunks() {
        return this.worldmap() && this.modMain.getSettings().getUseWorldMap();
    }

    public boolean shouldUseWorldMapCaveChunks() {
        return this.shouldUseWorldMapChunks() && this.worldmapSupport.caveLayersAreUsable();
    }

    public boolean framedBlocks() {
        return this.supportFramedBlocks != null;
    }

    public static void checkForMinimapDuplicates(String otherModMainClass) {
        try {
            Class.forName(otherModMainClass);
            throw new RuntimeException("Better PVP contains Xaero's Minimap by default. Do not install Better PVP and Xaero's Minimap together!");
        }
        catch (ClassNotFoundException classNotFoundException) {
            return;
        }
    }

    public SupportMods(IXaeroMinimap modMain) {
        this.modMain = modMain;
        try {
            Class<?> wmClassTest = Class.forName("xaero.map.WorldMap");
            this.worldmapSupport = new SupportXaeroWorldmap(modMain);
            MinimapLogs.LOGGER.info("Xaero's Minimap: World Map found!");
        }
        catch (ClassNotFoundException wmClassTest) {
            // empty catch block
        }
        try {
            Class<?> pacClassTest = Class.forName("xaero.pac.OpenPartiesAndClaims");
            this.xaeroPac = new SupportOpenPartiesAndClaims(modMain);
            this.xaeroPac.register();
            MinimapLogs.LOGGER.info("Xaero's Minimap: Open Parties And Claims found!");
        }
        catch (ClassNotFoundException pacClassTest) {
            // empty catch block
        }
        try {
            Class<?> optifineClassTest = Class.forName("optifine.Patcher");
            this.optifine = true;
            MinimapLogs.LOGGER.info("Optifine!");
        }
        catch (ClassNotFoundException e) {
            this.optifine = false;
            MinimapLogs.LOGGER.info("No Optifine!");
        }
        try {
            Class<?> vivecraftClassTest = Class.forName("org.vivecraft.api.VRData");
            this.vivecraft = true;
            try {
                Class<?> vrStateClass = Class.forName("org.vivecraft.VRState");
                Method checkVRMethod = vrStateClass.getDeclaredMethod("checkVR", new Class[0]);
                this.vivecraft = (Boolean)checkVRMethod.invoke(null, new Object[0]);
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (InvocationTargetException invocationTargetException) {}
        }
        catch (ClassNotFoundException vivecraftClassTest) {
            // empty catch block
        }
        if (this.vivecraft) {
            MinimapLogs.LOGGER.info("Xaero's Minimap: Vivecraft!");
        } else {
            MinimapLogs.LOGGER.info("Xaero's Minimap: No Vivecraft!");
        }
        try {
            Class<?> mmClassTest = Class.forName("xfacthd.framedblocks.FramedBlocks");
            this.supportFramedBlocks = new SupportFramedBlocks();
            MinimapLogs.LOGGER.info("Xaero's Minimap: Framed Blocks found!");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            this.supportIris = new SupportIris();
            this.iris = true;
            MinimapLogs.LOGGER.info("Xaero's Minimap: Iris found!");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

