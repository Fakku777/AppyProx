/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_268
 */
package xaero.hud.minimap.radar.color;

import net.minecraft.class_1297;
import net.minecraft.class_268;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.color.RadarColor;

public class RadarColorHelper {
    public int getTeamColor(class_1297 e) {
        Integer teamColor = null;
        class_268 team = e.method_5781();
        if (team != null) {
            teamColor = team.method_1202().method_532();
        }
        return teamColor == null ? -1 : teamColor;
    }

    public int getEntityColor(class_1297 entity, float offY, boolean cave, int heightLimit, int startFadingAt, boolean heightBasedFade, RadarColor radarColor, RadarColor fallbackColor) {
        float heightFade;
        int color = this.getRadarColorHex(entity, radarColor, fallbackColor);
        float f = heightFade = heightBasedFade ? this.getEntityHeightFade(offY, heightLimit, startFadingAt) : 1.0f;
        if (heightFade >= 1.0f) {
            return color;
        }
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int alpha = 255;
        if (cave) {
            alpha = (int)((float)alpha * heightFade);
        } else {
            red = (int)((float)red * heightFade);
            green = (int)((float)green * heightFade);
            blue = (int)((float)blue * heightFade);
        }
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private int getRadarColorHex(class_1297 entity, RadarColor radarColor, RadarColor fallbackColor) {
        if (radarColor != null) {
            return radarColor.getHex();
        }
        int entityTeamColour = this.getTeamColor(entity);
        if (entityTeamColour != -1) {
            return 0xFF000000 | entityTeamColour;
        }
        return fallbackColor.getHex();
    }

    public RadarColor getFallbackColor(EntityRadarCategory category) {
        int colorSetting;
        EntityRadarCategory fallbackCategory = category;
        do {
            if ((fallbackCategory = (EntityRadarCategory)fallbackCategory.getSuperCategory()) != null) continue;
            return RadarColor.WHITE;
        } while ((colorSetting = fallbackCategory.getSettingValue(EntityRadarCategorySettings.COLOR).intValue()) == -1);
        return RadarColor.fromIndex(colorSetting);
    }

    public float getEntityHeightFade(float offY, int heightLimit, int startFadingAt) {
        int threshold;
        float level = (float)heightLimit - offY;
        if (level < 0.0f) {
            level = 0.0f;
        }
        float brightness = 1.0f;
        int n = threshold = startFadingAt == 0 ? heightLimit * 3 / 4 : heightLimit - startFadingAt;
        if (level <= (float)threshold) {
            brightness = 0.25f + 0.5f * level / (float)threshold;
        }
        return brightness;
    }
}

