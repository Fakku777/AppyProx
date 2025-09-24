/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.class_10366
 *  net.minecraft.class_1041
 *  net.minecraft.class_11278
 *  net.minecraft.class_1291
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2371
 *  net.minecraft.class_2561
 *  net.minecraft.class_268
 *  net.minecraft.class_2680
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
 *  org.lwjgl.opengl.GL11
 */
package xaero.map.misc;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_10366;
import net.minecraft.class_1041;
import net.minecraft.class_11278;
import net.minecraft.class_1291;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2371;
import net.minecraft.class_2561;
import net.minecraft.class_268;
import net.minecraft.class_2680;
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
import org.lwjgl.opengl.GL11;
import xaero.map.WorldMap;
import xaero.map.controls.IKeyBindingHelper;
import xaero.map.core.IGameRenderer;
import xaero.map.core.IGuiRenderer;
import xaero.map.gui.IScreenBase;
import xaero.map.mods.SupportMods;
import xaero.map.platform.Services;

public class Misc {
    private static final long[] ZERO_LONG_1024 = new long[1024];
    private static long cpuTimerPreTime;
    private static long glTimerPreTime;
    public static final String OUTDATED_FILE_EXT = ".outdated";

    public static int myFloor(double d) {
        int asInt = (int)d;
        if ((double)asInt != d && d < 0.0) {
            --asInt;
        }
        return asInt;
    }

    public static double round(double a, int komaarvu) {
        double x = Math.pow(10.0, komaarvu);
        return (double)Math.round(a * x) / x;
    }

    public static class_2680 getStateById(int id) {
        try {
            return class_2248.method_9531((int)id);
        }
        catch (Exception e) {
            return Misc.getDefaultBlockStateForStateId(id);
        }
    }

    private static class_2680 getDefaultBlockStateForStateId(int id) {
        try {
            return class_2248.method_9531((int)id).method_26204().method_9564();
        }
        catch (Exception e) {
            return class_2246.field_10124.method_9564();
        }
    }

    public static void glTimerPre() {
        GL11.glFinish();
        glTimerPreTime = System.nanoTime();
    }

    public static int glTimerResult() {
        GL11.glFinish();
        return (int)(System.nanoTime() - glTimerPreTime);
    }

    public static void timerPre() {
        cpuTimerPreTime = System.nanoTime();
    }

    public static int timerResult() {
        return (int)(System.nanoTime() - cpuTimerPreTime);
    }

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

    public static void minecraftOrtho(class_310 mc, boolean raw) {
        class_1041 mainwindow = mc.method_22683();
        int width = raw ? mc.method_22683().method_4480() : mainwindow.method_4489();
        int height = raw ? mc.method_22683().method_4507() : mainwindow.method_4506();
        class_11278 guiProjectionCache = ((IGuiRenderer)((IGameRenderer)mc.field_1773).xaero_wm_getGuiRenderer()).xaero_wm_getGuiProjectionMatrixBuffer();
        GpuBufferSlice guiProjection = guiProjectionCache.method_71092((float)width / (float)mainwindow.method_4495(), (float)height / (float)mainwindow.method_4495());
        RenderSystem.setProjectionMatrix((GpuBufferSlice)guiProjection, (class_10366)class_10366.field_54954);
    }

    public static void clearHeightsData1024(long[] data) {
        System.arraycopy(ZERO_LONG_1024, 0, data, 0, 1024);
    }

    public static <T extends Comparable<? super T>> void addToListOfSmallest(int maxSize, List<T> list, T element) {
        int currentSize = list.size();
        if (currentSize == maxSize && ((Comparable)list.get(currentSize - 1)).compareTo(element) <= 0) {
            return;
        }
        int iterLimit = currentSize == maxSize ? maxSize : currentSize + 1;
        for (int i = 0; i < iterLimit; ++i) {
            if (i != currentSize && element.compareTo(list.get(i)) >= 0) continue;
            list.add(i, element);
            if (currentSize != maxSize) break;
            list.remove(currentSize);
            break;
        }
    }

    public static String getKeyName(class_304 kb) {
        if (kb == null || Services.PLATFORM.getKeyBindingHelper().getBoundKeyOf(kb).method_1444() == -1) {
            return "(unset)";
        }
        return kb.method_16007().getString().toUpperCase();
    }

    public static boolean inputMatchesKeyBinding(class_3675.class_307 type, int code, class_304 kb, int keyConflictContext) {
        IKeyBindingHelper keyBindingHelper = Services.PLATFORM.getKeyBindingHelper();
        return kb != null && code != -1 && keyBindingHelper.getBoundKeyOf(kb).method_1442() == type && keyBindingHelper.getBoundKeyOf(kb).method_1444() == code && keyBindingHelper.modifiersAreActive(kb, keyConflictContext);
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
        Misc.deleteFileIf(file, path -> true, attempts);
    }

    public static void deleteFileIf(Path file, final Predicate<Path> condition, int attempts) throws IOException {
        --attempts;
        try {
            Files.walkFileTree(file, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    if (condition.test(path)) {
                        Files.delete(path);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path path, IOException iOException) throws IOException {
                    if (iOException != null) {
                        throw iOException;
                    }
                    if (condition.test(path)) {
                        Files.delete(path);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            if (attempts > 0) {
                WorldMap.LOGGER.info("Failed to delete file/folder! Retrying... " + attempts);
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                Misc.deleteFileIf(file, condition, attempts);
            }
            throw e;
        }
    }

    public static Path convertToOutdated(Path path, int attempts) throws IOException {
        if (path.getFileName().toString().endsWith(OUTDATED_FILE_EXT)) {
            return path;
        }
        Path outdatedPath = path.resolveSibling(path.getFileName().toString() + OUTDATED_FILE_EXT);
        if (Files.exists(path, new LinkOption[0])) {
            Misc.convertToOutdated(path, outdatedPath, attempts);
        }
        return outdatedPath;
    }

    private static void convertToOutdated(Path path, Path outdatedPath, int attempts) throws IOException {
        --attempts;
        try {
            Files.move(path, outdatedPath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            if (attempts > 0) {
                WorldMap.LOGGER.info("Failed to convert file to outdated! Retrying... " + attempts);
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                Misc.convertToOutdated(path, outdatedPath, attempts);
            }
            throw e;
        }
    }

    public static boolean screenShouldSkipWorldRender(class_437 screen, boolean checkOtherMod) {
        return screen instanceof IScreenBase && ((IScreenBase)screen).shouldSkipWorldRender() || checkOtherMod && SupportMods.minimap() && SupportMods.xaeroMinimap.screenShouldSkipWorldRender(screen);
    }

    public static void drawNormalText(class_4587 matrices, String name, float x, float y, int color, boolean shadow, class_4597.class_4598 renderTypeBuffer) {
        class_310.method_1551().field_1772.method_27521(name, x, y, color, shadow, matrices.method_23760().method_23761(), (class_4597)renderTypeBuffer, class_327.class_6415.field_33993, 0, 0xF000F0);
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

    public static int getTeamColour(class_1297 e) {
        Integer teamColour = null;
        class_268 team = e.method_5781();
        if (team != null) {
            teamColour = team.method_1202().method_532();
        }
        return teamColour == null ? -1 : teamColour;
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

    public static Class<?> getClassForName(String obfuscatedName, String deobfName) throws ClassNotFoundException {
        return Services.PLATFORM.getObfuscatedFieldReflection().getClassForName(obfuscatedName, deobfName);
    }

    public static Field getFieldReflection(Class<?> c, String deobfName, String obfuscatedNameFabric, String descriptor, String obfuscatedNameForge) {
        return Services.PLATFORM.getObfuscatedFieldReflection().getFieldReflection(c, deobfName, obfuscatedNameFabric, descriptor, obfuscatedNameForge);
    }

    public static <A, B> B getReflectFieldValue(A parentObject, Field field) {
        boolean accessibleBU = field.isAccessible();
        field.setAccessible(true);
        Object result = null;
        try {
            result = field.get(parentObject);
        }
        catch (Exception e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
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
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        field.setAccessible(accessibleBU);
    }

    public static Method getMethodReflection(Class<?> c, String deobfName, String obfuscatedNameFabric, String descriptor, String obfuscatedNameForge, Class<?> ... parameters) {
        return Services.PLATFORM.getObfuscatedFieldReflection().getMethodReflection(c, deobfName, obfuscatedNameFabric, descriptor, obfuscatedNameForge, parameters);
    }

    public static <A, B> B getReflectMethodValue(A parentObject, Method method, Object ... arguments) {
        boolean accessibleBU = method.isAccessible();
        method.setAccessible(true);
        Object result = null;
        try {
            result = method.invoke(parentObject, arguments);
        }
        catch (Exception e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        method.setAccessible(accessibleBU);
        return (B)result;
    }

    public static boolean hasEffect(class_1657 player, class_6880<class_1291> effect) {
        return effect != null && player.method_6059(effect);
    }

    public static boolean hasEffect(class_6880<class_1291> effect) {
        return Misc.hasEffect((class_1657)class_310.method_1551().field_1724, effect);
    }
}

