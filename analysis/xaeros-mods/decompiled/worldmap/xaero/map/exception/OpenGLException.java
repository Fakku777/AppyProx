/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package xaero.map.exception;

import org.lwjgl.opengl.GL11;
import xaero.map.WorldMap;
import xaero.map.graphics.OpenGlHelper;

public class OpenGLException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public OpenGLException(int error) {
        super("OpenGL error: " + error);
    }

    public static void checkGLError() throws OpenGLException {
        OpenGLException.checkGLError(true, null);
    }

    public static void checkGLError(boolean crash, String where) throws OpenGLException {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        int error = GL11.glGetError();
        if (error != 0) {
            if (crash) {
                throw new OpenGLException(error);
            }
            WorldMap.LOGGER.warn("Ignoring OpenGL error " + error + " when " + where + ". Most likely caused by another mod.");
        }
    }
}

