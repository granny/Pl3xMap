package net.pl3x.map.util;

import ca.spottedleaf.starlight.common.light.StarLightInterface;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

public class LightEngine {
    private static StarLightInterface starLightInterface;
    private static Method getBlockLightValue;

    public static void init() {
        try {
            Field field = ThreadedLevelLightEngine.class.getDeclaredField("theLightEngine");
            field.setAccessible(true);
            starLightInterface = (StarLightInterface) field.get(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().getLightEngine());
            getBlockLightValue = StarLightInterface.class.getDeclaredMethod("getBlockLightValue", BlockPos.class, ChunkAccess.class);
            getBlockLightValue.setAccessible(true);
        } catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getBlockLightValue(BlockPos pos, ChunkAccess chunk) {
        try {
            return (int) getBlockLightValue.invoke(starLightInterface, pos, chunk);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return 0;
        }
    }
}
