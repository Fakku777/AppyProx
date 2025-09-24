/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$UniformDescription
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.platform.LogicOp
 *  com.mojang.blaze3d.platform.PolygonMode
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_10149
 *  net.minecraft.class_2960
 */
package xaero.hud.render.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_10149;
import net.minecraft.class_2960;

public class RenderPipelineWrapper
extends RenderPipeline {
    private final RenderPipeline original;
    private Optional<class_2960> vertexShaderOverride;
    private Optional<class_2960> fragmentShaderOverride;
    private Optional<class_10149> shaderDefinesOverride;
    private Optional<List<String>> samplersOverride;
    private Optional<List<RenderPipeline.UniformDescription>> uniformsOverride;
    private Optional<DepthTestFunction> depthTestFunctionOverride;
    private Optional<PolygonMode> polygonModeOverride;
    private Optional<Boolean> cullOverride;
    private Optional<LogicOp> colorLogicOverride;
    private Optional<Optional<BlendFunction>> blendFunctionOverride;
    private Optional<Boolean> writeColorOverride;
    private Optional<Boolean> writeAlphaOverride;
    private Optional<Boolean> writeDepthOverride;
    private Optional<VertexFormat> vertexFormatOverride;
    private Optional<VertexFormat.class_5596> vertexFormatModeOverride;
    private Optional<Float> depthBiasScaleFactorOverride;
    private Optional<Float> depthBiasConstantOverride;
    private Optional<Integer> sortKeyOverride;

    public RenderPipelineWrapper(class_2960 location, RenderPipeline original) {
        super(location, null, null, null, null, null, null, null, null, false, false, false, false, null, null, null, 0.0f, 0.0f, 0);
        this.original = original;
    }

    public DepthTestFunction getDepthTestFunction() {
        if (this.depthTestFunctionOverride.isPresent()) {
            return this.depthTestFunctionOverride.get();
        }
        return this.original.getDepthTestFunction();
    }

    public PolygonMode getPolygonMode() {
        if (this.polygonModeOverride.isPresent()) {
            return this.polygonModeOverride.get();
        }
        return this.original.getPolygonMode();
    }

    public boolean isCull() {
        if (this.cullOverride.isPresent()) {
            return this.cullOverride.get();
        }
        return this.original.isCull();
    }

    public LogicOp getColorLogic() {
        if (this.colorLogicOverride.isPresent()) {
            return this.colorLogicOverride.get();
        }
        return this.original.getColorLogic();
    }

    public Optional<BlendFunction> getBlendFunction() {
        if (this.blendFunctionOverride.isPresent()) {
            return this.blendFunctionOverride.get();
        }
        return this.original.getBlendFunction();
    }

    public boolean isWriteColor() {
        if (this.writeColorOverride.isPresent()) {
            return this.writeColorOverride.get();
        }
        return this.original.isWriteColor();
    }

    public boolean isWriteAlpha() {
        if (this.writeAlphaOverride.isPresent()) {
            return this.writeAlphaOverride.get();
        }
        return this.original.isWriteAlpha();
    }

    public boolean isWriteDepth() {
        if (this.writeDepthOverride.isPresent()) {
            return this.writeDepthOverride.get();
        }
        return this.original.isWriteDepth();
    }

    public float getDepthBiasScaleFactor() {
        if (this.depthBiasScaleFactorOverride.isPresent()) {
            return this.depthBiasScaleFactorOverride.get().floatValue();
        }
        return this.original.getDepthBiasScaleFactor();
    }

    public float getDepthBiasConstant() {
        if (this.depthBiasConstantOverride.isPresent()) {
            return this.depthBiasConstantOverride.get().floatValue();
        }
        return this.original.getDepthBiasConstant();
    }

    public VertexFormat getVertexFormat() {
        if (this.vertexFormatOverride.isPresent()) {
            return this.vertexFormatOverride.get();
        }
        return this.original.getVertexFormat();
    }

    public VertexFormat.class_5596 getVertexFormatMode() {
        if (this.vertexFormatModeOverride.isPresent()) {
            return this.vertexFormatModeOverride.get();
        }
        return this.original.getVertexFormatMode();
    }

    public class_2960 getVertexShader() {
        if (this.vertexShaderOverride.isPresent()) {
            return this.vertexShaderOverride.get();
        }
        return this.original.getVertexShader();
    }

    public class_2960 getFragmentShader() {
        if (this.fragmentShaderOverride.isPresent()) {
            return this.fragmentShaderOverride.get();
        }
        return this.original.getFragmentShader();
    }

    public class_10149 getShaderDefines() {
        if (this.shaderDefinesOverride.isPresent()) {
            return this.shaderDefinesOverride.get();
        }
        return this.original.getShaderDefines();
    }

    public List<String> getSamplers() {
        if (this.samplersOverride.isPresent()) {
            return this.samplersOverride.get();
        }
        return this.original.getSamplers();
    }

    public List<RenderPipeline.UniformDescription> getUniforms() {
        if (this.uniformsOverride.isPresent()) {
            return this.uniformsOverride.get();
        }
        return this.original.getUniforms();
    }

    public int getSortKey() {
        if (this.sortKeyOverride.isPresent()) {
            return this.sortKeyOverride.get();
        }
        return this.original.getSortKey();
    }

    public boolean wantsDepthTexture() {
        if (this.depthTestFunctionOverride.isEmpty() && this.depthBiasConstantOverride.isEmpty() && this.depthBiasScaleFactorOverride.isEmpty() && this.writeDepthOverride.isEmpty()) {
            return this.original.wantsDepthTexture();
        }
        return this.getDepthTestFunction() != DepthTestFunction.NO_DEPTH_TEST || this.getDepthBiasConstant() != 0.0f || this.getDepthBiasScaleFactor() != 0.0f || this.isWriteDepth();
    }

    public void setVertexShaderOverride(class_2960 vertexShaderOverride) {
        this.vertexShaderOverride = Optional.of(vertexShaderOverride);
    }

    public void setFragmentShaderOverride(class_2960 fragmentShaderOverride) {
        this.fragmentShaderOverride = Optional.of(fragmentShaderOverride);
    }

    public void setShaderDefinesOverride(class_10149 shaderDefinesOverride) {
        this.shaderDefinesOverride = Optional.of(shaderDefinesOverride);
    }

    public void setSamplersOverride(List<String> samplersOverride) {
        this.samplersOverride = Optional.of(samplersOverride);
    }

    public void setUniformsOverride(List<RenderPipeline.UniformDescription> uniformsOverride) {
        this.uniformsOverride = Optional.of(uniformsOverride);
    }

    public void setDepthTestFunctionOverride(DepthTestFunction depthTestFunctionOverride) {
        this.depthTestFunctionOverride = Optional.of(depthTestFunctionOverride);
    }

    public void setPolygonModeOverride(PolygonMode polygonModeOverride) {
        this.polygonModeOverride = Optional.of(polygonModeOverride);
    }

    public void setCullOverride(Boolean cullOverride) {
        this.cullOverride = Optional.of(cullOverride);
    }

    public void setColorLogicOverride(LogicOp colorLogicOverride) {
        this.colorLogicOverride = Optional.of(colorLogicOverride);
    }

    public void setBlendFunctionOverride(Optional<BlendFunction> blendFunctionOverride) {
        this.blendFunctionOverride = Optional.of(blendFunctionOverride);
    }

    public void setWriteColorOverride(boolean writeColorOverride) {
        this.writeColorOverride = Optional.of(writeColorOverride);
    }

    public void setWriteAlphaOverride(boolean writeAlphaOverride) {
        this.writeAlphaOverride = Optional.of(writeAlphaOverride);
    }

    public void setWriteDepthOverride(boolean writeDepthOverride) {
        this.writeDepthOverride = Optional.of(writeDepthOverride);
    }

    public void setVertexFormatOverride(VertexFormat vertexFormatOverride) {
        this.vertexFormatOverride = Optional.of(vertexFormatOverride);
    }

    public void setVertexFormatModeOverride(VertexFormat.class_5596 vertexFormatModeOverride) {
        this.vertexFormatModeOverride = Optional.of(vertexFormatModeOverride);
    }

    public void setDepthBiasScaleFactorOverride(float depthBiasScaleFactorOverride) {
        this.depthBiasScaleFactorOverride = Optional.of(Float.valueOf(depthBiasScaleFactorOverride));
    }

    public void setDepthBiasConstantOverride(float depthBiasConstantOverride) {
        this.depthBiasConstantOverride = Optional.of(Float.valueOf(depthBiasConstantOverride));
    }

    public void setSortKeyOverride(int sortKeyOverride) {
        this.sortKeyOverride = Optional.of(sortKeyOverride);
    }
}

