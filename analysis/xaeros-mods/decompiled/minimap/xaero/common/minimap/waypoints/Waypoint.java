/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_243
 *  net.minecraft.class_4184
 *  org.joml.Vector3f
 */
package xaero.common.minimap.waypoints;

import net.minecraft.class_1074;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import org.joml.Vector3f;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.WaypointVisibilityType;

public class Waypoint
implements Comparable<Waypoint> {
    public static final int ONEOFF_DESTINATION_SAFE_FOR = 5000;
    public static final int ONEOFF_DESTINATION_REMOVE_DISTANCE = 4;
    public static class_243 RENDER_SORTING_POS = new class_243(0.0, 0.0, 0.0);
    private int x;
    private int y;
    private int z;
    private String name;
    private String initials;
    private WaypointColor color;
    private WaypointVisibilityType visibility;
    private boolean disabled;
    private WaypointPurpose purpose;
    private boolean rotation;
    private int yaw;
    private boolean temporary;
    private boolean yIncluded;
    private final long createdAt;
    @Deprecated
    private int actualColor;

    @Deprecated
    public Waypoint(int x, int y, int z, String name, String initials, int color) {
        this(x, y, z, name, initials, color, 0, false);
    }

    @Deprecated
    public Waypoint(int x, int y, int z, String name, String initials, int color, int type) {
        this(x, y, z, name, initials, color, type, false);
    }

    @Deprecated
    public Waypoint(int x, int y, int z, String name, String initials, int color, int type, boolean temp) {
        this(x, y, z, name, initials, color, type, temp, true);
    }

    @Deprecated
    public Waypoint(int x, int y, int z, String name, String initials, int color, int type, boolean temp, boolean yIncluded) {
        this(x, y, z, name, initials, WaypointColor.fromIndex(color), WaypointPurpose.values()[type], temp, yIncluded);
        this.actualColor = color;
    }

    public Waypoint(int x, int y, int z, String name, String initials, WaypointColor color) {
        this(x, y, z, name, initials, color, WaypointPurpose.NORMAL, false);
    }

    public Waypoint(int x, int y, int z, String name, String initials, WaypointColor color, WaypointPurpose purpose) {
        this(x, y, z, name, initials, color, purpose, false);
    }

    public Waypoint(int x, int y, int z, String name, String initials, WaypointColor color, WaypointPurpose purpose, boolean temp) {
        this(x, y, z, name, initials, color, purpose, temp, true);
    }

    public Waypoint(int x, int y, int z, String name, String initials, WaypointColor color, WaypointPurpose purpose, boolean temp, boolean yIncluded) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.initials = initials;
        this.color = color;
        this.purpose = purpose;
        this.name = name;
        this.temporary = temp;
        this.visibility = WaypointVisibilityType.LOCAL;
        if (this.purpose.isDeath()) {
            this.visibility = WaypointVisibilityType.GLOBAL;
        }
        this.yIncluded = yIncluded;
        this.createdAt = System.currentTimeMillis();
        this.actualColor = color.ordinal();
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX(double dimDiv) {
        if (dimDiv == 1.0) {
            return this.x;
        }
        return (int)Math.floor((double)this.x / dimDiv);
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getZ(double dimDiv) {
        if (dimDiv == 1.0) {
            return this.z;
        }
        return (int)Math.floor((double)this.z / dimDiv);
    }

    public String getName() {
        return this.name;
    }

    public String getLocalizedName() {
        return class_1074.method_4662((String)this.name, (Object[])new Object[0]);
    }

    public String getNameSafe(String replacement) {
        return this.getName().replace(":", replacement);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitials() {
        return this.initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    @Deprecated
    public String getSymbol() {
        return this.getInitials();
    }

    @Deprecated
    public void setSymbol(String symbol) {
        this.setInitials(symbol);
    }

    @Deprecated
    public String getSymbolSafe(String replacement) {
        return this.getInitialsSafe(replacement);
    }

    public String getInitialsSafe(String replacement) {
        return this.getInitials().replace(":", replacement);
    }

    @Deprecated
    public int getColor() {
        return this.getWaypointColor().ordinal();
    }

    @Deprecated
    public int getActualColor() {
        return this.actualColor;
    }

    @Deprecated
    public void setColor(int c) {
        this.setWaypointColor(WaypointColor.fromIndex(c));
        this.actualColor = c;
    }

    public WaypointColor getWaypointColor() {
        return this.color;
    }

    public void setWaypointColor(WaypointColor c) {
        this.color = c;
        this.actualColor = c.ordinal();
    }

    public boolean isGlobal() {
        return this.visibility.isGlobal();
    }

    @Deprecated
    public int getVisibilityType() {
        return this.getVisibility().ordinal();
    }

    public WaypointVisibilityType getVisibility() {
        return this.visibility;
    }

    @Deprecated
    public void setVisibilityType(int visibility) {
        this.setVisibility(WaypointVisibilityType.values()[visibility]);
    }

    public void setVisibility(WaypointVisibilityType visibility) {
        if (this.purpose != WaypointPurpose.DEATH) {
            this.visibility = visibility;
        }
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean b) {
        this.temporary = false;
        this.disabled = b;
    }

    @Deprecated
    public int getWaypointType() {
        return this.purpose.ordinal();
    }

    @Deprecated
    public void setType(int type) {
        this.setPurpose(WaypointPurpose.values()[type]);
    }

    public WaypointPurpose getPurpose() {
        return this.purpose;
    }

    public void setPurpose(WaypointPurpose purpose) {
        this.purpose = purpose;
        if (this.purpose.isDeath()) {
            this.visibility = WaypointVisibilityType.GLOBAL;
        }
    }

    public boolean isRotation() {
        return this.rotation;
    }

    public void setRotation(boolean rotation) {
        this.rotation = rotation;
    }

    public int getYaw() {
        return this.yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public boolean isTemporary() {
        return this.temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
        this.disabled = false;
    }

    public boolean isYIncluded() {
        return this.yIncluded;
    }

    public void setYIncluded(boolean yIncluded) {
        this.yIncluded = yIncluded;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    @Deprecated
    public boolean isOneoffDestination() {
        return this.purpose.isDestination();
    }

    @Deprecated
    public void setOneoffDestination(boolean oneoffDestination) {
        if (oneoffDestination) {
            if (this.purpose == WaypointPurpose.NORMAL) {
                this.purpose = WaypointPurpose.DESTINATION;
            }
            return;
        }
        if (this.purpose != WaypointPurpose.DESTINATION) {
            return;
        }
        this.purpose = WaypointPurpose.NORMAL;
    }

    public boolean isDestination() {
        return this.purpose.isDestination();
    }

    public double getDistanceSq(double x, double y, double z) {
        double offX = (double)this.x - x;
        double offY = this.yIncluded ? (double)this.y - y : 0.0;
        double offZ = (double)this.z - z;
        return offX * offX + offY * offY + offZ * offZ;
    }

    public static String getStringFromStringSafe(String stringSafe, String replacement) {
        return stringSafe.replace(replacement, ":");
    }

    public boolean isServerWaypoint() {
        return false;
    }

    public String getComparisonName() {
        String comparisonName = this.getLocalizedName().toLowerCase().trim();
        if (comparisonName.startsWith("the ")) {
            return comparisonName.substring(4);
        }
        if (comparisonName.startsWith("a ")) {
            return comparisonName.substring(2);
        }
        return comparisonName;
    }

    public double getComparisonDistance(class_4184 camera, double dimDiv) {
        class_243 cameraPos = camera.method_19326();
        double offX = (double)this.getX(dimDiv) - cameraPos.field_1352;
        double offY = !this.isYIncluded() ? 0.0 : (double)this.getY() - cameraPos.field_1351;
        double offZ = (double)this.getZ(dimDiv) - cameraPos.field_1350;
        return offX * offX + offY * offY + offZ * offZ;
    }

    public double getComparisonAngleCos(class_4184 camera, double dimDiv) {
        Vector3f lookVector = camera.method_19335();
        class_243 cameraPos = camera.method_19326();
        double offX = (double)this.getX(dimDiv) - cameraPos.field_1352;
        double offY = !this.isYIncluded() ? 0.0 : (double)this.getY() - cameraPos.field_1351;
        double offZ = (double)this.getZ(dimDiv) - cameraPos.field_1350;
        double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
        return (offX * (double)lookVector.x() + offY * (double)lookVector.y() + offZ * (double)lookVector.z()) / distance;
    }

    private double getRenderSortingDistanceSquared() {
        double fromCameraX = (double)this.x - Waypoint.RENDER_SORTING_POS.field_1352;
        double fromCameraY = this.yIncluded ? (double)this.y - Waypoint.RENDER_SORTING_POS.field_1351 : 0.0;
        double fromCameraZ = (double)this.z - Waypoint.RENDER_SORTING_POS.field_1350;
        return fromCameraX * fromCameraX + fromCameraY * fromCameraY + fromCameraZ * fromCameraZ;
    }

    @Override
    public int compareTo(Waypoint other) {
        double otherRsds;
        boolean isDeath = this.purpose.isDeath();
        if (isDeath != other.purpose.isDeath()) {
            return isDeath ? 1 : -1;
        }
        double rsds = this.getRenderSortingDistanceSquared();
        return rsds > (otherRsds = other.getRenderSortingDistanceSquared()) ? -1 : (rsds == otherRsds ? 0 : 1);
    }
}

