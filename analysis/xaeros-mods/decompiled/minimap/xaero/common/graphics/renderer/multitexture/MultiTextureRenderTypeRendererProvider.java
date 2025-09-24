/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.minecraft.class_10865
 *  net.minecraft.class_10868
 *  net.minecraft.class_1921
 */
package xaero.common.graphics.renderer.multitexture;

import com.mojang.blaze3d.textures.GpuTexture;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.function.Consumer;
import net.minecraft.class_10865;
import net.minecraft.class_10868;
import net.minecraft.class_1921;
import xaero.common.graphics.OpenGlHelper;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.common.platform.Services;
import xaero.hud.render.util.IPlatformRenderDeviceUtil;

public class MultiTextureRenderTypeRendererProvider {
    private Deque<MultiTextureRenderTypeRenderer> availableRenderers = new ArrayDeque<MultiTextureRenderTypeRenderer>();
    private HashSet<MultiTextureRenderTypeRenderer> usedRenderers;

    public MultiTextureRenderTypeRendererProvider(int rendererCount) {
        for (int i = 0; i < rendererCount; ++i) {
            this.availableRenderers.add(new MultiTextureRenderTypeRenderer());
        }
        this.usedRenderers = new HashSet();
    }

    public MultiTextureRenderTypeRenderer getRenderer(Consumer<GpuTexture> textureBinder, class_1921 renderType) {
        return this.getRenderer(textureBinder, null, renderType);
    }

    public MultiTextureRenderTypeRenderer getRenderer(Consumer<GpuTexture> textureBinder, Consumer<GpuTexture> textureFinalizer, class_1921 renderType) {
        if (this.availableRenderers.isEmpty()) {
            throw new RuntimeException("No renderers available!");
        }
        MultiTextureRenderTypeRenderer renderer = this.availableRenderers.removeFirst();
        renderer.init(textureBinder, textureFinalizer, renderType);
        this.usedRenderers.add(renderer);
        return renderer;
    }

    public void draw(MultiTextureRenderTypeRenderer renderer) {
        if (!this.usedRenderers.remove(renderer)) {
            throw new RuntimeException("The renderer requested for drawing was not provided by this provider!");
        }
        renderer.draw();
        this.availableRenderers.add(renderer);
    }

    public static void defaultTextureBind(GpuTexture texture) {
        IPlatformRenderDeviceUtil renderDeviceUtil = Services.PLATFORM.getPlatformRenderDeviceUtil();
        if (!(renderDeviceUtil.getRealDevice() instanceof class_10865)) {
            throw new IllegalStateException("Unsupported non-OpenGL rendering detected!");
        }
        GpuTexture realTexture = renderDeviceUtil.getRealTexture(texture);
        OpenGlHelper.bindTexture(0, realTexture);
        if (realTexture != null) {
            ((class_10868)realTexture).method_68424(3553);
        }
    }
}

