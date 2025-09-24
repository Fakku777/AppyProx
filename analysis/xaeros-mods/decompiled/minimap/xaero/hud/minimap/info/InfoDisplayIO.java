/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info;

import java.io.PrintWriter;
import java.util.ArrayList;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.InfoDisplayManager;

public class InfoDisplayIO {
    private final InfoDisplayManager manager;

    public InfoDisplayIO(InfoDisplayManager manager) {
        this.manager = manager;
    }

    public void save(PrintWriter writer) {
        writer.print("infoDisplayOrder");
        this.manager.getOrderedStream().forEach(infoDisplay -> {
            writer.print(":");
            writer.print(infoDisplay.getId());
        });
        writer.println();
        this.manager.getOrderedStream().forEach(infoDisplay -> writer.println(this.getInfoDisplayLine((InfoDisplay)infoDisplay)));
    }

    private <T> String getInfoDisplayLine(InfoDisplay<T> infoDisplay) {
        return "infoDisplay:" + infoDisplay.getId() + ":" + infoDisplay.getCodec().encode(infoDisplay.getState()) + ":" + infoDisplay.getTextColor() + ":" + infoDisplay.getBackgroundColor();
    }

    public void loadInfoDisplayOrderLine(String[] args) {
        if (args.length > 1) {
            ArrayList<String> savedOrder = new ArrayList<String>();
            for (int i = 1; i < args.length; ++i) {
                savedOrder.add(args[i]);
            }
            this.manager.setOrder(savedOrder);
        }
    }

    public void loadInfoDisplayLine(String[] args) {
        String id = args[1];
        InfoDisplay<?> infoDisplay = this.manager.get(id);
        if (infoDisplay != null) {
            this.loadInfoDisplay(infoDisplay, args);
        }
    }

    private <T> void loadInfoDisplay(InfoDisplay<T> infoDisplay, String[] args) {
        T state;
        try {
            state = infoDisplay.getCodec().decode(args[2]);
        }
        catch (Throwable t) {
            state = infoDisplay.getDefaultState();
        }
        int textColorIndex = Integer.parseInt(args[3]);
        int backgroundColorIndex = Integer.parseInt(args[4]);
        infoDisplay.setState(state);
        infoDisplay.setTextColor(textColorIndex);
        infoDisplay.setBackgroundColor(backgroundColorIndex);
    }
}

