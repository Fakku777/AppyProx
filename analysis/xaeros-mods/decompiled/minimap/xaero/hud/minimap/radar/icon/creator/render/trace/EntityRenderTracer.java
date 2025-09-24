/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.platform.DestFactor
 *  com.mojang.blaze3d.platform.SourceFactor
 *  net.minecraft.class_10017
 *  net.minecraft.class_10042
 *  net.minecraft.class_1058
 *  net.minecraft.class_1297
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3879
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4599
 *  net.minecraft.class_4723
 *  net.minecraft.class_583
 *  net.minecraft.class_630
 *  net.minecraft.class_895
 *  net.minecraft.class_897
 *  net.minecraft.class_922
 *  org.lwjgl.opengl.GL11
 */
package xaero.hud.minimap.radar.icon.creator.render.trace;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_1058;
import net.minecraft.class_1297;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3879;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4599;
import net.minecraft.class_4723;
import net.minecraft.class_583;
import net.minecraft.class_630;
import net.minecraft.class_895;
import net.minecraft.class_897;
import net.minecraft.class_922;
import org.lwjgl.opengl.GL11;
import xaero.common.core.IBufferSource;
import xaero.common.core.ICompositeRenderType;
import xaero.common.exception.OpenGLException;
import xaero.common.misc.CachedFunction;
import xaero.common.misc.Misc;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.render.pipeline.RenderPipelineWrapper;

public class EntityRenderTracer {
    public static boolean TRACING_MODEL_RENDERS;
    private class_1297 tracedEntity;
    private class_10017 tracedEntityRenderState;
    private class_897 tracedEntityRenderer;
    private Class<?> tracedEntityModelClass;
    private List<ModelRenderTrace> traceResultList;
    private ModelRenderTrace lastModelRenderDetected;
    private class_4597.class_4598 modelRenderDetectionRenderTypeBuffer;
    private Class<?> compositeRenderTypeClass;
    private Field enderDragonModelField;
    private Field stateField;
    private Class<?> textureStateShardClass;
    private Field compositeStateTextureStateField;
    private Field textureStateShardTextureField;
    private Field renderBuffersBufferSourceField;
    private Field spriteCoordinateExpanderSpriteField;
    private Class<?> irisRenderLayerWrapperClass;
    private Method irisRenderLayerWrapperUnwrapMethod;
    private CachedFunction<RenderPipeline, RenderPipeline> fixAdditiveBlendFunction;

    public EntityRenderTracer() {
        this.initReflection();
        this.traceResultList = new ArrayList<ModelRenderTrace>();
        this.fixAdditiveBlendFunction = new CachedFunction<RenderPipeline, RenderPipeline>(this::fixAdditiveBlendUncached);
    }

    public <S extends class_10017> List<ModelRenderTrace> trace(class_4587 matrixStack, class_1297 entity, class_897<?, ? super S> entityRenderer, S entityRenderState) {
        TRACING_MODEL_RENDERS = true;
        this.tracedEntity = entity;
        this.tracedEntityRenderState = entityRenderState;
        this.tracedEntityRenderer = entityRenderer;
        this.tracedEntityModelClass = null;
        this.traceResultList.clear();
        this.lastModelRenderDetected = null;
        class_4587.class_4665 matrixEntryToRestore = matrixStack.method_23760();
        matrixStack.method_22903();
        try {
            class_4597.class_4598 renderTypeBuffer = this.modelRenderDetectionRenderTypeBuffer = (class_4597.class_4598)Misc.getReflectFieldValue(class_310.method_1551().method_22940(), this.renderBuffersBufferSourceField);
            ((IBufferSource)renderTypeBuffer).setXaero_lastRenderType(null);
            entityRenderer.method_3936(entityRenderState, matrixStack, (class_4597)renderTypeBuffer, 0xF000F0);
            renderTypeBuffer.method_22993();
            OpenGLException.checkGLError();
        }
        catch (Throwable e) {
            this.traceResultList.clear();
            MinimapLogs.LOGGER.error("Exception when calling the full entity renderer for {} before prerendering the icon.", (Object)entity.method_5820(), (Object)e);
        }
        TRACING_MODEL_RENDERS = false;
        this.tracedEntity = null;
        this.tracedEntityRenderState = null;
        this.tracedEntityRenderer = null;
        while (matrixStack.method_23760() != matrixEntryToRestore) {
            matrixStack.method_22909();
        }
        while (GL11.glGetError() != 0) {
        }
        return this.traceResultList;
    }

    public void onModelRender(class_3879 model, class_4588 vertexConsumer, int color) {
        this.lastModelRenderDetected = null;
        if (this.tracedEntityModelClass == null) {
            class_583 currentMainModel = this.getEntityRendererModel(this.tracedEntityRenderer);
            Class<?> clazz = this.tracedEntityModelClass = currentMainModel == null ? null : currentMainModel.getClass();
            if (this.tracedEntityModelClass == null) {
                return;
            }
        }
        if (!model.getClass().isAssignableFrom(this.tracedEntityModelClass) && !this.tracedEntityModelClass.isAssignableFrom(model.getClass())) {
            return;
        }
        class_1921 lastRenderType = this.getLastRenderType((class_4597)this.modelRenderDetectionRenderTypeBuffer);
        if (lastRenderType == null && this.traceResultList.isEmpty()) {
            class_2960 textureLocation = null;
            try {
                class_2960 textureLocationUnchecked;
                class_2960 class_29602;
                class_897 class_8972 = this.tracedEntityRenderer;
                if (class_8972 instanceof class_922) {
                    class_922 livingEntityRenderer = (class_922)class_8972;
                    class_29602 = livingEntityRenderer.method_3885((class_10042)this.tracedEntityRenderState);
                } else {
                    class_29602 = null;
                }
                textureLocation = textureLocationUnchecked = class_29602;
            }
            catch (Throwable t) {
                MinimapLogs.LOGGER.error("Couldn't fetch main entity texture when trying to use an alternative render type for an icon!", t);
            }
            if (textureLocation != null) {
                lastRenderType = model.method_23500(textureLocation);
            }
        }
        if (lastRenderType == null) {
            return;
        }
        if (!(lastRenderType instanceof ICompositeRenderType)) {
            return;
        }
        ICompositeRenderType compositeRenderType = (ICompositeRenderType)lastRenderType;
        Object renderState = this.getRenderState(lastRenderType);
        if (renderState == null) {
            return;
        }
        RenderPipeline renderPipeline = compositeRenderType.xaero_mm_getRenderPipeline();
        Object renderTextureState = Misc.getReflectFieldValue(renderState, this.compositeStateTextureStateField);
        class_2960 texture = null;
        if (this.textureStateShardClass.isAssignableFrom(renderTextureState.getClass())) {
            texture = this.getRenderStateTextureStateTexture(renderTextureState);
        }
        renderPipeline = this.fixAdditiveBlendFunction.apply(renderPipeline);
        class_1058 renderAtlasSprite = null;
        if (vertexConsumer instanceof class_4723) {
            renderAtlasSprite = (class_1058)Misc.getReflectFieldValue(vertexConsumer, this.spriteCoordinateExpanderSpriteField);
        }
        this.lastModelRenderDetected = new ModelRenderTrace(model, texture, renderAtlasSprite, renderPipeline, color);
        this.traceResultList.add(this.lastModelRenderDetected);
    }

    private RenderPipeline fixAdditiveBlendUncached(RenderPipeline renderPipeline) {
        SourceFactor blendSrcFactor;
        RenderPipeline result = renderPipeline;
        BlendFunction blendFunction = renderPipeline.getBlendFunction().orElse(null);
        if (blendFunction == null) {
            return result;
        }
        DestFactor blendDestFactor = blendFunction.destColor();
        if (blendDestFactor == DestFactor.ONE && (blendSrcFactor = blendFunction.sourceColor()) != SourceFactor.ZERO) {
            blendFunction = new BlendFunction(blendSrcFactor, blendDestFactor, SourceFactor.ZERO, DestFactor.ONE);
            RenderPipelineWrapper wrapper = new RenderPipelineWrapper(class_2960.method_60655((String)"xaerominimap", (String)("wrapper/" + result.getLocation().method_12832())), result);
            wrapper.setBlendFunctionOverride(Optional.of(blendFunction));
            result = wrapper;
        }
        return result;
    }

    public void onModelPartRender(class_630 modelRenderer, int color) {
        if (this.lastModelRenderDetected != null) {
            this.lastModelRenderDetected.addVisibleModelPart(modelRenderer, color);
        }
    }

    private class_1921 getLastRenderType(class_4597 renderTypeBuffer) {
        if (renderTypeBuffer instanceof IBufferSource) {
            IBufferSource xaeroBufferSource = (IBufferSource)renderTypeBuffer;
            return xaeroBufferSource.getXaero_lastRenderType();
        }
        return null;
    }

    private Object getRenderState(class_1921 renderType) {
        while (renderType.getClass() == this.irisRenderLayerWrapperClass && this.irisRenderLayerWrapperUnwrapMethod != null) {
            renderType = (class_1921)Misc.getReflectMethodValue(renderType, this.irisRenderLayerWrapperUnwrapMethod, new Object[0]);
        }
        if (renderType.getClass() == this.compositeRenderTypeClass) {
            return Misc.getReflectFieldValue(renderType, this.stateField);
        }
        return null;
    }

    private class_2960 getRenderStateTextureStateTexture(Object renderTextureState) {
        Object renderStateTextureObject = Misc.getReflectFieldValue(renderTextureState, this.textureStateShardTextureField);
        if (!(renderStateTextureObject instanceof Optional)) {
            return (class_2960)renderStateTextureObject;
        }
        Optional optional = (Optional)renderStateTextureObject;
        return optional.orElse(null);
    }

    public <S extends class_10017> class_583<S> getEntityRendererModel(class_897<?, ? super S> entityRenderer) {
        if (entityRenderer instanceof class_922) {
            return ((class_922)entityRenderer).method_4038();
        }
        if (entityRenderer instanceof class_895) {
            return (class_583)Misc.getReflectFieldValue(entityRenderer, this.enderDragonModelField);
        }
        return null;
    }

    private void initReflection() {
        this.enderDragonModelField = Misc.getFieldReflection(class_895.class, "model", "field_21008", "Lnet/minecraft/class_625;", "f_114183_");
        try {
            this.compositeRenderTypeClass = Misc.getClassForName("net.minecraft.class_1921$class_4687", "net.minecraft.client.renderer.RenderType$CompositeRenderType");
            this.stateField = Misc.getFieldReflection(this.compositeRenderTypeClass, "state", "field_21403", "Lnet/minecraft/class_1921$class_4688;", "f_110511_");
            Class<?> compositeStateClass = Misc.getClassForName("net.minecraft.class_1921$class_4688", "net.minecraft.client.renderer.RenderType$CompositeState");
            this.textureStateShardClass = Misc.getClassForName("net.minecraft.class_4668$class_4683", "net.minecraft.client.renderer.RenderStateShard$TextureStateShard");
            this.compositeStateTextureStateField = Misc.getFieldReflection(compositeStateClass, "textureState", "field_21406", "Lnet/minecraft/class_4668$class_5939;", "f_110576_");
            this.textureStateShardTextureField = Misc.getFieldReflection(this.textureStateShardClass, "texture", "field_21397", "Ljava/util/Optional;", "f_110328_");
        }
        catch (ClassNotFoundException e2) {
            throw new RuntimeException(e2);
        }
        this.renderBuffersBufferSourceField = Misc.getFieldReflection(class_4599.class, "bufferSource", "field_46901", "Lnet/minecraft/class_4597$class_4598;", "f_110094_");
        this.spriteCoordinateExpanderSpriteField = Misc.getFieldReflection(class_4723.class, "sprite", "field_21731", "Lnet/minecraft/class_1058;", "f_110796_");
        try {
            try {
                this.irisRenderLayerWrapperClass = Class.forName("net.coderbot.iris.layer.IrisRenderTypeWrapper");
            }
            catch (ClassNotFoundException e) {
                this.irisRenderLayerWrapperClass = Class.forName("net.coderbot.iris.layer.IrisRenderLayerWrapper");
            }
            this.irisRenderLayerWrapperUnwrapMethod = Misc.getMethodReflection(this.irisRenderLayerWrapperClass, "unwrap", "unwrap", "()Lnet/minecraft/class_1921;", "unwrap", new Class[0]);
            MinimapLogs.LOGGER.info("Old Iris detected and supported!");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void reset() {
        this.fixAdditiveBlendFunction = new CachedFunction<RenderPipeline, RenderPipeline>(this::fixAdditiveBlendUncached);
    }
}

