/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info.codec;

import java.util.function.Function;

public class InfoDisplayStateCodec<T> {
    private final Function<String, T> decoder;
    private final Function<T, String> encoder;

    public InfoDisplayStateCodec(Function<String, T> decoder, Function<T, String> encoder) {
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public T decode(String string) {
        return this.decoder.apply(string);
    }

    public String encode(T value) {
        return this.encoder.apply(value);
    }
}

