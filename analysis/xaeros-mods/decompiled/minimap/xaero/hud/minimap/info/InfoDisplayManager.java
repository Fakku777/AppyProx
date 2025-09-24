/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import xaero.hud.minimap.info.InfoDisplay;

public final class InfoDisplayManager {
    private final Map<String, InfoDisplay<?>> displays;
    private final List<String> defaultOrder;
    private List<String> order;

    private InfoDisplayManager(Map<String, InfoDisplay<?>> displays, List<String> defaultOrder) {
        this.displays = displays;
        this.defaultOrder = defaultOrder;
    }

    public void add(InfoDisplay<?> infoDisplay) {
        if (this.displays.put(infoDisplay.getId(), infoDisplay) == null) {
            this.defaultOrder.add(infoDisplay.getId());
        }
    }

    public void setOrder(List<String> order) {
        this.order = new ArrayList<String>(order);
        int lastDefaultOrderIdIndex = -1;
        for (int i = 0; i < this.defaultOrder.size(); ++i) {
            String defaultOrderId = this.defaultOrder.get(i);
            int defaultOrderIdIndex = this.order.indexOf(defaultOrderId);
            if (defaultOrderIdIndex == -1) {
                defaultOrderIdIndex = lastDefaultOrderIdIndex != -1 ? lastDefaultOrderIdIndex + 1 : 0;
                this.order.add(defaultOrderIdIndex, defaultOrderId);
            }
            lastDefaultOrderIdIndex = defaultOrderIdIndex;
        }
    }

    public InfoDisplay<?> get(String id) {
        return this.displays.get(id);
    }

    public Stream<InfoDisplay<?>> getOrderedStream() {
        Stream<InfoDisplay> unfilteredResult = this.order.stream().map(this.displays::get);
        return unfilteredResult.filter(Objects::nonNull);
    }

    public int getCount() {
        return this.displays.size();
    }

    public void reset() {
        this.setOrder(new ArrayList<String>());
        this.getOrderedStream().forEach(InfoDisplay::reset);
    }

    public static final class Builder {
        private Builder() {
        }

        private Builder setDefault() {
            return this;
        }

        public InfoDisplayManager build() {
            return new InfoDisplayManager(new HashMap(), new ArrayList<String>());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

