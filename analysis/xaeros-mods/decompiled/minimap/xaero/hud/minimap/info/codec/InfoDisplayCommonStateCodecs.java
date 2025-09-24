/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info.codec;

import xaero.hud.minimap.info.codec.InfoDisplayStateCodec;

public class InfoDisplayCommonStateCodecs {
    public static final InfoDisplayStateCodec<Boolean> BOOLEAN = new InfoDisplayStateCodec<Boolean>(s -> s.equals("true"), Object::toString);
    public static final InfoDisplayStateCodec<Integer> INTEGER = new InfoDisplayStateCodec<Integer>(s -> Integer.parseInt(s), Object::toString);
}

