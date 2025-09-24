/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.element;

public class MapElementMenuScroll {
    private String name;
    private String icon;
    private int direction;
    private long lastScroll;

    public MapElementMenuScroll(String name, String icon, int direction) {
        this.name = name;
        this.icon = icon;
        this.direction = direction;
    }

    public int getDirection() {
        return this.direction;
    }

    public int scroll() {
        long currentTime = System.currentTimeMillis();
        if (this.lastScroll == 0L || currentTime - this.lastScroll > 100L) {
            this.lastScroll = currentTime;
            return this.direction;
        }
        return 0;
    }

    public void onMouseRelease() {
        this.lastScroll = 0L;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }
}

