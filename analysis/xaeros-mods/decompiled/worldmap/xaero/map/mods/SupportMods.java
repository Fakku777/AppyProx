/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.mods;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import xaero.map.WorldMap;
import xaero.map.mods.SupportFramedBlocks;
import xaero.map.mods.SupportIris;
import xaero.map.mods.SupportXaeroMinimap;
import xaero.map.mods.pac.SupportOpenPartiesAndClaims;

public class SupportMods {
    public static SupportXaeroMinimap xaeroMinimap = null;
    public static SupportOpenPartiesAndClaims xaeroPac = null;
    public static boolean optifine;
    public static boolean vivecraft;
    public static boolean iris;
    public static SupportIris supportIris;
    public static SupportFramedBlocks supportFramedBlocks;

    public static boolean minimap() {
        return xaeroMinimap != null && SupportMods.xaeroMinimap.modMain != null;
    }

    public static boolean framedBlocks() {
        return supportFramedBlocks != null;
    }

    public static boolean pac() {
        return xaeroPac != null;
    }

    public void load() {
        Class<?> mmClassTest2;
        try {
            mmClassTest2 = Class.forName("xaero.common.IXaeroMinimap");
            xaeroMinimap = new SupportXaeroMinimap();
            xaeroMinimap.register();
        }
        catch (ClassNotFoundException mmClassTest2) {
            // empty catch block
        }
        try {
            Class<?> pacClassTest = Class.forName("xaero.pac.OpenPartiesAndClaims");
            xaeroPac = new SupportOpenPartiesAndClaims();
            xaeroPac.register();
            WorldMap.LOGGER.info("Xaero's WorldMap Mod: Open Parties And Claims found!");
        }
        catch (ClassNotFoundException pacClassTest) {
            // empty catch block
        }
        try {
            Class<?> optifineClassTest = Class.forName("optifine.Patcher");
            optifine = true;
            WorldMap.LOGGER.info("Optifine!");
        }
        catch (ClassNotFoundException e) {
            optifine = false;
            WorldMap.LOGGER.info("No Optifine!");
        }
        try {
            Class<?> vivecraftClassTest = Class.forName("org.vivecraft.api.VRData");
            vivecraft = true;
            try {
                Class<?> vrStateClass = Class.forName("org.vivecraft.VRState");
                Method checkVRMethod = vrStateClass.getDeclaredMethod("checkVR", new Class[0]);
                vivecraft = (Boolean)checkVRMethod.invoke(null, new Object[0]);
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
        if (vivecraft) {
            WorldMap.LOGGER.info("Xaero's World Map: Vivecraft!");
        } else {
            WorldMap.LOGGER.info("Xaero's World Map: No Vivecraft!");
        }
        try {
            mmClassTest2 = Class.forName("xfacthd.framedblocks.FramedBlocks");
            supportFramedBlocks = new SupportFramedBlocks();
            WorldMap.LOGGER.info("Xaero's World Map: Framed Blocks found!");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            supportIris = new SupportIris();
            iris = true;
            WorldMap.LOGGER.info("Xaero's World Map: Iris found!");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static {
        supportFramedBlocks = null;
    }
}

