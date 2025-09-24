/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1291
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_2371
 *  net.minecraft.class_2561
 *  net.minecraft.class_2818
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_342
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_437
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5348
 *  net.minecraft.class_6880
 */
package xaero.common.misc;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import net.minecraft.class_1291;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_2561;
import net.minecraft.class_2818;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_342;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5348;
import net.minecraft.class_6880;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.IScreenBase;
import xaero.common.misc.IObfuscatedReflection;
import xaero.common.platform.Services;
import xaero.hud.controls.key.IKeyBindingHelper;
import xaero.hud.minimap.MinimapLogs;

public class Misc {
    public static double getMouseX(class_310 mc, boolean raw) {
        if (raw) {
            return mc.field_1729.method_1603();
        }
        return mc.field_1729.method_1603() * (double)mc.method_22683().method_4489() / (double)mc.method_22683().method_4480();
    }

    public static double getMouseY(class_310 mc, boolean raw) {
        if (raw) {
            return mc.field_1729.method_1604();
        }
        return mc.field_1729.method_1604() * (double)mc.method_22683().method_4506() / (double)mc.method_22683().method_4507();
    }

    public static void drawNormalText(class_4587 matrices, String name, float x, float y, int color, boolean shadow, class_4597.class_4598 renderTypeBuffer) {
        class_310.method_1551().field_1772.method_27521(name, x, y, color, shadow, matrices.method_23760().method_23761(), (class_4597)renderTypeBuffer, class_327.class_6415.field_33993, 0, 0xF000F0);
    }

    public static void drawNormalText(class_4587 matrices, class_2561 name, float x, float y, int color, boolean shadow, class_4597.class_4598 renderTypeBuffer) {
        class_310.method_1551().field_1772.method_27522(name, x, y, color, shadow, matrices.method_23760().method_23761(), (class_4597)renderTypeBuffer, class_327.class_6415.field_33993, 0, 0xF000F0);
    }

    public static void drawPiercingText(class_4587 matrices, String name, float x, float y, int color, boolean shadow, class_4597.class_4598 renderTypeBuffer) {
        class_310.method_1551().field_1772.method_27521(name, x, y, color, shadow, matrices.method_23760().method_23761(), (class_4597)renderTypeBuffer, class_327.class_6415.field_33994, 0, 0xF000F0);
    }

    public static void drawPiercingText(class_4587 matrices, class_2561 name, float x, float y, int color, boolean shadow, class_4597.class_4598 renderTypeBuffer) {
        class_310.method_1551().field_1772.method_27522(name, x, y, color, shadow, matrices.method_23760().method_23761(), (class_4597)renderTypeBuffer, class_327.class_6415.field_33994, 0, 0xF000F0);
    }

    public static void drawCenteredPiercingText(class_4587 matrices, String name, float x, float y, int color, boolean shadow, class_4597.class_4598 renderTypeBuffer) {
        Misc.drawPiercingText(matrices, name, x - (float)(class_310.method_1551().field_1772.method_1727(name) / 2), y, color, shadow, renderTypeBuffer);
    }

    public static void drawCenteredPiercingText(class_4587 matrices, class_2561 name, float x, float y, int color, boolean shadow, class_4597.class_4598 renderTypeBuffer) {
        Misc.drawPiercingText(matrices, name, x - (float)(class_310.method_1551().field_1772.method_27525((class_5348)name) / 2), y, color, shadow, renderTypeBuffer);
    }

    public static Path quickFileBackupMove(Path file) throws IOException {
        Path backupPath = null;
        int backupNumber = 0;
        while (Files.exists(backupPath = file.resolveSibling(file.getFileName().toString() + ".backup" + backupNumber), new LinkOption[0])) {
            ++backupNumber;
        }
        Files.move(file, backupPath, new CopyOption[0]);
        return backupPath;
    }

    public static void safeMoveAndReplace(Path from, Path to, boolean backupFrom) throws IOException {
        Path fromBackupPath;
        Path backupPath;
        block8: {
            backupPath = null;
            fromBackupPath = null;
            if (backupFrom) {
                while (true) {
                    try {
                        fromBackupPath = Misc.quickFileBackupMove(from);
                        break block8;
                    }
                    catch (IOException ioe2) {
                        try {
                            Thread.sleep(10L);
                        }
                        catch (InterruptedException interruptedException) {}
                        continue;
                    }
                    break;
                }
            }
            fromBackupPath = from;
        }
        if (Files.exists(to, new LinkOption[0])) {
            backupPath = Misc.quickFileBackupMove(to);
        }
        Files.move(fromBackupPath, to, new CopyOption[0]);
        if (backupPath != null) {
            Files.delete(backupPath);
        }
    }

    public static void deleteFile(Path file, int attempts) throws IOException {
        --attempts;
        try {
            Files.walkFileTree(file, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    Files.delete(path);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path path, IOException iOException) throws IOException {
                    if (iOException != null) {
                        throw iOException;
                    }
                    Files.delete(path);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            if (attempts > 0) {
                MinimapLogs.LOGGER.info("Failed to delete file/folder! Retrying... " + attempts);
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                Misc.deleteFile(file, attempts);
            }
            throw e;
        }
    }

    public static boolean inputMatchesKeyBinding(IXaeroMinimap modMain, class_3675.class_307 type, int code, class_304 kb, int keyConflictContext) {
        IKeyBindingHelper keyBindingHelper = Services.PLATFORM.getKeyBindingHelper();
        return kb != null && code != -1 && keyBindingHelper.getBoundKeyOf(kb).method_1442() == type && keyBindingHelper.getBoundKeyOf(kb).method_1444() == code && keyBindingHelper.modifiersAreActive(kb, keyConflictContext);
    }

    public static boolean screenShouldSkipWorldRender(IXaeroMinimap modMain, class_437 screen, boolean checkOtherMod) {
        return screen instanceof IScreenBase && ((IScreenBase)screen).shouldSkipWorldRender() || checkOtherMod && modMain.getSupportMods().worldmap() && modMain.getSupportMods().worldmapSupport.screenShouldSkipWorldRender(screen);
    }

    public static long getChunkPosAsLong(class_2818 chunk) {
        return chunk.method_12004().method_8324();
    }

    public static Class<?> getClassForName(String obfuscatedName, String deobfName) throws ClassNotFoundException {
        IObfuscatedReflection obfuscatedReflection = Services.PLATFORM.getObfuscatedReflection();
        return obfuscatedReflection.getClassForName(obfuscatedName, deobfName);
    }

    public static Field getFieldReflection(Class<?> c, String deobfName, String obfuscatedNameFabric, String descriptor, String obfuscatedNameForge) {
        IObfuscatedReflection obfuscatedReflection = Services.PLATFORM.getObfuscatedReflection();
        return obfuscatedReflection.getFieldReflection(c, deobfName, obfuscatedNameFabric, descriptor, obfuscatedNameForge);
    }

    public static <A, B> B getReflectFieldValue(A parentObject, Field field) {
        boolean accessibleBU = field.isAccessible();
        field.setAccessible(true);
        Object result = null;
        try {
            result = field.get(parentObject);
        }
        catch (Exception e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        field.setAccessible(accessibleBU);
        return (B)result;
    }

    public static <A, B> void setReflectFieldValue(A parentObject, Field field, B value) {
        boolean accessibleBU = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(parentObject, value);
        }
        catch (Exception e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        field.setAccessible(accessibleBU);
    }

    public static Method getMethodReflection(Class<?> c, String deobfName, String obfuscatedNameFabric, String descriptor, String obfuscatedNameForge, Class<?> ... parameters) {
        IObfuscatedReflection obfuscatedReflection = Services.PLATFORM.getObfuscatedReflection();
        return obfuscatedReflection.getMethodReflection(c, deobfName, obfuscatedNameFabric, descriptor, obfuscatedNameForge, parameters);
    }

    public static <A, B> B getReflectMethodValue(A parentObject, Method method, Object ... arguments) {
        boolean accessibleBU = method.isAccessible();
        method.setAccessible(true);
        Object result = null;
        try {
            result = method.invoke(parentObject, arguments);
        }
        catch (Exception e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        method.setAccessible(accessibleBU);
        return (B)result;
    }

    public static void download(BufferedOutputStream output, InputStream input) throws IOException {
        int read;
        byte[] buffer = new byte[256];
        while ((read = input.read(buffer, 0, buffer.length)) >= 0) {
            output.write(buffer, 0, read);
        }
        output.flush();
        input.close();
        output.close();
    }

    public static boolean hasItem(class_1657 player, class_1792 item) {
        int i;
        class_1661 inventory = player.method_31548();
        for (i = 0; i < 9; ++i) {
            if (inventory.method_5438(i).method_7909() != item) continue;
            return true;
        }
        for (i = 36; i < inventory.method_5439(); ++i) {
            if (inventory.method_5438(i).method_7909() != item) continue;
            return true;
        }
        return false;
    }

    public static boolean hasItem(class_2371<class_1799> inventory, int limit, class_1792 item) {
        for (int i = 0; i < inventory.size() && (limit == -1 || i < limit); ++i) {
            if (inventory.get(i) == null || ((class_1799)inventory.get(i)).method_7909() != item) continue;
            return true;
        }
        return false;
    }

    public static void setFieldText(class_342 field, String text) {
        Misc.setFieldText(field, text, -1);
    }

    public static void setFieldText(class_342 field, String text, int color) {
        field.method_1868(color);
        if (field.method_1882().equals(text)) {
            return;
        }
        field.method_1852(text);
    }

    public static class_2561 getFixedDisplayName(class_1297 e) {
        class_2561 baseName = e.method_5477();
        if (baseName == null) {
            return null;
        }
        return e.method_5781() == null ? baseName.method_27661() : e.method_5781().method_1198((class_2561)baseName.method_27661());
    }

    public static boolean hasEffect(class_1657 player, class_6880<class_1291> effect) {
        return effect != null && player.method_6059(effect);
    }

    public static boolean hasEffect(class_6880<class_1291> effect) {
        return Misc.hasEffect((class_1657)class_310.method_1551().field_1724, effect);
    }

    public static boolean isValidResourceLocationString(String resourceLocationString) {
        if (resourceLocationString.isEmpty()) {
            return false;
        }
        for (int i = 0; i < resourceLocationString.length(); ++i) {
            if (class_2960.method_12831((char)resourceLocationString.charAt(i))) continue;
            return false;
        }
        return true;
    }
}

