/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.AddressMode
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 */
package xaero.map.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import xaero.map.WorldMap;
import xaero.map.icon.XaeroIcon;

public final class XaeroIconAtlas {
    private final GpuTexture textureId;
    private final GpuTextureView textureView;
    private final int width;
    private int currentIndex;
    private final int iconWidth;
    private final int sideIconCount;
    private final int maxIconCount;

    private XaeroIconAtlas(GpuTexture textureId, GpuTextureView textureView, int width, int iconWidth) {
        this.textureId = textureId;
        this.textureView = textureView;
        this.width = width;
        this.iconWidth = iconWidth;
        this.sideIconCount = width / iconWidth;
        this.maxIconCount = this.sideIconCount * this.sideIconCount;
    }

    public GpuTexture getTextureId() {
        return this.textureId;
    }

    public GpuTextureView getTextureView() {
        return this.textureView;
    }

    public void close() {
        this.textureView.close();
        this.textureId.close();
    }

    public int getWidth() {
        return this.width;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public boolean isFull() {
        return this.currentIndex >= this.maxIconCount;
    }

    public XaeroIcon createIcon() {
        if (!this.isFull()) {
            int offsetX = this.currentIndex % this.sideIconCount * this.iconWidth;
            int offsetY = this.currentIndex / this.sideIconCount * this.iconWidth;
            ++this.currentIndex;
            return new XaeroIcon(this, offsetX, offsetY);
        }
        return null;
    }

    public static class Builder {
        private int width;
        private GpuTexture preparedTexture;
        private int iconWidth;

        private Builder() {
        }

        public Builder setDefault() {
            this.setIconWidth(64);
            return this;
        }

        public Builder setPreparedTexture(GpuTexture preparedTexture) {
            this.preparedTexture = preparedTexture;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setIconWidth(int iconWidth) {
            this.iconWidth = iconWidth;
            return this;
        }

        private GpuTexture createGlTexture(int actualWidth) {
            GpuTexture texture = RenderSystem.getDevice().createTexture((String)null, 15, TextureFormat.RGBA8, actualWidth, actualWidth, 1, 1);
            if (texture == null) {
                return null;
            }
            texture.setTextureFilter(FilterMode.LINEAR, false);
            texture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(texture, 0);
            return texture;
        }

        public XaeroIconAtlas build() {
            GpuTexture texture;
            if (this.width == 0 || this.iconWidth <= 0) {
                throw new IllegalStateException();
            }
            if (this.width / this.iconWidth * this.iconWidth != this.width) {
                throw new IllegalArgumentException();
            }
            GpuTexture gpuTexture = texture = this.preparedTexture == null ? this.createGlTexture(this.width) : this.preparedTexture;
            if (texture == null) {
                WorldMap.LOGGER.error("Failed to create a GL texture for a new xaero icon atlas!");
                return null;
            }
            GpuTextureView textureView = RenderSystem.getDevice().createTextureView(texture);
            return new XaeroIconAtlas(texture, textureView, this.width, this.iconWidth);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

