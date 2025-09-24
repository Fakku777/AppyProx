/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_5250
 *  xaero.pac.common.claims.result.api.AreaClaimResult
 *  xaero.pac.common.claims.result.api.ClaimResult$Type
 */
package xaero.map.mods.pac.gui.claim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import xaero.map.gui.CursorBox;
import xaero.map.misc.Area;
import xaero.pac.common.claims.result.api.AreaClaimResult;
import xaero.pac.common.claims.result.api.ClaimResult;

public class ClaimResultElement {
    private final Area key;
    private final AreaClaimResult result;
    private final List<ClaimResult.Type> filteredResultTypes;
    private long fadeOutStartTime;
    private final long creationTime;
    private final CursorBox tooltip;
    private final boolean hasPositive;
    private final boolean hasNegative;

    private ClaimResultElement(Area key, AreaClaimResult result, List<ClaimResult.Type> filteredResultTypes, CursorBox tooltip, long fadeOutStartTime, long creationTime, boolean hasPositive, boolean hasNegative) {
        this.key = key;
        this.result = result;
        this.fadeOutStartTime = fadeOutStartTime;
        this.creationTime = creationTime;
        this.filteredResultTypes = filteredResultTypes;
        this.tooltip = tooltip;
        this.hasPositive = hasPositive;
        this.hasNegative = hasNegative;
    }

    public Area getKey() {
        return this.key;
    }

    public long getFadeOutStartTime() {
        return this.fadeOutStartTime;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public void setFadeOutStartTime(long fadeOutStartTime) {
        this.fadeOutStartTime = fadeOutStartTime;
    }

    public int getLeft() {
        return this.result.getLeft();
    }

    public int getTop() {
        return this.result.getTop();
    }

    public int getRight() {
        return this.result.getRight();
    }

    public int getBottom() {
        return this.result.getBottom();
    }

    public CursorBox getTooltip() {
        return this.tooltip;
    }

    public boolean hasNegative() {
        return this.hasNegative;
    }

    public boolean hasPositive() {
        return this.hasPositive;
    }

    public Iterator<ClaimResult.Type> getFilteredResultTypeIterator() {
        return this.filteredResultTypes.iterator();
    }

    public static final class Builder {
        private Area key;
        private AreaClaimResult result;

        private Builder() {
        }

        private Builder setDefault() {
            this.setResult(null);
            this.setKey(null);
            return this;
        }

        public Builder setKey(Area key) {
            this.key = key;
            return this;
        }

        public Builder setResult(AreaClaimResult result) {
            this.result = result;
            return this;
        }

        public ClaimResultElement build() {
            if (this.result == null || this.key == null) {
                throw new IllegalStateException();
            }
            long time = System.currentTimeMillis();
            class_5250 tooltipText = class_2561.method_43470((String)"");
            boolean hasPositive = false;
            boolean hasNegative = false;
            for (ClaimResult.Type type : this.result.getResultTypesIterable()) {
                if (type.success) {
                    hasPositive = true;
                }
                if (type.fail) {
                    hasNegative = true;
                }
                if (!hasPositive || !hasNegative) continue;
                break;
            }
            ArrayList<ClaimResult.Type> filteredResultTypes = new ArrayList<ClaimResult.Type>();
            boolean first = true;
            boolean filteredHasPositive = false;
            boolean filteredHasNegative = false;
            for (ClaimResult.Type type : this.result.getResultTypesIterable()) {
                if (hasPositive && !type.success && type != ClaimResult.Type.TOO_MANY_CHUNKS && type != ClaimResult.Type.TOO_FAR) continue;
                if (!first) {
                    tooltipText.method_10855().add(class_2561.method_43470((String)" \n "));
                }
                tooltipText.method_10855().add(type.message);
                filteredResultTypes.add(type);
                first = false;
                if (type.success) {
                    filteredHasPositive = true;
                }
                if (!type.fail) continue;
                filteredHasNegative = true;
            }
            CursorBox tooltip = new CursorBox((class_2561)tooltipText);
            return new ClaimResultElement(this.key, this.result, Collections.unmodifiableList(filteredResultTypes), tooltip, time, time, filteredHasPositive, filteredHasNegative);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

