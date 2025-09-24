/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IObfuscatedReflection {
    public Class<?> getClassForName(String var1, String var2) throws ClassNotFoundException;

    public Field getFieldReflection(Class<?> var1, String var2, String var3, String var4, String var5);

    public Method getMethodReflection(Class<?> var1, String var2, String var3, String var4, String var5, Class<?> ... var6);
}

