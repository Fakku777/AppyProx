/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.class_1074
 *  net.minecraft.class_1944
 *  net.minecraft.class_1959
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_6880
 *  net.minecraft.class_7924
 */
package xaero.hud.minimap.info;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_1074;
import net.minecraft.class_1944;
import net.minecraft.class_1959;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.codec.InfoDisplayCommonStateCodecs;
import xaero.hud.minimap.info.widget.InfoDisplayCommonWidgetFactories;
import xaero.hud.minimap.info.widget.InfoDisplayCycleWidgetFactory;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;

public class BuiltInInfoDisplays {
    private static List<InfoDisplay<?>> ALL = new ArrayList();
    public static final InfoDisplay<Boolean> COORDINATES = InfoDisplay.Builder.begin().setId("coords").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_coords")).setDefaultState(true).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        String coords = playerPos.method_10263() + ", " + playerPos.method_10264() + ", " + playerPos.method_10260();
        if (class_310.method_1551().field_1772.method_1727(coords) >= availableWidth) {
            String stringLevel = "" + playerPos.method_10264();
            coords = playerPos.method_10263() + ", " + playerPos.method_10260();
            compiler.addLine(coords);
            compiler.addLine(stringLevel);
        } else {
            compiler.addLine(coords);
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> OVERWORLD_COORDINATES = InfoDisplay.Builder.begin().setId("overworld_coords").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_overworld_coords")).setDefaultState(false).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        double coordinateScale = mc.field_1687.method_8597().comp_646();
        if (coordinateScale == 1.0) {
            return;
        }
        int overworldPlayerX = class_3532.method_15357((double)((double)playerPos.method_10263() * coordinateScale));
        int overworldPlayerZ = class_3532.method_15357((double)((double)playerPos.method_10260() * coordinateScale));
        String coords = "Xo: " + overworldPlayerX + ", Zo: " + overworldPlayerZ;
        compiler.addWords(coords);
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> CHUNK_COORDINATES = InfoDisplay.Builder.begin().setId("chunk_coords").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_chunk_coords")).setDefaultState(false).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        int chunkX = playerPos.method_10263() >> 4;
        int chunkZ = playerPos.method_10260() >> 4;
        int insideX = playerPos.method_10263() & 0xF;
        int insideZ = playerPos.method_10260() & 0xF;
        String coords = "C " + chunkX + ", " + chunkZ + " (" + insideX + "; " + insideZ + ")";
        compiler.addWords(coords);
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> ANGLES = InfoDisplay.Builder.begin().setId("angles").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_angles")).setDefaultState(false).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        compiler.addLine((class_2561)class_2561.method_43470((String)String.format("%.1f / %.1f", Float.valueOf(class_3532.method_15393((float)mc.method_1560().method_36454())), Float.valueOf(class_3532.method_15393((float)mc.method_1560().method_36455())))));
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> DIMENSION = InfoDisplay.Builder.begin().setId("dimension").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_dimension")).setDefaultState(false).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        class_2960 dimensionId = class_310.method_1551().field_1687.method_27983().method_29177();
        if (dimensionId != null) {
            String dimensionName = dimensionId.method_12836().equals("minecraft") ? dimensionId.method_12832() : dimensionId.toString();
            compiler.addLine(dimensionName);
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> BIOME = InfoDisplay.Builder.begin().setId("biome").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_biome")).setDefaultState(false).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        class_6880 biomeHolder = class_310.method_1551().field_1687.method_23753(playerPos);
        class_1959 biome = biomeHolder == null ? null : (class_1959)biomeHolder.comp_349();
        class_2960 biomeRL = biome == null ? null : class_310.method_1551().field_1687.method_30349().method_30530(class_7924.field_41236).method_10221((Object)biome);
        String biomeText = biomeRL == null ? class_1074.method_4662((String)"gui.xaero_unknown_biome", (Object[])new Object[0]) : class_1074.method_4662((String)("biome." + biomeRL.method_12836() + "." + biomeRL.method_12832()), (Object[])new Object[0]);
        compiler.addWords(biomeText);
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> WEATHER = InfoDisplay.Builder.begin().setId("weather").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_weather")).setDefaultState(false).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        if (class_310.method_1551().field_1687.method_8546()) {
            compiler.addLine((class_2561)class_2561.method_43471((String)"gui.xaero_weather_thundering"));
        } else if (class_310.method_1551().field_1687.method_8419()) {
            compiler.addLine((class_2561)class_2561.method_43471((String)"gui.xaero_weather_raining"));
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Integer> LIGHT_LEVEL = InfoDisplay.Builder.begin().setId("light_level").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_light_level")).setDefaultState(0).setCodec(InfoDisplayCommonStateCodecs.INTEGER).setWidgetFactory(new InfoDisplayCycleWidgetFactory(Lists.newArrayList((Object[])new Integer[]{0, 1, 2, 3, 4}), Lists.newArrayList((Object[])new class_2561[]{class_2561.method_43471((String)"gui.xaero_off"), class_2561.method_43471((String)"gui.xaero_light_block"), class_2561.method_43471((String)"gui.xaero_light_sky"), class_2561.method_43471((String)"gui.xaero_light_all"), class_2561.method_43471((String)"gui.xaero_light_both2")}))).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        int showLightLevel = (Integer)displayInfo.getState();
        if (showLightLevel == 0) {
            return;
        }
        class_310 mc = class_310.method_1551();
        int blockLight = mc.field_1687.method_8314(class_1944.field_9282, playerPos);
        int skyLight = mc.field_1687.method_8314(class_1944.field_9284, playerPos);
        if (showLightLevel == 1) {
            compiler.addLine((class_2561)class_2561.method_43469((String)"gui.xaero_block_light_value", (Object[])new Object[]{blockLight}));
        } else if (showLightLevel == 2) {
            compiler.addLine((class_2561)class_2561.method_43469((String)"gui.xaero_sky_light_value", (Object[])new Object[]{skyLight}));
        } else if (showLightLevel == 3) {
            compiler.addLine((class_2561)class_2561.method_43469((String)"gui.xaero_all_light_value", (Object[])new Object[]{Math.max(blockLight, skyLight)}));
        } else {
            compiler.addLine((class_2561)class_2561.method_43469((String)"gui.xaero_both_light_value", (Object[])new Object[]{blockLight, skyLight}));
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Integer> TIME = InfoDisplay.Builder.begin().setId("time").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_time")).setDefaultState(0).setCodec(InfoDisplayCommonStateCodecs.INTEGER).setWidgetFactory(new InfoDisplayCycleWidgetFactory(Lists.newArrayList((Object[])new Integer[]{0, 1, 2, 3, 4}), Lists.newArrayList((Object[])new class_2561[]{class_2561.method_43471((String)"gui.xaero_off"), class_2561.method_43469((String)"%s+%s", (Object[])new Object[]{class_2561.method_43471((String)"gui.xaero_day"), class_2561.method_43471((String)"gui.xaero_24h")}), class_2561.method_43469((String)"%s+%s", (Object[])new Object[]{class_2561.method_43471((String)"gui.xaero_day"), class_2561.method_43471((String)"gui.xaero_12h")}), class_2561.method_43471((String)"gui.xaero_24h"), class_2561.method_43471((String)"gui.xaero_12h")}))).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        int showTime = (Integer)displayInfo.getState();
        if (showTime <= 0) {
            return;
        }
        class_310 mc = class_310.method_1551();
        long totalTime = 6000L + mc.field_1687.method_8532();
        int dayNumber = (int)(totalTime / 24000L) + 1;
        int dayTimeSinceMidnight = (int)(totalTime % 24000L);
        int timeHours = dayTimeSinceMidnight / 1000;
        int minutes = (int)((double)(dayTimeSinceMidnight % 1000) / 1000.0 * 60.0);
        if ((showTime - 1) % 2 == 0) {
            if (showTime < 3) {
                compiler.addWords(class_1074.method_4662((String)"gui.xaero_day", (Object[])new Object[0]) + String.format(" %d, %02d:%02d", dayNumber, timeHours, minutes));
            } else {
                compiler.addWords(String.format("%02d:%02d", timeHours, minutes));
            }
        } else {
            String half = "AM";
            if (timeHours >= 12) {
                timeHours -= 12;
                half = "PM";
            }
            if (timeHours == 0) {
                timeHours = 12;
            }
            if (showTime < 3) {
                compiler.addWords(class_1074.method_4662((String)"gui.xaero_day", (Object[])new Object[0]) + String.format(" %d, %02d:%02d %s", dayNumber, timeHours, minutes, half));
            } else {
                compiler.addWords(String.format("%02d:%02d %s", timeHours, minutes, half));
            }
        }
    }).setDestination(ALL::add).build();
    private static final Calendar CALENDAR = Calendar.getInstance();
    public static final InfoDisplay<Integer> REAL_TIME = InfoDisplay.Builder.begin().setId("real_time").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_real_time")).setDefaultState(0).setCodec(InfoDisplayCommonStateCodecs.INTEGER).setWidgetFactory(new InfoDisplayCycleWidgetFactory(Lists.newArrayList((Object[])new Integer[]{0, 1, 2}), Lists.newArrayList((Object[])new class_2561[]{class_2561.method_43471((String)"gui.xaero_off"), class_2561.method_43471((String)"gui.xaero_24h"), class_2561.method_43471((String)"gui.xaero_12h")}))).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        int showTime = (Integer)displayInfo.getState();
        if (showTime <= 0) {
            return;
        }
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        int timeHours = CALENDAR.get(11);
        int minutes = CALENDAR.get(12);
        if (showTime == 1) {
            compiler.addWords(String.format("%02d:%02d IRL", timeHours, minutes));
        } else {
            String half = "AM";
            if (timeHours >= 12) {
                timeHours -= 12;
                half = "PM";
            }
            if (timeHours == 0) {
                timeHours = 12;
            }
            compiler.addWords(String.format("%02d:%02d %s IRL", timeHours, minutes, half));
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> HIGHLIGHTS = InfoDisplay.Builder.begin().setId("highlights").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_highlights")).setDefaultState(true).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.ALWAYS_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        if (session.getProcessor().getMinimapWriter().getDimensionHighlightHandler() != null) {
            session.getProcessor().getMinimapWriter().getDimensionHighlightHandler().addBlockHighlightTooltips(compiler, playerPos.method_10263(), playerPos.method_10260(), availableWidth, true);
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> LIGHT_OVERLAY_INDICATOR = InfoDisplay.Builder.begin().setId("light_overlay_indicator").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_light_overlay_indicator")).setDefaultState(true).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.ALWAYS_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        if (session.getModMain().getSettings().lightOverlayType > 0) {
            compiler.addWords(class_1074.method_4662((String)"gui.xaero_light_overlay_status", (Object[])new Object[0]));
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> MANUAL_CAVE_MODE_INDICATOR = InfoDisplay.Builder.begin().setId("manual_cave_mode_indicator").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_manual_cave_mode_indicator")).setDefaultState(true).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.ALWAYS_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        if (session.getProcessor().isManualCaveMode()) {
            compiler.addWords(class_1074.method_4662((String)"gui.xaero_manual_cave_mode", (Object[])new Object[0]));
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> CUSTOM_SUB_WORLD = InfoDisplay.Builder.begin().setId("custom_sub_world").setName((class_2561)class_2561.method_43471((String)"gui.xaero_infodisplay_custom_sub_world")).setDefaultState(true).setCodec(InfoDisplayCommonStateCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.ALWAYS_ON).setCompiler((displayInfo, compiler, hudSession, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getState()).booleanValue()) {
            return;
        }
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorldManager minimapWorldManager = session.getWorldManager();
        MinimapWorld currentWorld = minimapWorldManager.getCurrentWorld();
        if (currentWorld != null && minimapWorldManager.getAutoWorld() != currentWorld) {
            compiler.addWords(class_1074.method_4662((String)"gui.xaero_using_custom_subworld", (Object[])new Object[]{currentWorld.getContainer().getSubName()}));
        }
    }).setDestination(ALL::add).build();

    public static void forEach(Consumer<InfoDisplay<?>> action) {
        ALL.forEach(action);
    }
}

