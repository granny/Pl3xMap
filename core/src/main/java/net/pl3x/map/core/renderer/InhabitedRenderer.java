package net.pl3x.map.core.renderer;

import net.pl3x.map.core.registry.RendererRegistry;
import net.pl3x.map.core.renderer.task.RegionScanTask;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class InhabitedRenderer extends Renderer {
    private Renderer basic;

    public InhabitedRenderer(@NonNull RegionScanTask task, @NonNull Builder builder) {
        super(task, builder);
    }

    public void scanData(@NonNull Region region) {
        // get the basic renderer so we can copy its tiles
        this.basic = getRegionScanTask().getRenderer(RendererRegistry.BASIC);
        super.scanData(region);
    }

    @Override
    public void scanBlock(@NonNull Region region, @NonNull Chunk chunk, Chunk.@NonNull BlockData data, int blockX, int blockZ) {
        // get basic pixel color
        int pixelColor;
        if (this.basic != null) {
            // get current color from basic renderer
            pixelColor = this.basic.getTileImage().getPixel(blockX, blockZ);
        } else {
            // could not find basic renderer (disabled?), we have to draw it ourselves
            pixelColor = basicPixelColor(region, data.getBlockState(), data.getFluidState(), data.getBiome(region, blockX, blockZ), blockX, data.getBlockY(), blockZ, data.getFluidY());
        }

        // we hsb lerp between blue and red with ratio being the
        // percent inhabited time is of the maxed out inhabited time
        float ratio = Mathf.clamp(0F, 1F, chunk.getInhabitedTime() / 3600000F);
        int inhabitedRGB = Colors.lerpHSB(0x880000FF, 0x88FF0000, ratio, false);

        // set the color, mixing our heatmap on top
        // set a low enough alpha, so we can see the basic map underneath
        pixelColor = Colors.blend(inhabitedRGB, pixelColor);

        getTileImage().setPixel(blockX, blockZ, pixelColor);
    }
}
