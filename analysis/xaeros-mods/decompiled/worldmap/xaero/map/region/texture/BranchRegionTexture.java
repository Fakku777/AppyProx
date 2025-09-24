/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.minecraft.class_1959
 *  net.minecraft.class_5321
 */
package xaero.map.region.texture;

import com.mojang.blaze3d.textures.TextureFormat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.class_1959;
import net.minecraft.class_5321;
import xaero.map.MapProcessor;
import xaero.map.biome.BlockTintProvider;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.exception.OpenGLException;
import xaero.map.graphics.GpuTextureAndView;
import xaero.map.graphics.TextureUploader;
import xaero.map.highlight.DimensionHighlighterHandler;
import xaero.map.region.BranchLeveledRegion;
import xaero.map.region.LeveledRegion;
import xaero.map.region.OverlayManager;
import xaero.map.region.texture.BranchTextureRenderer;
import xaero.map.region.texture.RegionTexture;

public class BranchRegionTexture
extends RegionTexture<BranchRegionTexture> {
    private boolean updating;
    private boolean colorAllocationRequested;
    private ChildTextureInfo topLeftInfo;
    private ChildTextureInfo topRightInfo;
    private ChildTextureInfo bottomLeftInfo;
    private ChildTextureInfo bottomRightInfo;
    private LeveledRegion<?> branchUpdateChildRegion;
    private boolean checkForUpdatesAfterDownload;

    public BranchRegionTexture(LeveledRegion<BranchRegionTexture> region) {
        super(region);
        this.reset();
    }

    private void reset() {
        this.updating = false;
        this.colorAllocationRequested = false;
        this.topLeftInfo = new ChildTextureInfo(this);
        this.topRightInfo = new ChildTextureInfo(this);
        this.bottomLeftInfo = new ChildTextureInfo(this);
        this.bottomRightInfo = new ChildTextureInfo(this);
        this.checkForUpdatesAfterDownload = false;
    }

    public boolean checkForUpdates(RegionTexture<?> topLeft, RegionTexture<?> topRight, RegionTexture<?> bottomLeft, RegionTexture<?> bottomRight, LeveledRegion<?> childRegion) {
        boolean needsUpdating = false;
        if (topLeft != null && topLeft.glColorTexture == null || topRight != null && topRight.glColorTexture == null || bottomLeft != null && bottomLeft.glColorTexture == null || bottomRight != null && bottomRight.glColorTexture == null) {
            return false;
        }
        needsUpdating = needsUpdating || this.isChildUpdated(this.topLeftInfo, topLeft, childRegion);
        needsUpdating = needsUpdating || this.isChildUpdated(this.topRightInfo, topRight, childRegion);
        needsUpdating = needsUpdating || this.isChildUpdated(this.bottomLeftInfo, bottomLeft, childRegion);
        boolean bl = needsUpdating = needsUpdating || this.isChildUpdated(this.bottomRightInfo, bottomRight, childRegion);
        if (needsUpdating) {
            if (this.toUpload) {
                if (this.shouldDownloadFromPBO) {
                    this.checkForUpdatesAfterDownload = true;
                    return false;
                }
                if (this.topLeftInfo.temporaryReference == topLeft && this.topRightInfo.temporaryReference == topRight && this.bottomLeftInfo.temporaryReference == bottomLeft && this.bottomRightInfo.temporaryReference == bottomRight) {
                    return false;
                }
            } else {
                ++childRegion.activeBranchUpdateReferences;
            }
            this.setCachePrepared(false);
            this.region.setAllCachePrepared(false);
            this.colorBufferFormat = null;
            this.toUpload = true;
            this.updating = true;
            this.topLeftInfo.temporaryReference = topLeft;
            this.topRightInfo.temporaryReference = topRight;
            this.bottomLeftInfo.temporaryReference = bottomLeft;
            this.bottomRightInfo.temporaryReference = bottomRight;
            this.branchUpdateChildRegion = childRegion;
        }
        return needsUpdating;
    }

    private boolean isChildUpdated(ChildTextureInfo info, RegionTexture<?> texture, LeveledRegion<?> region) {
        if (region.isLoaded()) {
            if (texture == null && info.usedTextureVersion != 0) {
                return true;
            }
            if (texture != null && texture.glColorTexture != null && texture.shouldBeUsedForBranchUpdate(info.usedTextureVersion)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void preUpload(MapProcessor mapProcessor, BlockTintProvider blockTintProvider, OverlayManager overlayManager, LeveledRegion<BranchRegionTexture> region, boolean detailedDebug, BlockStateShortShapeCache blockStateShortShapeCache) {
    }

    @Override
    public void postUpload(MapProcessor mapProcessor, LeveledRegion<BranchRegionTexture> leveledRegion, boolean cleanAndCacheRequestsBlocked) {
    }

    @Override
    public long uploadBuffer(DimensionHighlighterHandler highlighterHandler, TextureUploader textureUploader, LeveledRegion<BranchRegionTexture> inRegion, BranchTextureRenderer branchTextureRenderer, int x, int y) throws OpenGLException, IllegalArgumentException, IllegalAccessException {
        return super.uploadBuffer(highlighterHandler, textureUploader, inRegion, branchTextureRenderer, x, y);
    }

    private void copyNonColorData(RegionTexture<?> childTexture, int offX, int offZ) {
        boolean resetting = childTexture == null;
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                int childHeight = resetting ? Short.MAX_VALUE : childTexture.getHeight(i << 1, j << 1);
                int childTopHeight = resetting ? Short.MAX_VALUE : childTexture.getTopHeight(i << 1, j << 1);
                class_5321<class_1959> childBiome = resetting ? null : childTexture.getBiome(i << 1, j << 1);
                int destX = offX | i;
                int destZ = offZ | j;
                if (childHeight != Short.MAX_VALUE) {
                    this.putHeight(destX, destZ, childHeight);
                } else {
                    this.removeHeight(destX, destZ);
                }
                if (childTopHeight != Short.MAX_VALUE) {
                    this.putTopHeight(destX, destZ, childTopHeight);
                } else {
                    this.removeTopHeight(destX, destZ);
                }
                this.setBiome(destX, destZ, childBiome);
            }
        }
    }

    @Override
    protected long uploadNonCache(DimensionHighlighterHandler highlighterHandler, TextureUploader textureUploader, BranchTextureRenderer renderer) {
        this.timer = 5;
        this.prepareBuffer();
        this.shouldDownloadFromPBO = true;
        if (this.updating) {
            this.ensurePackPBO();
            this.unbindPackPBO();
            this.bindColorTexture(true);
            OpenGLException.checkGLError();
            ChildTextureInfo topLeftInfo = this.topLeftInfo;
            ChildTextureInfo topRightInfo = this.topRightInfo;
            ChildTextureInfo bottomLeftInfo = this.bottomLeftInfo;
            ChildTextureInfo bottomRightInfo = this.bottomRightInfo;
            GpuTextureAndView emptyTexture = renderer.getEmptyTexture();
            GpuTextureAndView topLeftColor = topLeftInfo.getColorTextureForUpdate(emptyTexture);
            GpuTextureAndView topRightColor = topRightInfo.getColorTextureForUpdate(emptyTexture);
            GpuTextureAndView bottomLeftColor = bottomLeftInfo.getColorTextureForUpdate(emptyTexture);
            GpuTextureAndView bottomRightColor = bottomRightInfo.getColorTextureForUpdate(emptyTexture);
            long estimatedTime = textureUploader.requestBranchUpdate(!this.colorAllocationRequested, this.glColorTexture, this.unpackPbo[0], 0, DEFAULT_INTERNAL_FORMAT, 64, 64, 0, 0L, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor, renderer, this.packPbo, 0L);
            if (topLeftColor != null) {
                this.copyNonColorData(topLeftInfo.temporaryReference, 0, 0);
            }
            if (topRightColor != null) {
                this.copyNonColorData(topRightInfo.temporaryReference, 32, 0);
            }
            if (bottomLeftColor != null) {
                this.copyNonColorData(bottomLeftInfo.temporaryReference, 0, 32);
            }
            if (bottomRightColor != null) {
                this.copyNonColorData(bottomRightInfo.temporaryReference, 32, 32);
            }
            int textureVersionSum = 0;
            int topLeftVersion = topLeftInfo.getTextureVersion();
            textureVersionSum += topLeftVersion;
            int topRightVersion = topRightInfo.getTextureVersion();
            textureVersionSum += topRightVersion;
            int bottomLeftVersion = bottomLeftInfo.getTextureVersion();
            textureVersionSum += bottomLeftVersion;
            int bottomRightVersion = bottomRightInfo.getTextureVersion();
            this.updateTextureVersion(textureVersionSum += bottomRightVersion);
            this.colorAllocationRequested = true;
            this.textureHasLight = topLeftInfo.hasLight() || topRightInfo.hasLight() || bottomLeftInfo.hasLight() || bottomRightInfo.hasLight();
            --this.branchUpdateChildRegion.activeBranchUpdateReferences;
            this.branchUpdateChildRegion = null;
            topLeftInfo.onUpdate(topLeftVersion);
            topRightInfo.onUpdate(topRightVersion);
            bottomLeftInfo.onUpdate(bottomLeftVersion);
            bottomRightInfo.onUpdate(bottomRightVersion);
            BranchLeveledRegion branchRegion = (BranchLeveledRegion)this.region;
            branchRegion.postTextureUpdate();
            return estimatedTime;
        }
        this.ensurePackPBO();
        this.unbindPackPBO();
        return textureUploader.requestBranchDownload(this.glColorTexture, this.packPbo, 0L);
    }

    @Override
    protected void onCacheUploadRequested() {
        super.onCacheUploadRequested();
        this.colorAllocationRequested = true;
    }

    @Override
    protected void onDownloadedBuffer(ByteBuffer mappedPBO) {
        super.onDownloadedBuffer(mappedPBO);
        if (this.checkForUpdatesAfterDownload) {
            ((BranchLeveledRegion)this.region).setShouldCheckForUpdatesRecursive(true);
            this.checkForUpdatesAfterDownload = false;
        }
    }

    @Override
    protected void endPBODownload(TextureFormat format, boolean success) {
        if (!success) {
            --this.topLeftInfo.usedTextureVersion;
            --this.topRightInfo.usedTextureVersion;
            --this.bottomLeftInfo.usedTextureVersion;
            --this.bottomRightInfo.usedTextureVersion;
            this.updateTextureVersion(this.topLeftInfo.usedTextureVersion + this.topRightInfo.usedTextureVersion + this.bottomLeftInfo.usedTextureVersion + this.bottomRightInfo.usedTextureVersion);
        }
        super.endPBODownload(format, success);
    }

    @Override
    public boolean hasSourceData() {
        return false;
    }

    @Override
    public void addDebugLines(List<String> lines) {
        super.addDebugLines(lines);
        lines.add("updating: " + this.updating);
        lines.add("colorAllocationRequested: " + this.colorAllocationRequested);
        lines.add("topLeftInfo: " + String.valueOf(this.topLeftInfo));
        lines.add("topRightInfo: " + String.valueOf(this.topRightInfo));
        lines.add("bottomLeftInfo: " + String.valueOf(this.bottomLeftInfo));
        lines.add("bottomRightInfo: " + String.valueOf(this.bottomRightInfo));
    }

    @Override
    public void onTextureDeletion() {
        super.onTextureDeletion();
        if (this.branchUpdateChildRegion != null) {
            --this.branchUpdateChildRegion.activeBranchUpdateReferences;
        }
        this.topLeftInfo.onParentDeletion();
        this.topRightInfo.onParentDeletion();
        this.bottomLeftInfo.onParentDeletion();
        this.bottomRightInfo.onParentDeletion();
        this.reset();
    }

    public void requestDownload() {
        this.toUpload = true;
        this.updating = false;
    }

    @Override
    public void writeCacheMapData(DataOutputStream output, byte[] usableBuffer, byte[] integerByteBuffer, LeveledRegion<BranchRegionTexture> inRegion) throws IOException {
        super.writeCacheMapData(output, usableBuffer, integerByteBuffer, inRegion);
        output.writeInt(this.topLeftInfo.usedTextureVersion);
        output.writeInt(this.topRightInfo.usedTextureVersion);
        output.writeInt(this.bottomLeftInfo.usedTextureVersion);
        output.writeInt(this.bottomRightInfo.usedTextureVersion);
    }

    @Override
    public void readCacheData(int minorSaveVersion, int majorSaveVersion, DataInputStream input, byte[] usableBuffer, byte[] integerByteBuffer, LeveledRegion<BranchRegionTexture> inRegion, MapProcessor mapProcessor, int x, int y, boolean leafShouldAffectBranches) throws IOException {
        super.readCacheData(minorSaveVersion, majorSaveVersion, input, usableBuffer, integerByteBuffer, inRegion, mapProcessor, x, y, leafShouldAffectBranches);
        if (minorSaveVersion >= 15) {
            this.topLeftInfo.usedTextureVersion = input.readInt();
            this.topRightInfo.usedTextureVersion = input.readInt();
            this.bottomLeftInfo.usedTextureVersion = input.readInt();
            this.bottomRightInfo.usedTextureVersion = input.readInt();
        }
    }

    public class ChildTextureInfo {
        private int usedTextureVersion;
        private RegionTexture<?> temporaryReference;

        public ChildTextureInfo(BranchRegionTexture this$0) {
        }

        private GpuTextureAndView getColorTextureForUpdate(GpuTextureAndView emptyTexture) {
            if (this.temporaryReference == null && this.usedTextureVersion == 0 || this.temporaryReference != null && !this.temporaryReference.shouldBeUsedForBranchUpdate(this.usedTextureVersion)) {
                return null;
            }
            if (this.temporaryReference == null || !this.temporaryReference.shouldHaveContentForBranchUpdate()) {
                return emptyTexture;
            }
            return this.temporaryReference.glColorTexture;
        }

        private int getTextureVersion() {
            if (this.temporaryReference == null || !this.temporaryReference.shouldHaveContentForBranchUpdate()) {
                return 0;
            }
            return this.temporaryReference.textureVersion;
        }

        private boolean hasLight() {
            return this.temporaryReference != null && this.temporaryReference.textureHasLight && this.temporaryReference.shouldHaveContentForBranchUpdate();
        }

        public void onUpdate(int newVersion) {
            this.usedTextureVersion = newVersion;
            if (this.temporaryReference != null) {
                this.temporaryReference = null;
            }
        }

        public void onParentDeletion() {
            if (this.temporaryReference != null) {
                this.temporaryReference = null;
            }
        }

        public GpuTextureAndView getReferenceColorTexture() {
            return this.temporaryReference == null ? null : this.temporaryReference.glColorTexture;
        }

        public String toString() {
            return "tv " + this.usedTextureVersion + ", ct " + String.valueOf(this.getReferenceColorTexture());
        }
    }
}

