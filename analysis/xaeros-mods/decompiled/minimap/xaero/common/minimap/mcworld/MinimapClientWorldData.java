/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_638
 *  net.minecraft.class_7134
 */
package xaero.common.minimap.mcworld;

import net.minecraft.class_638;
import net.minecraft.class_7134;
import xaero.common.message.basic.ClientboundRulesPacket;

public class MinimapClientWorldData {
    private int serverModNetworkVersion;
    public Integer serverLevelId;
    public float shadowR = 1.0f;
    public float shadowG = 1.0f;
    public float shadowB = 1.0f;
    private ClientboundRulesPacket syncedRules;

    public MinimapClientWorldData(class_638 world) {
        if (world.method_8597().comp_655().equals((Object)class_7134.field_37670)) {
            this.shadowR = 0.518f;
            this.shadowG = 0.678f;
            this.shadowB = 1.0f;
        } else if (world.method_8597().comp_655().equals((Object)class_7134.field_37671)) {
            this.shadowR = 1.0f;
            this.shadowG = 0.0f;
            this.shadowB = 0.0f;
        }
    }

    public void setServerModNetworkVersion(int serverModNetworkVersion) {
        this.serverModNetworkVersion = serverModNetworkVersion;
    }

    public int getServerModNetworkVersion() {
        return this.serverModNetworkVersion;
    }

    public void setSyncedRules(ClientboundRulesPacket syncedRules) {
        this.syncedRules = syncedRules;
    }

    public ClientboundRulesPacket getSyncedRules() {
        if (this.syncedRules == null) {
            this.syncedRules = new ClientboundRulesPacket(true, true, true);
        }
        return this.syncedRules;
    }
}

