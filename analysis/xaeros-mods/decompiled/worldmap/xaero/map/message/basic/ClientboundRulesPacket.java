/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2487
 *  net.minecraft.class_2520
 *  net.minecraft.class_2540
 */
package xaero.map.message.basic;

import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_2540;
import xaero.map.mcworld.WorldMapClientWorldDataHelper;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.client.ClientMessageConsumer;

public class ClientboundRulesPacket
extends WorldMapMessage<ClientboundRulesPacket> {
    public final boolean allowCaveModeOnServer;
    public final boolean allowNetherCaveModeOnServer;

    public ClientboundRulesPacket(boolean allowCaveModeOnServer, boolean allowNetherCaveModeOnServer) {
        this.allowCaveModeOnServer = allowCaveModeOnServer;
        this.allowNetherCaveModeOnServer = allowNetherCaveModeOnServer;
    }

    public void write(class_2540 u) {
        class_2487 nbt = new class_2487();
        nbt.method_10556("cm", this.allowCaveModeOnServer);
        nbt.method_10556("ncm", this.allowNetherCaveModeOnServer);
        u.method_10794((class_2520)nbt);
    }

    public static ClientboundRulesPacket read(class_2540 buffer) {
        class_2487 nbt = buffer.method_10798();
        return new ClientboundRulesPacket(nbt.method_10577("cm").orElse(false), nbt.method_10577("ncm").orElse(false));
    }

    public static class ClientHandler
    implements ClientMessageConsumer<ClientboundRulesPacket> {
        @Override
        public void handle(ClientboundRulesPacket message) {
            WorldMapClientWorldDataHelper.getCurrentWorldData().setSyncedRules(message);
        }
    }
}

