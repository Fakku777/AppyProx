/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL15
 */
package xaero.map.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL15;
import xaero.map.WorldMap;

public class PixelBuffers {
    private static int buffersType = 0;

    private static int innerGenBuffers() {
        switch (buffersType) {
            case 0: {
                return GL15.glGenBuffers();
            }
        }
        return 0;
    }

    public static int glGenBuffers() {
        int result;
        int attempts = 5;
        do {
            result = PixelBuffers.innerGenBuffers();
        } while (--attempts > 0 && result == 0);
        if (result == 0) {
            WorldMap.LOGGER.error("Failed to generate a PBO after multiple attempts. Likely caused by previous errors from other mods.");
        }
        return result;
    }

    public static void glBindBuffer(int target, int buffer) {
        switch (buffersType) {
            case 0: {
                GL15.glBindBuffer((int)target, (int)buffer);
            }
        }
    }

    public static void glBufferData(int target, long size, int usage) {
        switch (buffersType) {
            case 0: {
                GL15.glBufferData((int)target, (long)size, (int)usage);
            }
        }
    }

    public static ByteBuffer glMapBuffer(int target, int access, long length, ByteBuffer old_buffer) {
        switch (buffersType) {
            case 0: {
                return GL15.glMapBuffer((int)target, (int)access, (long)length, (ByteBuffer)old_buffer);
            }
        }
        return null;
    }

    public static boolean glUnmapBuffer(int target) {
        switch (buffersType) {
            case 0: {
                return GL15.glUnmapBuffer((int)target);
            }
        }
        return false;
    }

    public static void glDeleteBuffers(int buffer) {
        switch (buffersType) {
            case 0: {
                GL15.glDeleteBuffers((int)buffer);
            }
        }
    }

    public static void glDeleteBuffers(IntBuffer buffers) {
        switch (buffersType) {
            case 0: {
                GL15.glDeleteBuffers((IntBuffer)buffers);
            }
        }
    }

    public static ByteBuffer glMapBuffer(int target, int access) {
        switch (buffersType) {
            case 0: {
                return GL15.glMapBuffer((int)target, (int)access);
            }
        }
        return null;
    }
}

