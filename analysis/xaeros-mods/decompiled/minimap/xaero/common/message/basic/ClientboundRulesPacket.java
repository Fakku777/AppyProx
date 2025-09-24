/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2487
 *  net.minecraft.class_2520
 *  net.minecraft.class_2540
 */
package xaero.common.message.basic;

import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_2540;
import xaero.common.message.MinimapMessage;
import xaero.common.message.client.ClientMessageConsumer;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;

public class ClientboundRulesPacket
extends MinimapMessage<ClientboundRulesPacket> {
    public final boolean allowCaveModeOnServer;
    public final boolean allowNetherCaveModeOnServer;
    public final boolean allowRadarOnServer;

    public ClientboundRulesPacket(boolean allowCaveModeOnServer, boolean allowNetherCaveModeOnServer, boolean allowRadarOnServer) {
        this.allowCaveModeOnServer = allowCaveModeOnServer;
        this.allowNetherCaveModeOnServer = allowNetherCaveModeOnServer;
        this.allowRadarOnServer = allowRadarOnServer;
    }

    public void write(class_2540 u) {
        class_2487 nbt = new class_2487();
        nbt.method_10556("cm", this.allowCaveModeOnServer);
        nbt.method_10556("ncm", this.allowNetherCaveModeOnServer);
        nbt.method_10556("r", this.allowRadarOnServer);
        u.method_10794((class_2520)nbt);
    }

    public static ClientboundRulesPacket read(class_2540 buffer) {
        class_2487 nbt = buffer.method_10798();
        return new ClientboundRulesPacket(nbt.method_10577("cm").orElse(false), nbt.method_10577("ncm").orElse(false), nbt.method_10577("r").orElse(false));
    }

    public static class ClientHandler
    implements ClientMessageConsumer<ClientboundRulesPacket> {
        @Override
        public void handle(ClientboundRulesPacket message) {
            MinimapClientWorldDataHelper.getCurrentWorldData().setSyncedRules(message);
        }
    }
}

