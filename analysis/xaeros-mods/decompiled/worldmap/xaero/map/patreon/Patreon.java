/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  xaero.common.patreon.Patreon
 *  xaero.common.patreon.PatreonMod
 */
package xaero.map.patreon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.Cipher;
import net.minecraft.class_310;
import xaero.map.WorldMap;
import xaero.map.patreon.PatreonMod;
import xaero.map.patreon.decrypt.DecryptInputStream;
import xaero.map.platform.Services;

public class Patreon {
    private static boolean hasAutoUpdates;
    private static int onlineWidgetLevel;
    private static boolean notificationDisplayed;
    private static boolean loaded;
    private static String updateLocation;
    private static HashMap<String, Object> mods;
    private static ArrayList<Object> outdatedMods;
    private static Cipher cipher;
    private static int KEY_VERSION;
    private static String publicKeyString;
    private static boolean shouldRedirectToMinimap;
    private static File optionsFile;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void checkPatreon() {
        if (!WorldMap.settings.allowInternetAccess) {
            return;
        }
        if (shouldRedirectToMinimap) {
            xaero.common.patreon.Patreon.checkPatreon();
            return;
        }
        HashMap<String, Object> hashMap = mods;
        synchronized (hashMap) {
            if (loaded) {
                return;
            }
            Patreon.loadSettings();
            Object s = "http://data.chocolateminecraft.com/Versions_" + KEY_VERSION + "/Patreon2.dat";
            s = ((String)s).replaceAll(" ", "%20");
            try {
                String line;
                URL url = new URL((String)s);
                URLConnection conn = url.openConnection();
                conn.setReadTimeout(900);
                conn.setConnectTimeout(900);
                if (conn.getContentLengthLong() > 524288L) {
                    throw new IOException("Input too long to trust!");
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(new DecryptInputStream(conn.getInputStream(), cipher)));
                boolean parsingPatrons = false;
                String localPlayerName = class_310.method_1551().method_1548().method_1676();
                while ((line = reader.readLine()) != null && !line.equals("LAYOUTS")) {
                    String[] rewards;
                    if (line.startsWith("PATREON")) {
                        parsingPatrons = true;
                        continue;
                    }
                    if (!parsingPatrons || (rewards = line.split(";")).length <= 1 || !rewards[0].equalsIgnoreCase(localPlayerName)) continue;
                    for (int i = 1; i < rewards.length; ++i) {
                        String rewardString = rewards[i].trim();
                        if ("updates".equals(rewardString)) {
                            hasAutoUpdates = true;
                            continue;
                        }
                        String[] keyAndValue = rewardString.split(":");
                        if (keyAndValue.length < 2 || !keyAndValue[0].equals("widget_level")) continue;
                        try {
                            onlineWidgetLevel = Integer.parseInt(keyAndValue[1]);
                            continue;
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    }
                }
                updateLocation = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] args = line.split("\\t");
                    mods.put(args[0], new PatreonMod(args[0], args[1], args[2], args[3]));
                }
                reader.close();
            }
            catch (IOException ioe) {
                WorldMap.LOGGER.error("io exception while checking patreon: {}", (Object)ioe.getMessage());
                mods.clear();
            }
            catch (Throwable e) {
                WorldMap.LOGGER.error("suppressed exception", e);
                mods.clear();
            }
            finally {
                loaded = true;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addOutdatedMod(Object mod) {
        if (shouldRedirectToMinimap) {
            xaero.common.patreon.Patreon.addOutdatedMod((Object)mod);
            return;
        }
        ArrayList<Object> arrayList = Patreon.getOutdatedMods();
        synchronized (arrayList) {
            Patreon.getOutdatedMods().add(mod);
        }
    }

    public static void saveSettings() {
        if (shouldRedirectToMinimap) {
            xaero.common.patreon.Patreon.saveSettings();
            return;
        }
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(optionsFile));
            writer.close();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    public static void loadSettings() {
        if (shouldRedirectToMinimap) {
            xaero.common.patreon.Patreon.loadSettings();
            return;
        }
        try {
            String line;
            if (!optionsFile.exists()) {
                Patreon.saveSettings();
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(optionsFile));
            while ((line = reader.readLine()) != null) {
                String[] stringArray = line.split(":");
            }
            reader.close();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    public static ArrayList<Object> getOutdatedMods() {
        if (shouldRedirectToMinimap) {
            return xaero.common.patreon.Patreon.getOutdatedMods();
        }
        return outdatedMods;
    }

    public static boolean needsNotification() {
        if (shouldRedirectToMinimap) {
            return false;
        }
        return !Patreon.isNotificationDisplayed() && !outdatedMods.isEmpty();
    }

    public static boolean isNotificationDisplayed() {
        if (shouldRedirectToMinimap) {
            return xaero.common.patreon.Patreon.isNotificationDisplayed();
        }
        return notificationDisplayed;
    }

    public static void setNotificationDisplayed(boolean notificationDisplayed) {
        if (shouldRedirectToMinimap) {
            xaero.common.patreon.Patreon.setNotificationDisplayed((boolean)notificationDisplayed);
            return;
        }
        Patreon.notificationDisplayed = notificationDisplayed;
    }

    public static HashMap<String, Object> getMods() {
        if (shouldRedirectToMinimap) {
            return xaero.common.patreon.Patreon.getMods();
        }
        return mods;
    }

    public static String getUpdateLocation() {
        if (shouldRedirectToMinimap) {
            return xaero.common.patreon.Patreon.getUpdateLocation();
        }
        return updateLocation;
    }

    public static void setModInfo(Object mod, File modJAR, String versionID, String latestVersion, String latestVersionMD5, Runnable onVersionIgnore) {
        if (shouldRedirectToMinimap) {
            xaero.common.patreon.PatreonMod patreonEntry = (xaero.common.patreon.PatreonMod)mod;
            patreonEntry.modJar = modJAR;
            patreonEntry.currentVersion = versionID;
            patreonEntry.latestVersion = latestVersion;
            patreonEntry.md5 = latestVersionMD5;
            patreonEntry.onVersionIgnore = onVersionIgnore;
            return;
        }
        PatreonMod patreonEntry = (PatreonMod)mod;
        patreonEntry.modJar = modJAR;
        patreonEntry.currentVersion = versionID;
        patreonEntry.latestVersion = latestVersion;
        patreonEntry.md5 = latestVersionMD5;
        patreonEntry.onVersionIgnore = onVersionIgnore;
    }

    public static String getPublicKeyString() {
        if (shouldRedirectToMinimap) {
            return xaero.common.patreon.Patreon.getPublicKeyString2();
        }
        return publicKeyString;
    }

    public static int getKEY_VERSION() {
        if (shouldRedirectToMinimap) {
            return xaero.common.patreon.Patreon.getKEY_VERSION2();
        }
        return KEY_VERSION;
    }

    public static boolean getHasAutoUpdates() {
        if (shouldRedirectToMinimap) {
            return xaero.common.patreon.Patreon.getHasAutoUpdates();
        }
        return hasAutoUpdates;
    }

    public static int getOnlineWidgetLevel() {
        if (shouldRedirectToMinimap) {
            return xaero.common.patreon.Patreon.getOnlineWidgetLevel();
        }
        return onlineWidgetLevel;
    }

    static {
        loaded = false;
        mods = new HashMap();
        outdatedMods = new ArrayList();
        cipher = null;
        KEY_VERSION = 4;
        publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoBeELcruvAEIeLF/UsWF/v5rxyRXIpCs+eORLCbDw5cz9jHsnoypQKx0RTk5rcXIeA0HbEfY0eREB25quHjhZKul7MnzotQT+F2Qb1bPfHa6+SPie+pj79GGGAFP3npki6RqoU/wyYkd1tOomuD8v5ytEkOPC4U42kxxvx23A7vH6w46dew/E/HvfbBvZF2KrqdJtwKAunk847C3FgyhVq8/vzQc6mqAW6Mmn4zlwFvyCnTOWjIRw/I93WIM/uvhE3lt6pmtrWA2yIbKIj1z4pgG/K72EqHfYLGkBFTh7fV1wwCbpNTXZX2JnTfmvMGqzHjq7FijwVfCpFB/dWR3wQIDAQAB";
        shouldRedirectToMinimap = false;
        try {
            Class.forName("xaero.common.patreon.Patreon");
            shouldRedirectToMinimap = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        if (!shouldRedirectToMinimap) {
            try {
                cipher = Cipher.getInstance("RSA");
                KeyFactory factory = KeyFactory.getInstance("RSA");
                byte[] byteKey = Base64.getDecoder().decode(Patreon.getPublicKeyString().getBytes());
                X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
                PublicKey publicKey = factory.generatePublic(X509publicKey);
                cipher.init(2, publicKey);
            }
            catch (Exception e) {
                cipher = null;
                WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
            }
        }
        optionsFile = Services.PLATFORM.getGameDir().resolve("config").resolve("xaeropatreon.txt").toFile();
    }
}

