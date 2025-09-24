/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10017
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_5603
 *  net.minecraft.class_583
 *  net.minecraft.class_597
 *  net.minecraft.class_630
 *  net.minecraft.class_630$class_628
 *  net.minecraft.class_9799
 *  net.minecraft.class_9848
 *  org.lwjgl.opengl.GL11
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.part;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_10017;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_5603;
import net.minecraft.class_583;
import net.minecraft.class_597;
import net.minecraft.class_630;
import net.minecraft.class_9799;
import net.minecraft.class_9848;
import org.lwjgl.opengl.GL11;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.misc.Misc;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.ModelPartUtil;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelPartRenderTrace;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.model.config.RadarIconModelConfig;

public class RadarIconModelPartPrerenderer {
    public final Field quadrupedHeadField = Misc.getFieldReflection(class_597.class, "head", "field_3535", "Lnet/minecraft/class_630;", "f_103492_");
    private final VertexConsumerWrapper vertexConsumerWrapper = new VertexConsumerWrapper();
    private boolean testedRenderEngineWrapperCompatibility;
    private boolean renderEngineIsWrapperCompatible;
    private class_4597.class_4598 testBufferSource = class_4597.method_22991((class_9799)new class_9799(256));

    public void renderPart(class_4587 matrixStack, class_4588 vertexConsumer, class_630 part, class_630 mainPart, Parameters parameters) {
        if (part == null) {
            return;
        }
        if (parameters.renderedDest.contains(part)) {
            return;
        }
        ModelPartRenderTrace renderInfo = parameters.mrt.getModelPartRenderInfo(part);
        if (renderInfo == null) {
            return;
        }
        if (!ModelPartUtil.hasCubes(part)) {
            return;
        }
        boolean showModelBU = part.field_3665;
        boolean skipDrawBU = part.field_38456;
        if (!this.testedRenderEngineWrapperCompatibility) {
            this.testRenderEngineWrapperCompatibility(part, parameters.mrt, renderInfo);
        }
        float centerPointX = mainPart.field_3657;
        float centerPointY = mainPart.field_3656;
        float centerPointZ = mainPart.field_3655;
        class_630.class_628 biggestMainPartCuboid = ModelPartUtil.getBiggestCuboid(mainPart);
        if (biggestMainPartCuboid != null) {
            centerPointY += (biggestMainPartCuboid.field_3647 + biggestMainPartCuboid.field_3644) / 2.0f;
            centerPointZ += (biggestMainPartCuboid.field_3646 + biggestMainPartCuboid.field_3643) / 2.0f;
        }
        float xRotBU = 0.0f;
        float yRotBU = 0.0f;
        float zRotBU = 0.0f;
        if (parameters.config.modelPartsRotationReset) {
            xRotBU = part.field_3654;
            yRotBU = part.field_3675;
            zRotBU = part.field_3674;
            class_5603 initPose = part.method_41921();
            part.method_33425(initPose.comp_3000(), initPose.comp_3001(), initPose.comp_3002());
        }
        part.field_3665 = true;
        part.field_38456 = false;
        float xBU = part.field_3657;
        float yBU = part.field_3656;
        float zBU = part.field_3655;
        part.method_2851(part.field_3657 - centerPointX, part.field_3656 - centerPointY, part.field_3655 - centerPointZ);
        try {
            if (this.renderEngineIsWrapperCompatible) {
                vertexConsumer = this.vertexConsumerWrapper.prepareDetection(vertexConsumer, 3.0, 61.0, 3.0, 61.0, -497.0, -2.0);
            }
            part.method_22699(matrixStack, vertexConsumer, 0xF000F0, class_4608.field_21444, renderInfo.color);
            if ((!this.renderEngineIsWrapperCompatible || this.vertexConsumerWrapper.hasDetectedVertex()) && class_9848.method_61320((int)renderInfo.color) > 0) {
                parameters.renderedDest.add(part);
            }
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.info("Exception when rendering entity part. " + String.valueOf(part) + " " + t.getMessage());
        }
        part.method_2851(xBU, yBU, zBU);
        while (GL11.glGetError() != 0) {
        }
        if (parameters.config.modelPartsRotationReset) {
            part.method_33425(xRotBU, yRotBU, zRotBU);
        }
        part.field_3665 = showModelBU;
        part.field_38456 = skipDrawBU;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void testRenderEngineWrapperCompatibility(class_630 part, ModelRenderTrace mrt, ModelPartRenderTrace renderInfo) {
        boolean normalWorks = false;
        try {
            class_4587 testMatrix = new class_4587();
            testMatrix.method_46416(0.0f, 0.0f, -2500.0f);
            class_4588 actualVertexConsumer = this.testBufferSource.getBuffer(CustomRenderTypes.entityIconRenderType(mrt.renderTexture, mrt.layerPipeline));
            part.method_22699(testMatrix, actualVertexConsumer, 0xF000F0, class_4608.field_21444, renderInfo.color);
            normalWorks = true;
            testMatrix = new class_4587();
            testMatrix.method_46416(0.0f, 0.0f, -2500.0f);
            this.vertexConsumerWrapper.prepareDetection(actualVertexConsumer, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            part.field_3665 = true;
            part.field_38456 = false;
            part.method_22699(testMatrix, (class_4588)this.vertexConsumerWrapper, 0xF000F0, class_4608.field_21444, renderInfo.color);
            this.renderEngineIsWrapperCompatible = this.vertexConsumerWrapper.hasDetectedVertex();
            if (!this.renderEngineIsWrapperCompatible) {
                throw new Exception("can't detect vertices");
            }
        }
        catch (Throwable t) {
            if (normalWorks) {
                MinimapLogs.LOGGER.warn("Render engine used for entities is not fully compatible with the minimap entity icons. Using fallback. " + t.getMessage());
            }
        }
        finally {
            this.testBufferSource.method_37104();
        }
        if (normalWorks) {
            this.testedRenderEngineWrapperCompatibility = true;
        }
    }

    public <T extends class_10017> class_630 renderDeclaredMethod(class_4587 matrixStack, class_4588 vertexConsumer, Method method, class_583<T> model, class_630 mainPart, Parameters parameters) {
        if (method == null) {
            return mainPart;
        }
        return this.renderPartsIterable((Iterable)Misc.getReflectMethodValue(model, method, new Object[0]), matrixStack, vertexConsumer, mainPart, parameters);
    }

    public class_630 renderPartsIterable(Iterable<class_630> parts, class_4587 matrixStack, class_4588 vertexConsumer, class_630 mainPart, Parameters parameters) {
        if (parts == null) {
            return mainPart;
        }
        Iterator<class_630> partsIterator = parts.iterator();
        if (!partsIterator.hasNext()) {
            return mainPart;
        }
        if (mainPart == null) {
            mainPart = partsIterator.next();
            this.renderPart(matrixStack, vertexConsumer, mainPart, mainPart, parameters);
        }
        while (partsIterator.hasNext()) {
            this.renderPart(matrixStack, vertexConsumer, partsIterator.next(), mainPart, parameters);
        }
        return mainPart;
    }

    public static class VertexConsumerWrapper
    implements class_4588 {
        private class_4588 consumer;
        private boolean detectedVertex;
        private double detectionMinX;
        private double detectionMaxX;
        private double detectionMinY;
        private double detectionMaxY;
        private double detectionMinZ;
        private double detectionMaxZ;

        public VertexConsumerWrapper prepareDetection(class_4588 consumer, double detectionMinX, double detectionMaxX, double detectionMinY, double detectionMaxY, double detectionMinZ, double detectionMaxZ) {
            this.consumer = consumer;
            this.detectionMinX = detectionMinX;
            this.detectionMaxX = detectionMaxX;
            this.detectionMinY = detectionMinY;
            this.detectionMaxY = detectionMaxY;
            this.detectionMinZ = detectionMinZ;
            this.detectionMaxZ = detectionMaxZ;
            this.detectedVertex = false;
            return this;
        }

        public class_4588 method_22912(float d, float e, float f) {
            if ((double)d >= this.detectionMinX && (double)d <= this.detectionMaxX && (double)e >= this.detectionMinY && (double)e <= this.detectionMaxY && (double)f >= this.detectionMinZ && (double)f <= this.detectionMaxZ) {
                this.detectedVertex = true;
            }
            return this.consumer.method_22912(d, e, f);
        }

        public class_4588 method_1336(int i, int j, int k, int l) {
            return this.consumer.method_1336(i, j, k, l);
        }

        public class_4588 method_22913(float f, float g) {
            return this.consumer.method_22913(f, g);
        }

        public class_4588 method_60796(int var1, int var2) {
            return this.consumer.method_60796(var1, var2);
        }

        public class_4588 method_22922(int i) {
            return this.consumer.method_22922(i);
        }

        public class_4588 method_22921(int i, int j) {
            return this.consumer.method_22921(i, j);
        }

        public class_4588 method_22914(float f, float g, float h) {
            return this.consumer.method_22914(f, g, h);
        }

        public boolean hasDetectedVertex() {
            return this.detectedVertex;
        }
    }

    public static class Parameters {
        public final RadarIconModelConfig config;
        public final ModelRenderTrace mrt;
        public final List<class_630> renderedDest;

        public Parameters(RadarIconModelConfig config, ModelRenderTrace mrt, List<class_630> renderedDest) {
            this.config = config;
            this.mrt = mrt;
            this.renderedDest = renderedDest;
        }
    }
}

