/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_5250
 *  net.minecraft.class_5348
 */
package xaero.map.gui.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.class_5348;
import xaero.map.gui.message.Message;
import xaero.map.misc.TextSplitter;

public class MessageBox {
    private final List<Message> messages;
    private final int width;
    private final int capacity;

    private MessageBox(List<Message> messages, int width, int capacity) {
        this.messages = messages;
        this.width = width;
        this.capacity = capacity;
    }

    private void addMessageLine(class_2561 text) {
        Message msg = new Message(text, System.currentTimeMillis());
        this.messages.add(0, msg);
        if (this.messages.size() > this.capacity) {
            this.messages.remove(this.messages.size() - 1);
        }
    }

    public void addMessage(class_2561 text) {
        ArrayList<class_2561> splitDest = new ArrayList<class_2561>();
        TextSplitter.splitTextIntoLines(splitDest, this.width, this.width, (class_5348)text, null);
        for (class_2561 line : splitDest) {
            this.addMessageLine(line);
        }
    }

    public void addMessageWithSource(class_2561 source, class_2561 text) {
        class_5250 fullText = class_2561.method_43470((String)"<");
        fullText.method_10855().add(source);
        fullText.method_10855().add(class_2561.method_43470((String)"> "));
        fullText.method_10855().add(text);
        this.addMessage((class_2561)fullText);
    }

    public int getCapacity() {
        return this.capacity;
    }

    public Iterator<Message> getIterator() {
        return this.messages.iterator();
    }

    public static class Builder {
        private int width;
        private int capacity;

        private Builder() {
        }

        public Builder setDefault() {
            this.setWidth(250);
            this.setCapacity(5);
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public MessageBox build() {
            return new MessageBox(new ArrayList<Message>(this.capacity), this.width, this.capacity);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

