/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  javax.annotation.Nullable
 *  net.minecraft.class_10017
 *  net.minecraft.class_10042
 *  net.minecraft.class_1058
 *  net.minecraft.class_1297
 *  net.minecraft.class_2960
 *  net.minecraft.class_308$class_11274
 *  net.minecraft.class_310
 *  net.minecraft.class_3879
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_583
 *  net.minecraft.class_630
 *  net.minecraft.class_897
 *  net.minecraft.class_922
 *  net.minecraft.class_9799
 *  org.joml.Vector3fc
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_1058;
import net.minecraft.class_1297;
import net.minecraft.class_2960;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_3879;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_583;
import net.minecraft.class_630;
import net.minecraft.class_897;
import net.minecraft.class_922;
import net.minecraft.class_9799;
import org.joml.Vector3fc;
import xaero.common.exception.OpenGLException;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.misc.OptimizedMath;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.creator.entity.LivingEntityPoseResetter;
import xaero.hud.minimap.radar.icon.creator.render.form.IRadarIconFormPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.RadarIconModelPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.custom.RadarIconCustomPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.RadarIconModelPartPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.BuiltInRadarIconDefinitions;
import xaero.hud.minimap.radar.icon.definition.form.model.RadarIconModelForm;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;
import xaero.hud.render.util.ImmediateRenderUtil;

public class RadarIconModelFormPrerenderer
implements IRadarIconFormPrerenderer {
    private final LivingEntityPoseResetter livingEntityPoseResetter;
    private ModelRenderTrace mainModelTrace;
    private final ArrayList<class_630> mainRenderedModels;
    private final RadarIconModelPrerenderer modelPrerenderer;
    private class_630 mainPart;
    private List<String> hardcodedMainPartAliases;
    private List<String> hardcodedModelPartsFields;
    private boolean forceFieldCheck;
    private boolean fullModelIcon;
    private class_4597.class_4598 entityIconRenderTypeBuffer = class_4597.method_22991((class_9799)new class_9799(256));

    public RadarIconModelFormPrerenderer() {
        this.livingEntityPoseResetter = new LivingEntityPoseResetter();
        this.mainRenderedModels = new ArrayList();
        this.modelPrerenderer = new RadarIconModelPrerenderer();
    }

    @Override
    public boolean requiresEntityModel() {
        return true;
    }

    @Override
    public boolean isFlipped() {
        return false;
    }

    @Override
    public boolean isOutlined() {
        return true;
    }

    @Override
    public <S extends class_10017> boolean prerender(MinimapElementGraphics guiGraphics, class_897<?, ? super S> entityRenderer, S entityRenderState, @Nullable class_583<S> entityModel, class_1297 entity, @Nullable List<ModelRenderTrace> traceList, RadarIconCreator.Parameters parameters) {
        class_4587 matrixStack = guiGraphics.pose();
        RadarIconModelForm modelForm = (RadarIconModelForm)parameters.form;
        class_4597.class_4598 renderTypeBuffer = this.entityIconRenderTypeBuffer;
        class_310.method_1551().field_1773.method_71114().method_71034(class_308.class_11274.field_60026);
        if (parameters.debug) {
            matrixStack.method_22903();
            matrixStack.method_46416(0.0f, 10.0f, -10.0f);
            matrixStack.method_22905(1.0f, 1.0f, 1.0f);
            ImmediateRenderUtil.coloredRectangle(matrixStack, 0.0f, 0.0f, 9.0f, 9.0f, -65536);
            matrixStack.method_22909();
        }
        RadarIconModelConfig config = parameters.defaultModelConfig;
        RadarIconModelConfig variantModelConfig = modelForm.getConfig();
        if (variantModelConfig != null) {
            config = variantModelConfig;
        }
        matrixStack.method_22903();
        matrixStack.method_46416(32.0f, 32.0f, -450.0f);
        matrixStack.method_46416(config.offsetX, config.offsetY, 0.0f);
        int mainScale = 32;
        matrixStack.method_22905((float)mainScale, (float)mainScale, (float)(-mainScale));
        float scale = parameters.scale;
        if (scale < 1.0f) {
            matrixStack.method_22905(scale, scale, scale);
        }
        matrixStack.method_22905(config.baseScale, config.baseScale, config.baseScale);
        OptimizedMath.rotatePose(matrixStack, config.rotationY, (Vector3fc)OptimizedMath.YP);
        OptimizedMath.rotatePose(matrixStack, config.rotationX, (Vector3fc)OptimizedMath.XP);
        OptimizedMath.rotatePose(matrixStack, config.rotationZ, (Vector3fc)OptimizedMath.ZP);
        BuiltInRadarIconDefinitions.defaultTransformation(matrixStack, entityModel, entity);
        if (entityRenderState instanceof class_10042) {
            class_10042 livingEntityRenderState = (class_10042)entityRenderState;
            this.livingEntityPoseResetter.resetValues(livingEntityRenderState);
        }
        boolean result = this.renderLayers(matrixStack, renderTypeBuffer, entityRenderer, entityRenderState, entityModel, traceList, entity, config, parameters.defaultModelConfig);
        BuiltInRadarIconDefinitions.defaultPostIconModelRender(matrixStack, entityModel, entity);
        matrixStack.method_22909();
        if (parameters.debug) {
            matrixStack.method_22903();
            matrixStack.method_46416(9.0f, 10.0f, -10.0f);
            matrixStack.method_22905(1.0f, 1.0f, 1.0f);
            ImmediateRenderUtil.coloredRectangle(matrixStack, 0.0f, 0.0f, 9.0f, 9.0f, -16711936);
            matrixStack.method_22909();
        }
        return result;
    }

    private <S extends class_10017> boolean renderLayers(class_4587 matrixStack, class_4597.class_4598 bufferSource, class_897<?, ? super S> entityRenderer, S entityRenderState, class_583<S> mainEntityModel, List<ModelRenderTrace> traceList, class_1297 entity, RadarIconModelConfig config, RadarIconModelConfig defaultConfig) {
        boolean bl = this.forceFieldCheck = !(config.renderingFullModel != null && config.renderingFullModel != false || config.modelPartsFields == null && !BuiltInRadarIconDefinitions.forceFieldCheck(mainEntityModel));
        this.fullModelIcon = config.renderingFullModel == null ? !this.forceFieldCheck && BuiltInRadarIconDefinitions.fullModelIcon(mainEntityModel) : config.renderingFullModel;
        boolean renderedSomething = false;
        if (traceList.isEmpty()) {
            this.addDefaultLayer(traceList, entityRenderer, entityRenderState, mainEntityModel, entity);
        }
        boolean allEmpty = true;
        for (ModelRenderTrace mrt : traceList) {
            if (mrt.isEmpty()) continue;
            allEmpty = false;
            break;
        }
        if (allEmpty) {
            for (ModelRenderTrace mrt : traceList) {
                mrt.allVisible = true;
            }
        }
        this.mainPart = null;
        this.mainModelTrace = null;
        this.hardcodedMainPartAliases = BuiltInRadarIconDefinitions.getMainModelPartFields(entityRenderer, mainEntityModel, entity);
        this.hardcodedModelPartsFields = BuiltInRadarIconDefinitions.getSecondaryModelPartsFields(entityRenderer, mainEntityModel, entity);
        this.mainRenderedModels.clear();
        for (ModelRenderTrace mrt : traceList) {
            if (mrt.isEmpty() || renderedSomething && !config.layersAllowed) continue;
            int result = this.renderLayer(matrixStack, bufferSource, mrt, entityRenderState, mainEntityModel, entity, config, defaultConfig);
            if (result == -1) break;
            if (result != 1) continue;
            renderedSomething = true;
        }
        this.hardcodedMainPartAliases = null;
        this.hardcodedModelPartsFields = null;
        if (this.mainRenderedModels.isEmpty() || !config.layersAllowed) {
            return renderedSomething;
        }
        RadarIconCustomPrerenderer extraLayer = BuiltInRadarIconDefinitions.getCustomLayer(entityRenderer, entity);
        RadarIconModelPartPrerenderer partPrerenderer = this.modelPrerenderer.getPartPrerenderer();
        if (extraLayer != null) {
            this.mainPart = extraLayer.render(matrixStack, bufferSource, entityRenderer, entityRenderState, entity, mainEntityModel, partPrerenderer, this.mainRenderedModels, this.mainPart, config, this.mainModelTrace);
        }
        return renderedSomething;
    }

    private <S extends class_10017> void addDefaultLayer(List<ModelRenderTrace> traceList, class_897<?, ? super S> entityRenderer, S entityRenderState, class_583<S> mainEntityModel, class_1297 entity) {
        class_2960 mainEntityTexture = null;
        try {
            class_2960 class_29602;
            if (entityRenderer instanceof class_922) {
                class_922 livingEntityRenderer = (class_922)entityRenderer;
                class_29602 = livingEntityRenderer.method_3885((class_10042)entityRenderState);
            } else {
                class_29602 = null;
            }
            mainEntityTexture = class_29602;
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("Couldn't fetch main entity texture when prerendering an icon with nothing detected!", t);
        }
        if (mainEntityTexture == null) {
            return;
        }
        RenderPipeline basicPipeline = CustomRenderTypes.getBasicRenderPipeline();
        traceList.add(new ModelRenderTrace((class_3879)mainEntityModel, mainEntityTexture, null, basicPipeline, -1));
    }

    private <S extends class_10017> int renderLayer(class_4587 matrixStack, class_4597.class_4598 bufferSource, ModelRenderTrace mrt, S entityRenderState, class_583<S> mainEntityModel, class_1297 entity, RadarIconModelConfig config, RadarIconModelConfig defaultConfig) {
        boolean mainPartsVisibility;
        class_3879 traceModel = mrt.model;
        class_2960 traceTexture = mrt.renderTexture;
        class_1058 traceAtlasSprite = mrt.renderAtlasSprite;
        boolean mainModel = traceModel == mainEntityModel;
        boolean bl = mainPartsVisibility = mainModel && this.mainModelTrace != null && mrt.sameVisibility(this.mainModelTrace);
        if (mainModel && !mainPartsVisibility) {
            if (traceTexture == null) {
                return 0;
            }
            if (!this.resetModelRotations(entityRenderState, traceModel)) {
                return -1;
            }
            this.mainRenderedModels.clear();
            RadarIconModelPrerenderer.Parameters parameters = new RadarIconModelPrerenderer.Parameters(config, defaultConfig, traceTexture, traceAtlasSprite, mrt, this.forceFieldCheck, this.fullModelIcon, this.hardcodedMainPartAliases, this.hardcodedModelPartsFields, this.mainRenderedModels);
            this.mainPart = this.modelPrerenderer.renderModel(matrixStack, bufferSource, entityRenderState, traceModel, entity, this.mainPart, parameters);
            this.mainModelTrace = mrt;
            if (!this.mainRenderedModels.isEmpty()) {
                return 1;
            }
            return 0;
        }
        if (!mainModel) {
            if (traceTexture == null) {
                return 0;
            }
            if (!this.resetModelRotations(entityRenderState, traceModel)) {
                return -1;
            }
            ArrayList<class_630> renderedModels = new ArrayList<class_630>();
            RadarIconModelPrerenderer.Parameters parameters = new RadarIconModelPrerenderer.Parameters(config, defaultConfig, traceTexture, traceAtlasSprite, mrt, this.forceFieldCheck, this.fullModelIcon, this.hardcodedMainPartAliases, this.hardcodedModelPartsFields, renderedModels);
            this.mainPart = this.modelPrerenderer.renderModel(matrixStack, bufferSource, entityRenderState, traceModel, entity, this.mainPart, parameters);
            if (!renderedModels.isEmpty()) {
                return 1;
            }
            return 0;
        }
        if (this.mainRenderedModels.isEmpty()) {
            return 0;
        }
        class_4588 vertexConsumer = this.modelPrerenderer.getLayerModelVertexConsumer(bufferSource, traceTexture, traceAtlasSprite, mrt);
        RadarIconModelPartPrerenderer.Parameters parameters = new RadarIconModelPartPrerenderer.Parameters(config, mrt, new ArrayList<class_630>());
        this.modelPrerenderer.getPartPrerenderer().renderPartsIterable(this.mainRenderedModels, matrixStack, vertexConsumer, this.mainPart, parameters);
        bufferSource.method_22993();
        return 0;
    }

    private <T extends class_1297> boolean resetModelRotations(class_10017 entityRenderState, class_3879 model) {
        if (!(model instanceof class_583)) {
            return true;
        }
        class_583 entityModel = (class_583)model;
        try {
            entityModel.method_2819(entityRenderState);
            OpenGLException.checkGLError();
            return true;
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("suppressed exception", t);
            return false;
        }
    }
}

