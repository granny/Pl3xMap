package net.pl3x.map.core.world;

import org.checkerframework.checker.nullness.qual.NonNull;

public class EmptyChunk extends Chunk {
    protected EmptyChunk(@NonNull World world, @NonNull Region region) {
        super(world, region);
    }

    @Override
    public @NonNull BlockState getBlockState(int x, int y, int z) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public int getLight(int x, int y, int z) {
        return getWorld().getSkylight();
    }

    @Override
    public @NonNull Biome getBiome(int x, int y, int z) {
        return Biome.DEFAULT;
    }

    @Override
    public int getWorldSurfaceY(int x, int z) {
        return 0;
    }

    @Override
    public @NonNull Chunk populate() {
        return this;
    }
}
