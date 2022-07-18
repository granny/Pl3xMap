package net.pl3x.map.render.task;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.render.heightmap.Heightmap;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.LightEngine;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.world.ChunkHelper;
import net.pl3x.map.world.MapWorld;

public abstract class Renderer {
    private final String name;
    private final Render render;
    private final RegionCoordinate region;

    private final MapWorld mapWorld;
    private final ChunkHelper chunkHelper;

    private Image.Holder imageHolder;

    public Renderer(String name, Render render, RegionCoordinate region) {
        this.name = name;
        this.render = render;
        this.region = region;

        this.mapWorld = render.getWorld();
        this.chunkHelper = new ChunkHelper(render);
    }

    public String getName() {
        return this.name;
    }

    public Render getRender() {
        return this.render;
    }

    public RegionCoordinate getRegion() {
        return this.region;
    }

    public MapWorld getWorld() {
        return this.mapWorld;
    }

    public ChunkHelper getChunkHelper() {
        return this.chunkHelper;
    }

    public Image.Holder getImageHolder() {
        return this.imageHolder;
    }

    public void allocateImages() {
        this.imageHolder = new Image.Holder(getName(), getWorld(), getRegion());
    }

    public void saveImages() {
        this.imageHolder.save();
    }

    public abstract void doIt(MapWorld mapWorld, ChunkAccess chunk, BlockState blockState, BlockPos blockPos, Biome blockBiome, BlockState fluidState, BlockPos fluidPos, Biome fluidBiome, int x, int z, List<Integer> glass, Heightmap heightmap, int color);

    public int basicPixelColor(BlockState blockState, BlockPos blockPos, Biome blockBiome, BlockState fluidState, BlockPos fluidPos, Biome fluidBiome, int x, int z, List<Integer> glass, Heightmap heightmap, int color) {
        // fluid stuff
        boolean isFluid = fluidPos != null;
        boolean transFluid = getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS;
        boolean flatFluid = isFluid && !transFluid;

        // fix true block color
        int blockColor;
        if (flatFluid) {
            blockColor = Colors.fixBlockColor(getRender().getBiomeColors(), getChunkHelper(), fluidBiome, fluidState, fluidPos, color);
        } else {
            blockColor = Colors.fixBlockColor(getRender().getBiomeColors(), getChunkHelper(), blockBiome, blockState, blockPos, color);
        }
        int pixelColor = blockColor == 0 ? blockColor : Colors.setAlpha(0xFF, blockColor);

        // work out the heightmap
        pixelColor = Colors.mix(pixelColor, heightmap.getColor(blockPos, x, z, flatFluid));

        // fancy fluids, yum
        if (isFluid && transFluid) {
            int fluidColor = fancyFluids(fluidState, fluidPos, fluidBiome, (fluidPos.getY() - blockPos.getY()) * 0.025F);
            pixelColor = Colors.mix(pixelColor, fluidColor);
        }

        // if there was translucent glass, mix it in here
        if (!glass.isEmpty()) {
            pixelColor = Colors.mix(pixelColor, Colors.merge(glass), Math.min(1.0F, 0.70F + (0.05F * glass.size())));
        }

        return pixelColor;
    }

    public int fancyFluids(BlockState fluidState, BlockPos fluidPos, Biome fluidBiome, float depth) {
        // let's do some maths to get pretty fluid colors based on depth
        int fluidColor;
        if (fluidState.is(Blocks.LAVA)) {
            fluidColor = Colors.getRawBlockColor(fluidState);
            fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, 0.3F, Easing.cubicOut(depth / 1.5F)));
            fluidColor = Colors.setAlpha(0xFF, fluidColor);
        } else {
            fluidColor = getRender().getBiomeColors().getWaterColor(getChunkHelper(), fluidBiome, fluidPos);
            fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, 0.45F, Easing.cubicOut(depth / 1.5F)));
            fluidColor = Colors.setAlpha((int) (Easing.quinticOut(Mathf.clamp(0, 1, depth * 5F)) * 0xFF), fluidColor);
        }
        return fluidColor;
    }

    public int calculateLight(ChunkAccess chunk, BlockPos blockPos, BlockState fluidState, BlockPos fluidPos, int pixelColor) {
        if (getWorld().getConfig().RENDER_SKYLIGHT > -1 && getWorld().getConfig().RENDER_SKYLIGHT < 15) {
            // get light level right above this block
            int blockLight;
            if (fluidState != null && fluidState.is(Blocks.LAVA)) {
                // not sure why lava isn't returning
                // the correct light levels in the nether..
                // maybe a starlight optimization?
                blockLight = 15;
            } else {
                blockLight = LightEngine.getBlockLightValue(chunk, (fluidPos == null ? blockPos : fluidPos).above());
            }
            // blocklight in 0-255 range
            int alpha = (int) (Mathf.inverseLerp(0, 15, blockLight) * 0xFF);
            // skylight level in 0-15 range
            int skylight = (int) Mathf.clamp(0, 15, getWorld().getConfig().RENDER_SKYLIGHT);
            // inverse of the skylight level in 0-1 range
            float inverseSkylight = Mathf.inverseLerp(0, 15, 15 - skylight);
            // how much darkness to draw in 0-255 range
            int darkness = (int) Mathf.clamp(0, 0xFF, (0xFF * inverseSkylight) - alpha);
            // mix it into the pixel
            return Colors.mix(pixelColor, Colors.setAlpha(darkness, 0x00));
        }
        return pixelColor;
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
