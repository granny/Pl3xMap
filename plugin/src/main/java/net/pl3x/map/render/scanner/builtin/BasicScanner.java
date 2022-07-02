package net.pl3x.map.render.scanner.builtin;

import java.util.Arrays;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.renderer.Renderer;
import net.pl3x.map.render.renderer.iterator.coordinate.Coordinate;
import net.pl3x.map.render.renderer.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.scanner.Scanner;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.Mathf;
import org.apache.commons.lang3.BooleanUtils;

public class BasicScanner extends Scanner {
    public BasicScanner(Renderer render, RegionCoordinate region, Collection<Long> chunks) {
        super(render, region, chunks);
    }

    @Override
    public void scanChunk(int chunkX, int chunkZ) {
        ChunkAccess chunk = getChunkHelper().getChunk(getWorld().getLevel(), chunkX, chunkZ);
        if (chunk == null) {
            return;
        }

        int blockX = Coordinate.chunkToBlock(chunkX);
        int blockZ = Coordinate.chunkToBlock(chunkZ);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int[] lastY = new int[16];

        // iterate each block in this chunk
        for (int z = 0; z < 16; z++) {

            // we need the bottom row of the chunk to the north to get heightmap correct
            if (z == 0) {
                scanNorthChunk(chunkX, chunkZ, blockX, blockZ, pos, lastY);
            }

            for (int x = 0; x < 16; x++) {

                // find our starting point
                pos.set(blockX + x, 0, blockZ + z);
                pos.setY(chunk.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) + 1);

                // setup data
                int pixelX = pos.getX() & Image.SIZE - 1;
                int pixelZ = pos.getZ() & Image.SIZE - 1;
                BlockState state;
                int blockColor = 0;
                float depth = 0F;
                boolean isFluid;
                Boolean lava = null;

                // let's find the right block to work with
                do {
                    pos.move(Direction.DOWN);
                    state = chunk.getBlockState(pos);
                    isFluid = !state.getFluidState().isEmpty();
                    if (isFluid && getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS) {
                        if (lava == null) {
                            lava = chunk.getBlockState(pos).is(Blocks.LAVA);
                        }
                        depth += 0.025F;
                    } else {
                        blockColor = Colors.getBlockColor(state);
                    }
                } while (pos.getY() > getWorld().getLevel().getMinBuildHeight() && (blockColor <= 0 || (isFluid && getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS)));

                Biome biome;
                if (getWorld().getConfig().RENDER_BIOME_BLEND > 0) {
                    biome = getChunkHelper().getBiomeWithCaching(getWorld(), pos).value();
                } else {
                    biome = getChunkHelper().getBiome(getWorld(), pos).value();
                }

                // update color for correct biome
                blockColor = getWorld().getBiomeColors().fixBiomeColor(getChunkHelper(), biome, state, pos, blockColor);
                int pixelColor = blockColor == 0 ? blockColor : (0xFF << 24) | blockColor;

                int heightColor = 0x22;
                if (lastY[x] != Integer.MAX_VALUE) {
                    if (pos.getY() > lastY[x]) {
                        heightColor = 0x00;
                    } else if (pos.getY() < lastY[x]) {
                        heightColor = 0x44;
                    }
                }
                pixelColor = Colors.mix(pixelColor, heightColor << 24);
                lastY[x] = pos.getY();

                if (getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS) {
                    // let's do some maths to get pretty fluid colors based on depth
                    int fluidColor;
                    if (BooleanUtils.isTrue(lava)) {
                        fluidColor = Colors.getBlockColor(Blocks.LAVA.defaultBlockState());
                        fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, 0.3F, Easing.cubicOut(depth / 1.5F)));
                        fluidColor = Colors.setAlpha(0xFF, fluidColor);
                    } else {
                        BlockPos pos1 = new BlockPos(pos.getX(), pos.getY() + (depth / 0.025F), pos.getZ());
                        fluidColor = getWorld().getBiomeColors().getWaterColor(getChunkHelper(), biome, pos1, getWorld().getConfig().RENDER_BIOME_BLEND);
                        fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, 0.45F, Easing.cubicOut(depth / 1.5F)));
                        fluidColor = Colors.setAlpha((int) (Easing.quinticOut(Mathf.clamp(0, 1, depth * 5F)) * 0xFF), fluidColor);
                    }
                    pixelColor = Colors.mix(pixelColor, fluidColor);
                }

                getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);

            }
        }
    }

    private void scanNorthChunk(int chunkX, int chunkZ, int blockX, int blockZ, BlockPos.MutableBlockPos pos, int[] lastY) {
        ChunkAccess northChunk = getChunkHelper().getChunk(getWorld().getLevel(), chunkX, chunkZ - 1);
        if (northChunk == null) {
            Arrays.fill(lastY, Integer.MAX_VALUE);
        } else {
            BlockState state;
            int blockColor;
            boolean isFluid;
            for (int x = 0; x < 16; x++) {
                pos.set(blockX + x, 0, blockZ + 15);
                pos.setY(northChunk.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) + 1);
                do {
                    pos.move(Direction.DOWN);
                    state = northChunk.getBlockState(pos);
                    blockColor = Colors.getBlockColor(state);
                    isFluid = getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS && !state.getFluidState().isEmpty();
                } while (pos.getY() > getWorld().getLevel().getMinBuildHeight() && (blockColor <= 0 || isFluid));
                lastY[x] = pos.getY();
            }
        }
    }

    public static class Easing {
        public static float cubicOut(float t) {
            return 1F + ((t -= 1F) * t * t);
        }

        public static float quinticOut(float t) {
            return 1F + ((t -= 1F) * t * t * t * t);
        }
    }
}
