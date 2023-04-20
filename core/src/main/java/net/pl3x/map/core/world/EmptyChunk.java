package net.pl3x.map.core.world;

import org.checkerframework.checker.nullness.qual.NonNull;

public class EmptyChunk extends Chunk {
    protected EmptyChunk(@NonNull World world, @NonNull Region region) {
        super(world, region);
    }

    @Override
    @NonNull
    public BlockState getBlockState(int x, int y, int z) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public int getLight(int x, int y, int z) {
        return getWorld().getSkylight();
    }

    @Override
    @NonNull
    public Biome getBiome(int x, int y, int z) {
        return Biome.DEFAULT;
    }

    @Override
    public int getWorldSurfaceY(int x, int z) {
        return 0;
    }

    @Override
    public int getOceanFloorY(int x, int z) {
        return 0;
    }

    @Override
    @NonNull
    public Chunk populate() {
        return this;
    }
}
