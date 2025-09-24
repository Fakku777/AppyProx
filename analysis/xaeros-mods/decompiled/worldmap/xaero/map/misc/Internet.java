/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import xaero.map.WorldMap;
import xaero.map.patreon.Patreon;
import xaero.map.patreon.decrypt.DecryptInputStream;
import xaero.map.settings.ModSettings;

public class Internet {
    public static Cipher cipher = null;

    public static void checkModVersion() {
        int keyVersion;
        if (!WorldMap.settings.allowInternetAccess) {
            return;
        }
        Object s = "http://data.chocolateminecraft.com/Versions_" + keyVersion + "/WorldMap" + ((keyVersion = Patreon.getKEY_VERSION()) >= 4 ? ".dat" : ".txt");
        s = ((String)s).replaceAll(" ", "%20");
        try {
            if (cipher == null) {
                throw new Exception("Cipher instance is null!");
            }
            URL url = new URL((String)s);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(900);
            conn.setConnectTimeout(900);
            if (conn.getContentLengthLong() > 524288L) {
                throw new IOException("Input too long to trust!");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new DecryptInputStream(conn.getInputStream(), cipher), "UTF8"));
            WorldMap.isOutdated = true;
            String line = reader.readLine();
            if (line != null) {
                WorldMap.newestUpdateID = Integer.parseInt(line);
                if (!ModSettings.updateNotification || WorldMap.newestUpdateID == ModSettings.ignoreUpdate) {
                    WorldMap.isOutdated = false;
                    reader.close();
                    return;
                }
            }
            boolean versionFound = false;
            String[] current = WorldMap.INSTANCE.getVersionID().split("_");
            while ((line = reader.readLine()) != null) {
                String[] args;
                if (line.equals(WorldMap.INSTANCE.getVersionID())) {
                    WorldMap.isOutdated = false;
                    break;
                }
                if (!Patreon.getHasAutoUpdates()) continue;
                if (versionFound) {
                    if (line.startsWith("meta;")) {
                        String[] metadata = line.substring(5).split(";");
                        WorldMap.latestVersionMD5 = metadata[0];
                    }
                    versionFound = false;
                }
                if (!line.startsWith(current[0] + "_") || (args = line.split("_")).length != current.length) continue;
                boolean sameType = true;
                if (current.length > 2) {
                    for (int i = 2; i < current.length && sameType; ++i) {
                        if (args[i].equals(current[i])) continue;
                        sameType = false;
                    }
                }
                if (!sameType) continue;
                WorldMap.latestVersion = args[1];
                versionFound = true;
            }
            reader.close();
        }
        catch (IOException ioe) {
            WorldMap.LOGGER.error("io exception while checking versions: {}", (Object)ioe.getMessage());
            WorldMap.isOutdated = false;
        }
        catch (Throwable e) {
            WorldMap.LOGGER.error("suppressed exception", e);
            WorldMap.isOutdated = false;
        }
    }

    static {
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
}

