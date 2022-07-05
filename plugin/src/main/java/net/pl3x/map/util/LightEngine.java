package net.pl3x.map.util;

import ca.spottedleaf.starlight.common.light.StarLightInterface;
import java.lang.reflect.Field;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.render.job.iterator.coordinate.Coordinate;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

public class LightEngine {
    private static StarLightInterface starLightInterface;
    private static Field minLightSection;

    public static void init() {
        try {
            ServerLevel level = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

            Field theLightEngine = ThreadedLevelLightEngine.class.getDeclaredField("theLightEngine");
            theLightEngine.setAccessible(true);
            starLightInterface = (StarLightInterface) theLightEngine.get(level.getLightEngine());

            minLightSection = StarLightInterface.class.getDeclaredField("minLightSection");
            minLightSection.setAccessible(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getBlockLightValue(ChunkAccess chunk, BlockPos pos) {
        return chunk.getBlockNibbles()[Coordinate.blockToChunk(pos.getY()) - getMinLightSection()].getVisible(pos.getX(), pos.getY(), pos.getZ());
    }

    private static int getMinLightSection() {
        try {
            return minLightSection.getInt(starLightInterface);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
