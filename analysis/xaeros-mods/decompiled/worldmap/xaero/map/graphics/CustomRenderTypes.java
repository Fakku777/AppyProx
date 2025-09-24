/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.platform.DestFactor
 *  com.mojang.blaze3d.platform.SourceFactor
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.minecraft.class_10789
 *  net.minecraft.class_1921
 *  net.minecraft.class_1921$class_4688
 *  net.minecraft.class_1921$class_4688$class_4689
 *  net.minecraft.class_1921$class_4750
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_4668
 *  net.minecraft.class_4668$class_4675
 *  net.minecraft.class_4668$class_4676
 *  net.minecraft.class_4668$class_4677
 *  net.minecraft.class_4668$class_4678
 *  net.minecraft.class_4668$class_4679
 *  net.minecraft.class_4668$class_4683
 *  net.minecraft.class_4668$class_4684
 *  net.minecraft.class_4668$class_5939
 *  net.minecraft.class_9801
 */
package xaero.map.graphics;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.lang.reflect.Method;
import net.minecraft.class_10789;
import net.minecraft.class_1921;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_4668;
import net.minecraft.class_9801;
import xaero.map.WorldMap;
import xaero.map.graphics.ImprovedCompositeRenderType;
import xaero.map.graphics.shader.BuiltInCustomUniforms;
import xaero.map.graphics.shader.MapShaders;
import xaero.map.misc.Misc;

public class CustomRenderTypes
extends class_1921 {
    public static final VertexFormat POSITION_COLOR_TEX = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV0", VertexFormatElement.UV0).build();
    protected static final BlendFunction TRANSLUCENT_TRANSPARENCY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    protected static final BlendFunction DEST_TRANSPARENCY = new BlendFunction(SourceFactor.ONE, DestFactor.ZERO, SourceFactor.ZERO, DestFactor.ONE);
    protected static final BlendFunction PREMULTIPLIED_TRANSPARENCY = new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    protected static final BlendFunction ADD_ALPHA_TRANSPARENCY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE);
    public static final RenderPipeline RP_POSITION_COLOR_TEX;
    public static final RenderPipeline RP_POSITION_COLOR_TEX_NO_BLEND;
    public static final RenderPipeline RP_POSITION_COLOR_TEX_NO_CULL;
    public static final RenderPipeline RP_POSITION_COLOR;
    public static final RenderPipeline RP_POSITION_COLOR_NO_BLEND;
    public static final RenderPipeline RP_POSITION_COLOR_NO_CULL;
    public static final RenderPipeline RP_MAP_BRANCH;
    public static final class_1921 GUI;
    public static final class_1921 GUI_PREMULTIPLIED;
    public static final class_1921 MAP;
    public static final class_1921 MAP_BRANCH;
    public static final class_1921 MAP_COLOR_OVERLAY;
    public static final class_1921 MAP_FRAME;
    public static final class_1921 MAP_COLOR_FILLER;
    public static final class_1921 MAP_ELEMENT_TEXT_BG;
    private static final Method compositeStateBuilderMethod;
    private static final Method compositeStateBuilderCreateCompositeStateMethod;
    private static final Method compositeStateBuilderSetTextureStateMethod;
    private static final Method compositeStateBuilderSetLightmapStateMethod;
    private static final Method compositeStateBuilderSetOverlayStateMethod;
    private static final Method compositeStateBuilderSetLayeringStateMethod;
    private static final Method compositeStateBuilderSetOutputStateMethod;
    private static final Method compositeStateBuilderSetTexturingStateMethod;
    private static final Method compositeStateBuilderSetLineStateMethod;
    private static final Method renderTypeCreateMethod;

    private CustomRenderTypes(String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable startAction, Runnable endAction) {
        super(name, bufferSize, affectsCrumbling, sortOnUpload, startAction, endAction);
    }

    public void method_60895(class_9801 var1) {
        throw new IllegalAccessError();
    }

    public VertexFormat method_23031() {
        throw new IllegalAccessError();
    }

    public VertexFormat.class_5596 method_23033() {
        throw new IllegalAccessError();
    }

    private static CustomStateBuilder getStateBuilder() {
        return new CustomStateBuilder((class_1921.class_4688.class_4689)Misc.getReflectMethodValue(null, compositeStateBuilderMethod, new Object[0]));
    }

    private static class_1921 createRenderType(String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, RenderPipeline renderPipeline, CustomStateBuilder stateBuilder, class_1921.class_4750 outlineProperty) {
        class_1921.class_4688 compositeState = stateBuilder.createCompositeState(outlineProperty);
        class_1921 normalRenderType = (class_1921)Misc.getReflectMethodValue(null, renderTypeCreateMethod, name, bufferSize, affectsCrumbling, sortOnUpload, renderPipeline, compositeState);
        return new ImprovedCompositeRenderType(name, bufferSize, affectsCrumbling, sortOnUpload, renderPipeline, stateBuilder.getOutputState(), compositeState, normalRenderType);
    }

    public static class_276 getOutputStateTarget(Object outputStateShard) {
        return ((class_4668.class_4678)outputStateShard).method_68491();
    }

    static {
        try {
            Class<?> compositeStateClass = Misc.getClassForName("net.minecraft.class_1921$class_4688", "net.minecraft.client.renderer.RenderType$CompositeState");
            compositeStateBuilderMethod = Misc.getMethodReflection(compositeStateClass, "builder", "method_23598", "()Lnet/minecraft/class_1921$class_4688$class_4689;", "m_110628_", new Class[0]);
            compositeStateBuilderCreateCompositeStateMethod = Misc.getMethodReflection(class_1921.class_4688.class_4689.class, "createCompositeState", "method_24297", "(Lnet/minecraft/class_1921$class_4750;)Lnet/minecraft/class_1921$class_4688;", "m_110689_", class_1921.class_4750.class);
            compositeStateBuilderSetTextureStateMethod = Misc.getMethodReflection(class_1921.class_4688.class_4689.class, "setTextureState", "method_34577", "(Lnet/minecraft/class_4668$class_5939;)Lnet/minecraft/class_1921$class_4688$class_4689;", "m_173290_", class_4668.class_5939.class);
            compositeStateBuilderSetLightmapStateMethod = Misc.getMethodReflection(class_1921.class_4688.class_4689.class, "setLightmapState", "method_23608", "(Lnet/minecraft/class_4668$class_4676;)Lnet/minecraft/class_1921$class_4688$class_4689;", "m_110671_", class_4668.class_4676.class);
            compositeStateBuilderSetOverlayStateMethod = Misc.getMethodReflection(class_1921.class_4688.class_4689.class, "setOverlayState", "method_23611", "(Lnet/minecraft/class_4668$class_4679;)Lnet/minecraft/class_1921$class_4688$class_4689;", "m_110677_", class_4668.class_4679.class);
            compositeStateBuilderSetLayeringStateMethod = Misc.getMethodReflection(class_1921.class_4688.class_4689.class, "setLayeringState", "method_23607", "(Lnet/minecraft/class_4668$class_4675;)Lnet/minecraft/class_1921$class_4688$class_4689;", "m_110669_", class_4668.class_4675.class);
            compositeStateBuilderSetOutputStateMethod = Misc.getMethodReflection(class_1921.class_4688.class_4689.class, "setOutputState", "method_23610", "(Lnet/minecraft/class_4668$class_4678;)Lnet/minecraft/class_1921$class_4688$class_4689;", "m_110675_", class_4668.class_4678.class);
            compositeStateBuilderSetTexturingStateMethod = Misc.getMethodReflection(class_1921.class_4688.class_4689.class, "setTexturingState", "method_23614", "(Lnet/minecraft/class_4668$class_4684;)Lnet/minecraft/class_1921$class_4688$class_4689;", "m_110683_", class_4668.class_4684.class);
            compositeStateBuilderSetLineStateMethod = Misc.getMethodReflection(class_1921.class_4688.class_4689.class, "setLineState", "method_23609", "(Lnet/minecraft/class_4668$class_4677;)Lnet/minecraft/class_1921$class_4688$class_4689;", "m_110673_", class_4668.class_4677.class);
            renderTypeCreateMethod = Misc.getMethodReflection(class_1921.class, "create", "method_24049", "(Ljava/lang/String;IZZLcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_1921$class_4688;)Lnet/minecraft/class_1921$class_4687;", "m_173215_", String.class, Integer.TYPE, Boolean.TYPE, Boolean.TYPE, RenderPipeline.class, class_1921.class_4688.class);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        RenderPipeline.Snippet MATRICES_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withUniform("DynamicTransforms", class_10789.field_60031).withUniform("Projection", class_10789.field_60031).buildSnippet();
        RenderPipeline.Snippet POSITION_COLOR_TEX_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(MapShaders.POSITION_COLOR_TEX).withFragmentShader(MapShaders.POSITION_COLOR_TEX).withVertexFormat(POSITION_COLOR_TEX, VertexFormat.class_5596.field_27382).withSampler("Sampler0").buildSnippet();
        RenderPipeline.Snippet POSITION_COLOR_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(MapShaders.POSITION_COLOR).withFragmentShader(MapShaders.POSITION_COLOR).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).buildSnippet();
        RP_POSITION_COLOR_TEX = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/pos_col_tex")).withBlend(TRANSLUCENT_TRANSPARENCY).build();
        RP_POSITION_COLOR_TEX_NO_BLEND = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/pos_col_tex_no_blend")).withoutBlend().build();
        RP_POSITION_COLOR_TEX_NO_CULL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/pos_col_tex_no_cull")).withBlend(TRANSLUCENT_TRANSPARENCY).withCull(false).build();
        RP_POSITION_COLOR = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/pos_col")).withBlend(TRANSLUCENT_TRANSPARENCY).build();
        RP_POSITION_COLOR_NO_BLEND = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/pos_col_no_blend")).withoutBlend().build();
        RP_POSITION_COLOR_NO_CULL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/pos_col_no_cull")).withBlend(TRANSLUCENT_TRANSPARENCY).withCull(false).build();
        RP_MAP_BRANCH = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/map_branch")).withVertexShader(MapShaders.WORLD_MAP_BRANCH).withFragmentShader(MapShaders.WORLD_MAP_BRANCH).withVertexFormat(class_290.field_1585, VertexFormat.class_5596.field_27382).withSampler("Sampler0").withoutBlend().withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).build();
        GUI = CustomRenderTypes.createRenderType("xaero_wm_gui", 786432, false, false, RP_POSITION_COLOR_TEX_NO_CULL, CustomRenderTypes.getStateBuilder().setTextureState((class_4668.class_5939)new class_4668.class_4683(WorldMap.guiTextures, false)).setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        RenderPipeline POSITION_COLOR_TEX_PREMULTIPLIED_PL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/pos_col_tex_premultiplied")).withBlend(PREMULTIPLIED_TRANSPARENCY).withCull(false).build();
        GUI_PREMULTIPLIED = CustomRenderTypes.createRenderType("xaero_wm_gui_pre", 786432, false, false, POSITION_COLOR_TEX_PREMULTIPLIED_PL, CustomRenderTypes.getStateBuilder().setTextureState((class_4668.class_5939)new class_4668.class_4683(WorldMap.guiTextures, false)).setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        RenderPipeline MAP_PL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/map")).withVertexShader(MapShaders.WORLD_MAP).withFragmentShader(MapShaders.WORLD_MAP).withVertexFormat(class_290.field_1585, VertexFormat.class_5596.field_27382).withSampler("Sampler0").withUniform(BuiltInCustomUniforms.BRIGHTNESS.name(), BuiltInCustomUniforms.BRIGHTNESS.type()).withUniform(BuiltInCustomUniforms.WITH_LIGHT.name(), BuiltInCustomUniforms.WITH_LIGHT.type()).withBlend(DEST_TRANSPARENCY).withCull(false).build();
        MAP = CustomRenderTypes.createRenderType("xaero_wm_map_with_light", 786432, false, false, MAP_PL, CustomRenderTypes.getStateBuilder().setTextureState((class_4668.class_5939)new class_4668.class_4683(WorldMap.guiTextures, false)).setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        MAP_BRANCH = CustomRenderTypes.createRenderType("xaero_wm_map_branch", 1536, false, false, RP_MAP_BRANCH, CustomRenderTypes.getStateBuilder().setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        MAP_COLOR_OVERLAY = CustomRenderTypes.createRenderType("xaero_wm_world_map_overlay", 786432, false, false, RP_POSITION_COLOR_NO_CULL, CustomRenderTypes.getStateBuilder().setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        RenderPipeline MAP_FRAME_PL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/map_frame")).withBlend(DEST_TRANSPARENCY).withCull(false).withDepthWrite(false).build();
        MAP_FRAME = CustomRenderTypes.createRenderType("xaero_wm_frame_texture", 1536, false, false, MAP_FRAME_PL, CustomRenderTypes.getStateBuilder().setTextureState((class_4668.class_5939)new class_4668.class_4683(WorldMap.guiTextures, false)).setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        RenderPipeline COLOR_FILLER_PL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET}).withLocation(class_2960.method_60655((String)"xaeroworldmap", (String)"pipeline/color_filler")).withBlend(TRANSLUCENT_TRANSPARENCY).withDepthWrite(false).build();
        MAP_COLOR_FILLER = CustomRenderTypes.createRenderType("xaero_wm_world_map_filler", 1536, false, false, COLOR_FILLER_PL, CustomRenderTypes.getStateBuilder().setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        MAP_ELEMENT_TEXT_BG = CustomRenderTypes.createRenderType("xaero_wm_world_map_waypoint_name_bg", 786432, false, false, RP_POSITION_COLOR, CustomRenderTypes.getStateBuilder().setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
    }

    private static final class CustomStateBuilder {
        private final class_1921.class_4688.class_4689 original;
        private class_4668.class_4678 outputState = class_4668.field_21358;

        private CustomStateBuilder(class_1921.class_4688.class_4689 original) {
            this.original = original;
        }

        private CustomStateBuilder setTextureState(class_4668.class_5939 textureState) {
            Misc.getReflectMethodValue(this.original, compositeStateBuilderSetTextureStateMethod, textureState);
            return this;
        }

        private CustomStateBuilder setLightmapState(class_4668.class_4676 lightmapState) {
            Misc.getReflectMethodValue(this.original, compositeStateBuilderSetLightmapStateMethod, lightmapState);
            return this;
        }

        private CustomStateBuilder setOverlayState(class_4668.class_4679 overlayState) {
            Misc.getReflectMethodValue(this.original, compositeStateBuilderSetOverlayStateMethod, overlayState);
            return this;
        }

        private CustomStateBuilder setLayeringState(class_4668.class_4675 layeringState) {
            Misc.getReflectMethodValue(this.original, compositeStateBuilderSetLayeringStateMethod, layeringState);
            return this;
        }

        private CustomStateBuilder setOutputState(class_4668.class_4678 outputState) {
            Misc.getReflectMethodValue(this.original, compositeStateBuilderSetOutputStateMethod, outputState);
            this.outputState = outputState;
            return this;
        }

        private CustomStateBuilder setTexturingState(class_4668.class_4684 texturingState) {
            Misc.getReflectMethodValue(this.original, compositeStateBuilderSetTexturingStateMethod, texturingState);
            return this;
        }

        private CustomStateBuilder setLineState(class_4668.class_4677 lineState) {
            Misc.getReflectMethodValue(this.original, compositeStateBuilderSetLineStateMethod, lineState);
            return this;
        }

        public class_4668.class_4678 getOutputState() {
            return this.outputState;
        }

        private class_1921.class_4688 createCompositeState(class_1921.class_4750 outlineProperty) {
            return (class_1921.class_4688)Misc.getReflectMethodValue(this.original, compositeStateBuilderCreateCompositeStateMethod, outlineProperty);
        }
    }
}

