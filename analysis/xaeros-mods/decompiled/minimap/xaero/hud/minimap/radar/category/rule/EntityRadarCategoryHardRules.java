/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1309
 *  net.minecraft.class_1542
 *  net.minecraft.class_1657
 *  net.minecraft.class_1944
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package xaero.hud.minimap.radar.category.rule;

import java.util.List;
import java.util.Map;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1944;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.XaeroMinimapCore;
import xaero.hud.category.rule.ObjectCategoryHardRule;
import xaero.hud.minimap.radar.category.EntityRadarCategoryConstants;
import xaero.hud.minimap.radar.util.RadarUtils;

public final class EntityRadarCategoryHardRules {
    public static final List<ObjectCategoryHardRule<class_1297, class_1657>> HARD_RULES_LIST = EntityRadarCategoryConstants.LIST_FACTORY.get();
    public static final Map<String, ObjectCategoryHardRule<class_1297, class_1657>> HARD_RULES = EntityRadarCategoryConstants.MAP_FACTORY.get();
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_NOTHING = new ObjectCategoryHardRule.Builder().setName("nothing").setPredicate((e, p) -> false).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_ANYTHING = new ObjectCategoryHardRule.Builder().setName("anything").setPredicate((e, p) -> true).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_LIVING = new ObjectCategoryHardRule.Builder().setName("living").setPredicate((e, p) -> e instanceof class_1309).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_PLAYER = new ObjectCategoryHardRule.Builder().setName("players").setPredicate((e, p) -> e instanceof class_1657).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_SAME_TEAM = new ObjectCategoryHardRule.Builder().setName("same-team").setPredicate((e, p) -> p.method_5781() == e.method_5781()).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_HOSTILE = new ObjectCategoryHardRule.Builder().setName("hostile").setPredicate((e, p) -> RadarUtils.isHostile(e)).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_TAMED = new ObjectCategoryHardRule.Builder().setName("tamed").setPredicate((e, p) -> RadarUtils.isTamed(e, p)).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_ITEM = new ObjectCategoryHardRule.Builder().setName("items").setPredicate((e, p) -> e instanceof class_1542).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_FRIENDLY = new ObjectCategoryHardRule.Builder().setName("friendly").setPredicate((e, p) -> !IS_HOSTILE.isFollowedBy((class_1297)e, (class_1657)p)).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_OTHER_TEAMS = new ObjectCategoryHardRule.Builder().setName("other-teams").setPredicate((e, p) -> !IS_SAME_TEAM.isFollowedBy((class_1297)e, (class_1657)p)).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_BABY = new ObjectCategoryHardRule.Builder().setName("baby").setPredicate((e, p) -> e instanceof class_1309 && ((class_1309)e).method_6109()).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_VANILLA = new ObjectCategoryHardRule.Builder().setName("vanilla").setPredicate((e, p) -> {
        class_1299 type = e.method_5864();
        class_2960 resourceLocation = type == null ? null : class_1299.method_5890((class_1299)type);
        return resourceLocation == null ? false : resourceLocation.method_12836().equals("minecraft");
    }).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_MODDED = new ObjectCategoryHardRule.Builder().setName("modded").setPredicate((e, p) -> !IS_VANILLA.isFollowedBy((class_1297)e, (class_1657)p)).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_ABOVE_GROUND = new ObjectCategoryHardRule.Builder().setName("above-ground").setPredicate((e, p) -> e.method_37908().method_8314(class_1944.field_9284, e.method_24515()) == 15).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_BELOW_GROUND = new ObjectCategoryHardRule.Builder().setName("below-ground").setPredicate((e, p) -> !IS_ABOVE_GROUND.isFollowedBy((class_1297)e, (class_1657)p)).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_MY_GROUND = new ObjectCategoryHardRule.Builder().setName("my-ground").setPredicate((e, p) -> {
        if (class_310.method_1551().field_1719 == null) {
            return true;
        }
        boolean caveMode = XaeroMinimapSession.getCurrentSession().getMinimapProcessor().isCaveModeDisplayed();
        return IS_ABOVE_GROUND.isFollowedBy((class_1297)e, (class_1657)p) == !caveMode;
    }).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_NOT_MY_GROUND = new ObjectCategoryHardRule.Builder().setName("not-my-ground").setPredicate((e, p) -> !IS_MY_GROUND.isFollowedBy((class_1297)e, (class_1657)p)).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_LIT = new ObjectCategoryHardRule.Builder().setName("block-lit").setPredicate((e, p) -> e.method_37908().method_8314(class_1944.field_9282, e.method_24515()) > 0).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_UNLIT = new ObjectCategoryHardRule.Builder().setName("block-unlit").setPredicate((e, p) -> !IS_LIT.isFollowedBy((class_1297)e, (class_1657)p)).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> HAS_CUSTOM_NAME = new ObjectCategoryHardRule.Builder().setName("has-custom-name").setPredicate((e, p) -> e.method_16914()).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> NO_CUSTOM_NAME = new ObjectCategoryHardRule.Builder().setName("no-custom-name").setPredicate((e, p) -> !HAS_CUSTOM_NAME.isFollowedBy((class_1297)e, (class_1657)p)).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_TRACKED = new ObjectCategoryHardRule.Builder().setName("tracked").setPredicate((e, p) -> {
        if (!(e instanceof class_1657)) {
            return false;
        }
        return XaeroMinimapCore.modMain.getTrackedPlayerRenderer().getCollector().playerExists(e.method_5667());
    }).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_IN_TEAM = new ObjectCategoryHardRule.Builder().setName("in-a-team").setPredicate((e, p) -> e.method_5781() != null).build(HARD_RULES, HARD_RULES_LIST);
    public static final ObjectCategoryHardRule<class_1297, class_1657> IS_TEAMLESS = new ObjectCategoryHardRule.Builder().setName("teamless").setPredicate((e, p) -> !IS_IN_TEAM.isFollowedBy((class_1297)e, (class_1657)p)).build(HARD_RULES, HARD_RULES_LIST);
}

