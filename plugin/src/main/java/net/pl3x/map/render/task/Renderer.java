package net.pl3x.map.render.task;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.render.Heightmap;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.util.Colors;
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

    public void setImageHolder(Image.Holder imageHolder) {
        this.imageHolder = imageHolder;
    }

    public abstract void doIt(MapWorld mapWorld, ChunkAccess chunk, BlockState blockState, BlockPos blockPos, Biome blockBiome, BlockState fluidState, BlockPos fluidPos, Biome fluidBiome, int x, int z, List<Integer> glass, Heightmap heightmap, int color);

    public int scanHeightMap(BlockPos pos, Heightmap heightmap, int x, int z) {
        int heightColor = 0x22;
        if (heightmap.x[x] != Integer.MAX_VALUE) {
            if (pos.getY() > heightmap.x[x]) {
                heightColor = 0x00;
            } else if (pos.getY() < heightmap.x[x]) {
                heightColor = 0x44;
            }
        }
        if (heightmap.z[z] != Integer.MAX_VALUE) {
            if (pos.getY() > heightmap.z[z]) {
                heightColor = 0x00;
            } else if (pos.getY() < heightmap.z[z]) {
                heightColor = 0x44;
            }
        }
        heightmap.x[x] = pos.getY();
        heightmap.z[z] = pos.getY();
        return Colors.setAlpha(heightColor, 0x000000);
    }

    public int fancyFluids(BlockState fluidState, BlockPos fluidPos, Biome fluidBiome, float depth) {
        // let's do some maths to get pretty fluid colors based on depth
        int fluidColor;
        if (fluidState.is(Blocks.LAVA)) {
            fluidColor = Colors.getRawBlockColor(fluidState);
            fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, 0.3F, Easing.cubicOut(depth / 1.5F)));
            fluidColor = Colors.setAlpha(0xFF, fluidColor);
        } else {
            fluidColor = getWorld().getBiomeColors().getWaterColor(getChunkHelper(), fluidBiome, fluidPos);
            fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, 0.45F, Easing.cubicOut(depth / 1.5F)));
            fluidColor = Colors.setAlpha((int) (Easing.quinticOut(Mathf.clamp(0, 1, depth * 5F)) * 0xFF), fluidColor);
        }
        return fluidColor;
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
