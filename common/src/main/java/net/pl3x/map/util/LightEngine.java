package net.pl3x.map.util;

import ca.spottedleaf.starlight.common.light.SWMRNibbleArray;
import ca.spottedleaf.starlight.common.light.StarLightInterface;
import java.lang.reflect.Field;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.coordinate.Coordinate;

public class LightEngine {
    private static final StarLightInterface starLightInterface;
    private static final Field minLightSection;

    static {
        try {
            //noinspection resource
            ServerLevel level = MinecraftServer.getServer().overworld();

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
        final SWMRNibbleArray[] nibbles = chunk.getBlockNibbles();
        final int index = Mathf.clamp(0, nibbles.length - 1, Coordinate.blockToChunk(pos.getY()) - getMinLightSection());
        final SWMRNibbleArray nibble = nibbles[index];
        return nibble == null ? 15 : nibble.getVisible(pos.getX(), pos.getY(), pos.getZ());
    }

    private static int getMinLightSection() {
        try {
            return minLightSection.getInt(starLightInterface);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
