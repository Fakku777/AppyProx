/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_2248
 *  net.minecraft.class_2378
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_5321
 *  net.minecraft.class_7924
 */
package xaero.map.mods;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import xaero.map.WorldMap;
import xaero.map.misc.Misc;

public class SupportFramedBlocks {
    private Class<?> framedTileBlockClass;
    private Method framedTileEntityCamoStateMethod;
    private Method framedTileEntityCamoMethod;
    private Method camoContainerStateMethod;
    private Method camoContainerContentMethod;
    private Method camoContentStateMethod;
    private boolean usable;
    private Set<class_2248> framedBlocks;

    public SupportFramedBlocks() {
        try {
            this.framedTileBlockClass = Class.forName("xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity");
        }
        catch (ClassNotFoundException cnfe) {
            try {
                this.framedTileBlockClass = Class.forName("xfacthd.framedblocks.common.tileentity.FramedTileEntity");
            }
            catch (ClassNotFoundException cnfe2) {
                try {
                    this.framedTileBlockClass = Class.forName("xfacthd.framedblocks.api.block.FramedBlockEntity");
                }
                catch (ClassNotFoundException cnfe3) {
                    WorldMap.LOGGER.info("Failed to init Framed Blocks support!", (Throwable)cnfe3);
                    return;
                }
            }
        }
        try {
            this.framedTileEntityCamoStateMethod = this.framedTileBlockClass.getDeclaredMethod("getCamoState", new Class[0]);
        }
        catch (NoSuchMethodException | SecurityException e1) {
            try {
                Class<?> camoContainerClass;
                try {
                    camoContainerClass = Class.forName("xfacthd.framedblocks.api.data.CamoContainer");
                }
                catch (ClassNotFoundException cnfe) {
                    camoContainerClass = Class.forName("xfacthd.framedblocks.api.camo.CamoContainer");
                }
                this.framedTileEntityCamoMethod = this.framedTileBlockClass.getDeclaredMethod("getCamo", new Class[0]);
                try {
                    this.camoContainerStateMethod = camoContainerClass.getDeclaredMethod("getState", new Class[0]);
                }
                catch (NoSuchMethodException | SecurityException e2) {
                    this.camoContainerContentMethod = camoContainerClass.getDeclaredMethod("getContent", new Class[0]);
                    Class<?> camoContentClass = Class.forName("xfacthd.framedblocks.api.camo.CamoContent");
                    this.camoContentStateMethod = camoContentClass.getDeclaredMethod("getAppearanceState", new Class[0]);
                }
            }
            catch (ClassNotFoundException | NoSuchMethodException | SecurityException e2) {
                WorldMap.LOGGER.info("Failed to init Framed Blocks support!", (Throwable)e1);
                WorldMap.LOGGER.info("Failed to init Framed Blocks support!", (Throwable)e2);
            }
        }
        this.usable = this.framedTileBlockClass != null && (this.framedTileEntityCamoStateMethod != null || this.framedTileEntityCamoMethod != null && (this.camoContainerStateMethod != null || this.camoContainerContentMethod != null && this.camoContentStateMethod != null));
    }

    public void onWorldChange() {
        this.framedBlocks = null;
    }

    private void findFramedBlocks(class_1937 world, class_2378<class_2248> registry) {
        if (this.framedBlocks == null) {
            this.framedBlocks = new HashSet<class_2248>();
            if (registry == null) {
                registry = world.method_30349().method_30530(class_7924.field_41254);
            }
            registry.method_29722().forEach(entry -> {
                class_5321 key = (class_5321)entry.getKey();
                if (key.method_29177().method_12836().equals("framedblocks") && key.method_29177().method_12832().startsWith("framed_")) {
                    this.framedBlocks.add((class_2248)entry.getValue());
                }
            });
        }
    }

    public boolean isFrameBlock(class_1937 world, class_2378<class_2248> registry, class_2680 state) {
        if (!this.usable) {
            return false;
        }
        this.findFramedBlocks(world, registry);
        return this.framedBlocks.contains(state.method_26204());
    }

    public class_2680 unpackFramedBlock(class_1937 world, class_2378<class_2248> registry, class_2680 original, class_2586 tileEntity) {
        if (!this.usable) {
            return original;
        }
        if (this.framedTileBlockClass.isAssignableFrom(tileEntity.getClass())) {
            if (this.framedTileEntityCamoStateMethod != null) {
                return (class_2680)Misc.getReflectMethodValue(tileEntity, this.framedTileEntityCamoStateMethod, new Object[0]);
            }
            Object camoContainer = Misc.getReflectMethodValue(tileEntity, this.framedTileEntityCamoMethod, new Object[0]);
            if (this.camoContainerStateMethod != null) {
                return (class_2680)Misc.getReflectMethodValue(camoContainer, this.camoContainerStateMethod, new Object[0]);
            }
            Object camoContent = Misc.getReflectMethodValue(camoContainer, this.camoContainerContentMethod, new Object[0]);
            if (camoContent == null) {
                return original;
            }
            class_2680 state = (class_2680)Misc.getReflectMethodValue(camoContent, this.camoContentStateMethod, new Object[0]);
            if (state == null) {
                return original;
            }
            return state;
        }
        return original;
    }
}

