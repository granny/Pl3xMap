package net.pl3x.map.render;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.Key;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.heightmap.Heightmap;
import net.pl3x.map.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.LightEngine;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.world.World;

public abstract class Renderer {
    private final Key key;
    private final String name;
    private final ScanTask scanTask;
    private final Heightmap heightmap;

    private Image.Holder imageHolder;

    public Renderer(RendererHolder holder, ScanTask scanTask) {
        this.key = holder.getKey();
        this.name = holder.getName();
        this.scanTask = scanTask;

        Key key = Key.of(getWorld().getConfig().RENDER_HEIGHTMAP_TYPE.toLowerCase(Locale.ROOT));
        this.heightmap = Pl3xMap.api().getHeightmapRegistry().get(key);
    }

    public Key getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public ScanTask getScanTask() {
        return scanTask;
    }

    public Render getRender() {
        return this.scanTask.getRender();
    }

    public RegionCoordinate getRegion() {
        return this.scanTask.getRegion();
    }

    public World getWorld() {
        return this.scanTask.getWorld();
    }

    public Heightmap getHeightmap() {
        return this.heightmap;
    }

    public Image.Holder getImageHolder() {
        return this.imageHolder;
    }

    public void allocateData() {
        this.imageHolder = new Image.Holder(getKey(), getWorld(), getRegion());
    }

    public void saveData() {
        this.imageHolder.save();
    }

    public abstract void scanData(RegionCoordinate region, ScanData.Data scanData);

    public int basicPixelColor(ScanData data, ScanData.Data scanData) {
        // fluid stuff
        boolean isFluid = data.getFluidPos() != null;
        boolean transFluid = getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS;
        boolean flatFluid = isFluid && !transFluid;

        // fix true block color
        int pixelColor = 0;
        if (!flatFluid) {
            pixelColor = Colors.fixBlockColor(getRender().getBiomeColors(), data, scanData, Colors.getRawBlockColor(data.getBlockState()));
            if (pixelColor != 0) {
                // fix alpha
                pixelColor = Colors.setAlpha(0xFF, pixelColor);
                // work out the heightmap
                pixelColor = Colors.mix(pixelColor, getHeightmap().getColor(data.getCoordinate(), data, scanData));
            }
        }

        // fancy fluids, yum
        if (isFluid) {
            if (transFluid) {
                int fluidColor = fancyFluids(data, scanData, data.getFluidState(), (data.getFluidPos().getY() - data.getBlockPos().getY()) * 0.025F);
                pixelColor = Colors.mix(pixelColor, fluidColor);
            } else {
                pixelColor = getRender().getBiomeColors().getWaterColor(data, scanData);
            }
        }

        // if there was translucent glass, mix it in here
        if (!data.getGlassColors().isEmpty()) {
            pixelColor = Colors.mix(pixelColor, Colors.merge(data.getGlassColors()), Math.min(1.0F, 0.70F + (0.05F * data.getGlassColors().size())));
        }

        return pixelColor;
    }

    public int fancyFluids(ScanData data, ScanData.Data scanData, BlockState fluidState, float depth) {
        // let's do some maths to get pretty fluid colors based on depth
        int fluidColor;
        if (fluidState.is(Blocks.LAVA)) {
            fluidColor = Colors.getRawBlockColor(fluidState);
            fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, 0.3F, Easing.cubicOut(depth / 1.5F)));
            fluidColor = Colors.setAlpha(0xFF, fluidColor);
        } else {
            fluidColor = getRender().getBiomeColors().getWaterColor(data, scanData);
            fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, 0.45F, Easing.cubicOut(depth / 1.5F)));
            fluidColor = Colors.setAlpha((int) (Easing.quinticOut(Mathf.clamp(0, 1, depth * 5F)) * 0xFF), fluidColor);
        }
        return fluidColor;
    }

    public int calculateLight(ChunkAccess chunk, BlockPos blockPos, BlockState fluidState, BlockPos fluidPos, int pixelColor) {
        if (getWorld().getConfig().RENDER_SKYLIGHT < 15) {
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
            int skylight = getWorld().getConfig().RENDER_SKYLIGHT;
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
