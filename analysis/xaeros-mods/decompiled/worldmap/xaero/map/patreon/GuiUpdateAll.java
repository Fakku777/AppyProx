/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_156
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_364
 *  net.minecraft.class_410
 *  net.minecraft.class_4185
 *  net.minecraft.class_8021
 *  net.minecraft.class_8667
 *  org.apache.commons.codec.binary.Hex
 */
package xaero.map.patreon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_8021;
import net.minecraft.class_8667;
import org.apache.commons.codec.binary.Hex;
import xaero.map.WorldMap;
import xaero.map.patreon.Patreon;
import xaero.map.patreon.PatreonMod;
import xaero.map.platform.Services;

public class GuiUpdateAll
extends class_410 {
    public GuiUpdateAll() {
        super(GuiUpdateAll::confirmResult, (class_2561)class_2561.method_43470((String)("These mods are out-of-date: " + GuiUpdateAll.modListToNames(Patreon.getOutdatedMods()))), (class_2561)class_2561.method_43470((String)(Patreon.getHasAutoUpdates() ? "Would you like to automatically update them?" : "Would you like to update them (open the mod pages)?")));
        Patreon.setNotificationDisplayed(true);
    }

    private static String modListToNames(List<Object> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(((PatreonMod)list.get((int)i)).modName);
        }
        return builder.toString();
    }

    protected void method_37051(class_8667 $$0) {
        super.method_37051($$0);
        if (Patreon.getHasAutoUpdates()) {
            this.field_61001.method_52736((class_8021)class_4185.method_46430((class_2561)class_2561.method_43469((String)"Changelogs", (Object[])new Object[0]), b -> {
                for (int i = 0; i < Patreon.getOutdatedMods().size(); ++i) {
                    PatreonMod mod = (PatreonMod)Patreon.getOutdatedMods().get(i);
                    try {
                        class_156.method_668().method_673(new URI(mod.changelogLink));
                        continue;
                    }
                    catch (URISyntaxException e) {
                        WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                    }
                }
            }).method_46434(this.field_22789 / 2 - 100, this.field_22790 / 6 + 120, 200, 20).method_46431());
        }
        this.field_61001.method_52736((class_8021)class_4185.method_46430((class_2561)class_2561.method_43469((String)"Don't show again for these updates", (Object[])new Object[0]), b -> {
            for (int i = 0; i < Patreon.getOutdatedMods().size(); ++i) {
                PatreonMod mod = (PatreonMod)Patreon.getOutdatedMods().get(i);
                if (mod.onVersionIgnore == null) continue;
                mod.onVersionIgnore.run();
            }
            this.field_22787.method_1507(null);
        }).method_46434(this.field_22789 / 2 - 100, this.field_22790 / 6 + 144, 200, 20).method_46431());
    }

    private static void confirmResult(boolean p_confirmResult_1_) {
        if (p_confirmResult_1_) {
            boolean shouldExit = false;
            if (Patreon.getHasAutoUpdates()) {
                for (class_364 b : class_310.method_1551().field_1755.method_25396()) {
                    if (!(b instanceof class_4185)) continue;
                    ((class_4185)b).field_22763 = false;
                }
                shouldExit = GuiUpdateAll.autoUpdate();
            } else {
                shouldExit = true;
                for (int i = 0; i < Patreon.getOutdatedMods().size(); ++i) {
                    PatreonMod m = (PatreonMod)Patreon.getOutdatedMods().get(i);
                    try {
                        class_156.method_668().method_673(new URI(m.changelogLink));
                        if (m.modJar == null) continue;
                        class_156.method_668().method_672(m.modJar.getParentFile());
                        continue;
                    }
                    catch (Exception e) {
                        WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                        shouldExit = false;
                    }
                }
            }
            if (shouldExit) {
                class_310.method_1551().method_1592();
            } else {
                class_310.method_1551().method_1507(null);
            }
            class_310.method_1551().method_1592();
        } else {
            class_310.method_1551().method_1507(null);
        }
    }

    private static void download(BufferedOutputStream output, InputStream input, boolean closeInput) throws IOException {
        int read;
        byte[] buffer = new byte[256];
        while ((read = input.read(buffer, 0, buffer.length)) >= 0) {
            output.write(buffer, 0, read);
        }
        output.flush();
        if (closeInput) {
            input.close();
        }
        output.close();
    }

    private static boolean autoUpdate() {
        try {
            MessageDigest digestMD5;
            try {
                digestMD5 = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException e1) {
                WorldMap.LOGGER.info("No algorithm for MD5.");
                return false;
            }
            PatreonMod autoupdater = (PatreonMod)Patreon.getMods().get("autoupdater30");
            String jarLink = autoupdater.changelogLink;
            String jarMD5 = autoupdater.latestVersionLayout;
            URL url = new URL(jarLink);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(900);
            conn.setConnectTimeout(900);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
            if (conn.getContentLengthLong() > 0x200000L) {
                throw new IOException("Input too long to trust!");
            }
            InputStream input = conn.getInputStream();
            input = new BufferedInputStream(input);
            DigestInputStream digestInput = new DigestInputStream(input, digestMD5);
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(Services.PLATFORM.getGameDir().resolve("xaero_autoupdater.jar").toFile()));
            GuiUpdateAll.download(output, digestInput, true);
            byte[] digest = digestMD5.digest();
            String fileMD5 = Hex.encodeHexString((byte[])digest);
            if (!jarMD5.equals(fileMD5)) {
                WorldMap.LOGGER.info("Invalid autoupdater MD5: " + fileMD5);
                return false;
            }
            ArrayList<String> command = new ArrayList<String>();
            Path javaPath = new File(System.getProperty("java.home")).toPath().resolve("bin").resolve("java");
            command.add(javaPath.toString());
            command.add("-jar");
            command.add("./xaero_autoupdater.jar");
            command.add("6");
            command.add(Patreon.getUpdateLocation());
            for (int i = 0; i < Patreon.getOutdatedMods().size(); ++i) {
                PatreonMod m = (PatreonMod)Patreon.getOutdatedMods().get(i);
                if (m.modJar == null) continue;
                int canonicalPathAttempts = 10;
                String jarPath = null;
                while (canonicalPathAttempts-- > 0) {
                    try {
                        jarPath = m.modJar.getCanonicalPath();
                        break;
                    }
                    catch (IOException ioe) {
                        WorldMap.LOGGER.info("IO exception fetching the canonical path to the mod jar!");
                        if (canonicalPathAttempts == 0) {
                            throw ioe;
                        }
                        WorldMap.LOGGER.error("suppressed exception", (Throwable)ioe);
                        WorldMap.LOGGER.info("Retrying... (" + canonicalPathAttempts + ")");
                        try {
                            Thread.sleep(25L);
                        }
                        catch (InterruptedException interruptedException) {}
                    }
                }
                command.add(jarPath);
                command.add(m.latestVersionLayout);
                command.add(m.currentVersion.split("_")[1]);
                command.add(m.latestVersion);
                command.add(m.currentVersion.split("_")[0]);
                command.add(m.md5 == null ? "null" : m.md5);
            }
            WorldMap.LOGGER.info(String.join((CharSequence)", ", command));
            Runtime.getRuntime().exec(command.toArray(new String[0]));
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
            return false;
        }
        return true;
    }
}

