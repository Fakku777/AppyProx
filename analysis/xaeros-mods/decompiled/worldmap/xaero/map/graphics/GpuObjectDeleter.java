/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 */
package xaero.map.graphics;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.util.ArrayList;
import xaero.map.graphics.GpuTextureAndView;
import xaero.map.graphics.OpenGlHelper;

public class GpuObjectDeleter {
    private static final int DELETE_PER_FRAME = 5;
    private ArrayList<GpuTextureAndView> texturesToDelete = new ArrayList();
    private ArrayList<GpuBuffer> buffersToDelete = new ArrayList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void work() {
        ArrayList<GpuTextureAndView> arrayList;
        if (!this.texturesToDelete.isEmpty()) {
            do {
                arrayList = this.texturesToDelete;
                synchronized (arrayList) {
                    OpenGlHelper.deleteTextures(this.texturesToDelete, 5);
                }
            } while (this.texturesToDelete.size() > 640);
        }
        if (!this.buffersToDelete.isEmpty()) {
            do {
                arrayList = this.buffersToDelete;
                synchronized (arrayList) {
                    OpenGlHelper.deleteBuffers(this.buffersToDelete, 5);
                }
            } while (this.buffersToDelete.size() > 640);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void requestTextureDeletion(GpuTextureAndView texture) {
        ArrayList<GpuTextureAndView> arrayList = this.texturesToDelete;
        synchronized (arrayList) {
            this.texturesToDelete.add(texture);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void requestBufferToDelete(GpuBuffer bufferId) {
        ArrayList<GpuBuffer> arrayList = this.buffersToDelete;
        synchronized (arrayList) {
            this.buffersToDelete.add(bufferId);
        }
    }
}

