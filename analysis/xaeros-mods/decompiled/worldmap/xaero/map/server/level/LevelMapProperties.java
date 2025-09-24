/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 */
package xaero.map.server.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import net.minecraft.class_2540;
import xaero.map.message.WorldMapMessage;

public class LevelMapProperties
extends WorldMapMessage<LevelMapProperties> {
    private int id = new Random().nextInt();
    private boolean usable = true;

    public void write(PrintWriter writer) {
        writer.print("id:" + this.id);
    }

    public void read(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] args = line.split(":");
            if (!args[0].equals("id")) continue;
            try {
                this.id = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException numberFormatException) {}
        }
    }

    public boolean isUsable() {
        return this.usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    public int getId() {
        return this.id;
    }

    public static LevelMapProperties read(class_2540 input) {
        LevelMapProperties result = new LevelMapProperties();
        result.id = input.readInt();
        return result;
    }

    public void write(class_2540 u) {
        u.method_53002(this.id);
    }
}

