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
 *  net.minecraft.class_10799
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
package xaero.common.graphics;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.lang.reflect.Method;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_1921;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_4668;
import net.minecraft.class_9801;
import xaero.common.graphics.ImprovedCompositeRenderType;
import xaero.common.graphics.shader.BuiltInCustomUniforms;
import xaero.common.graphics.shader.MinimapShaders;
import xaero.common.misc.Misc;
import xaero.hud.render.TextureLocations;

public class CustomRenderTypes
extends class_1921 {
    public static final VertexFormat POSITION_COLOR_TEX = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV0", VertexFormatElement.UV0).build();
    protected static final BlendFunction TRANSLUCENT_TRANSPARENCY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    protected static final BlendFunction LINES_TRANSPARENCY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    protected static final BlendFunction PREMULTIPLIED_TRANSPARENCY = new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    protected static final BlendFunction NEGATIVE_TRANSPARENCY = new BlendFunction(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ZERO, SourceFactor.ZERO, DestFactor.ONE);
    protected static final BlendFunction REPLACE_TRANSPARENCY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ZERO, SourceFactor.ONE, DestFactor.ZERO);
    protected static final BlendFunction ADD_ALPHA_TRANSPARENCY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE);
    public static final RenderPipeline RP_POSITION_TEX_ALPHA;
    public static final RenderPipeline RP_POSITION_TEX_ALPHA_NO_BLEND;
    public static final RenderPipeline RP_POSITION_TEX_NO_ALPHA;
    public static final RenderPipeline RP_POSITION_TEX_NO_ALPHA_NO_BLEND;
    public static final RenderPipeline RP_POSITION_TEX_ALPHA_REPLACE;
    public static final RenderPipeline RP_POSITION_TEX_ALPHA_NO_CULL;
    public static final RenderPipeline RP_POSITION;
    public static final RenderPipeline RP_NEGATIVE_COLOR;
    public static final RenderPipeline RP_ICON_OUTLINE;
    public static final RenderPipeline RP_DEPTH_CLEAR;
    public static final class_1921 GUI;
    public static final class_1921 GUI_PREMULTIPLIED;
    public static final class_1921 COLORED_WAYPOINTS_BGS;
    public static final class_1921 MAP_CHUNK_OVERLAY;
    public static final class_1921 MAP_LINES;
    public static final class_1921 RADAR_NAME_BGS;
    public static final class_1921 DEPTH_CLEAR;
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

    public static class_1921 entityIconRenderType(class_2960 texture, RenderPipeline renderPipeline) {
        return CustomRenderTypes.createRenderType("xaero_entity_icon", 1536, true, true, renderPipeline, CustomRenderTypes.getStateBuilder().setTextureState((class_4668.class_5939)new class_4668.class_4683(texture, false)).setOverlayState(class_4668.field_21385).setLightmapState(class_4668.field_21384).setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
    }

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

    public static RenderPipeline getBasicRenderPipeline() {
        return class_10799.field_56906;
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
        RenderPipeline.Snippet MATRICES_FOG_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withUniform("Fog", class_10789.field_60031).buildSnippet();
        RenderPipeline.Snippet POSITION_TEX_NO_ALPHA = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(MinimapShaders.POSITION_TEX_NO_ALPHA_TEST).withFragmentShader(MinimapShaders.POSITION_TEX_NO_ALPHA_TEST).withVertexFormat(class_290.field_1585, VertexFormat.class_5596.field_27382).withSampler("Sampler0").buildSnippet();
        RP_POSITION_TEX_NO_ALPHA = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_TEX_NO_ALPHA}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_tex_no_alpha")).withBlend(TRANSLUCENT_TRANSPARENCY).build();
        RP_POSITION_TEX_NO_ALPHA_NO_BLEND = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_TEX_NO_ALPHA}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_tex_no_alpha_no_blend")).withoutBlend().build();
        RenderPipeline.Snippet RP_POSITION_TEX_ALPHA_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(MinimapShaders.POSITION_TEX_ALPHA_TEST).withFragmentShader(MinimapShaders.POSITION_TEX_ALPHA_TEST).withUniform(BuiltInCustomUniforms.DISCARD_ALPHA.name(), BuiltInCustomUniforms.DISCARD_ALPHA.type()).withVertexFormat(class_290.field_1585, VertexFormat.class_5596.field_27382).withSampler("Sampler0").buildSnippet();
        RP_POSITION_TEX_ALPHA = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_tex_alpha")).withBlend(TRANSLUCENT_TRANSPARENCY).build();
        RP_ICON_OUTLINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/icon_outline")).withBlend(ADD_ALPHA_TRANSPARENCY).build();
        RP_POSITION_TEX_ALPHA_NO_BLEND = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_tex_alpha_no_blend")).withoutBlend().build();
        RP_POSITION_TEX_ALPHA_REPLACE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_tex_alpha_replace")).withBlend(REPLACE_TRANSPARENCY).build();
        RP_POSITION_TEX_ALPHA_NO_CULL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_tex_alpha_no_cull")).withBlend(TRANSLUCENT_TRANSPARENCY).withCull(false).build();
        RP_POSITION = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_FOG_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos")).withVertexShader("core/position").withFragmentShader("core/position").withVertexFormat(class_290.field_1592, VertexFormat.class_5596.field_27382).withBlend(TRANSLUCENT_TRANSPARENCY).build();
        RP_NEGATIVE_COLOR = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_FOG_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_negative_color")).withVertexShader("core/position").withFragmentShader("core/position").withVertexFormat(class_290.field_1592, VertexFormat.class_5596.field_27382).withBlend(NEGATIVE_TRANSPARENCY).build();
        RenderPipeline.Snippet POSITION_COLOR_TEX_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(MinimapShaders.POSITION_COLOR_TEX).withFragmentShader(MinimapShaders.POSITION_COLOR_TEX).withVertexFormat(POSITION_COLOR_TEX, VertexFormat.class_5596.field_27382).withSampler("Sampler0").buildSnippet();
        RenderPipeline.Snippet POSITION_COLOR_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(MinimapShaders.POSITION_COLOR).withFragmentShader(MinimapShaders.POSITION_COLOR).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).buildSnippet();
        RenderPipeline.Snippet POSITION_COLOR_NO_ALPHA_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(MinimapShaders.POSITION_COLOR_NO_ALPHA_TEST).withFragmentShader(MinimapShaders.POSITION_COLOR_NO_ALPHA_TEST).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).buildSnippet();
        RenderPipeline POSITION_COLOR_TEX_TRANSLUCENT_PL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_col_text_translucent")).withBlend(TRANSLUCENT_TRANSPARENCY).withCull(false).build();
        GUI = CustomRenderTypes.createRenderType("xaero_gui", 786432, false, false, POSITION_COLOR_TEX_TRANSLUCENT_PL, CustomRenderTypes.getStateBuilder().setTextureState((class_4668.class_5939)new class_4668.class_4683(TextureLocations.GUI_TEXTURES, false)).setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        RenderPipeline POSITION_COLOR_TEX_PREMULTIPLIED_PL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_col_text_premultiplied")).withBlend(PREMULTIPLIED_TRANSPARENCY).withCull(false).build();
        GUI_PREMULTIPLIED = CustomRenderTypes.createRenderType("xaero_gui_pre", 786432, false, false, POSITION_COLOR_TEX_PREMULTIPLIED_PL, CustomRenderTypes.getStateBuilder().setTextureState((class_4668.class_5939)new class_4668.class_4683(TextureLocations.GUI_TEXTURES, false)).setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        RenderPipeline POSITION_COLOR_TRANSLUCENT_PL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/pos_col_translucent")).withBlend(TRANSLUCENT_TRANSPARENCY).build();
        COLORED_WAYPOINTS_BGS = CustomRenderTypes.createRenderType("xaero_colored_waypoints", 786432, false, false, POSITION_COLOR_TRANSLUCENT_PL, CustomRenderTypes.getStateBuilder().setLayeringState(class_4668.field_53123).setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        RADAR_NAME_BGS = CustomRenderTypes.createRenderType("xaero_radar_name_bg", 786432, false, false, POSITION_COLOR_TRANSLUCENT_PL, CustomRenderTypes.getStateBuilder().setLayeringState(class_4668.field_53123).setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        MAP_CHUNK_OVERLAY = CustomRenderTypes.createRenderType("xaero_chunk_overlay", 786432, false, false, POSITION_COLOR_TRANSLUCENT_PL, CustomRenderTypes.getStateBuilder().setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        RenderPipeline LINES_PL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/lines")).withVertexShader(MinimapShaders.FRAMEBUFFER_LINES).withFragmentShader(MinimapShaders.FRAMEBUFFER_LINES).withUniform(BuiltInCustomUniforms.FRAME_SIZE.name(), BuiltInCustomUniforms.FRAME_SIZE.type()).withVertexFormat(class_290.field_29337, VertexFormat.class_5596.field_27377).withBlend(LINES_TRANSPARENCY).build();
        MAP_LINES = CustomRenderTypes.createRenderType("xaero_lines", 1536, false, false, LINES_PL, CustomRenderTypes.getStateBuilder().setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
        RP_DEPTH_CLEAR = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_NO_ALPHA_SNIPPET}).withLocation(class_2960.method_60655((String)"xaerominimap", (String)"pipeline/depth_clear")).withBlend(TRANSLUCENT_TRANSPARENCY).withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST).withColorWrite(false, false).build();
        DEPTH_CLEAR = CustomRenderTypes.createRenderType("xaero_depth_clear", 1536, false, false, RP_DEPTH_CLEAR, CustomRenderTypes.getStateBuilder().setOutputState(class_4668.field_21358), class_1921.class_4750.field_21853);
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

