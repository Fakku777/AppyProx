/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.MappingResolver
 */
package xaero.map.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import xaero.map.misc.IObfuscatedReflection;

public class ObfuscatedReflectionFabric
implements IObfuscatedReflection {
    private static String fixFabricFieldMapping(Class<?> clazz, String name, String descriptor) {
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String owner = mappingResolver.unmapClassName("intermediary", clazz.getName());
        return mappingResolver.mapFieldName("intermediary", owner, name, descriptor);
    }

    private static String fixFabricMethodMapping(Class<?> clazz, String name, String descriptor) {
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String owner = mappingResolver.unmapClassName("intermediary", clazz.getName());
        return mappingResolver.mapMethodName("intermediary", owner, name, descriptor);
    }

    @Override
    public Class<?> getClassForName(String obfuscatedName, String deobfName) throws ClassNotFoundException {
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String name = mappingResolver.mapClassName("intermediary", obfuscatedName);
        try {
            return Class.forName(name);
        }
        catch (ClassNotFoundException cnfe) {
            return Class.forName(deobfName);
        }
    }

    @Override
    public Field getFieldReflection(Class<?> c, String deobfName, String obfuscatedNameFabric, String descriptor, String obfuscatedNameForge) {
        String name = ObfuscatedReflectionFabric.fixFabricFieldMapping(c, obfuscatedNameFabric, descriptor);
        Field field = null;
        try {
            field = c.getDeclaredField(name);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return field;
    }

    @Override
    public Method getMethodReflection(Class<?> c, String deobfName, String obfuscatedNameFabric, String descriptor, String obfuscatedNameForge, Class<?> ... parameters) {
        String name = ObfuscatedReflectionFabric.fixFabricMethodMapping(c, obfuscatedNameFabric, descriptor);
        Method method = null;
        try {
            method = c.getDeclaredMethod(name, parameters);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return method;
    }
}

