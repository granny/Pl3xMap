package net.pl3x.map.addon.flowermap.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.image.Image;
import net.pl3x.map.render.Renderer;
import net.pl3x.map.render.RendererHolder;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.render.ScanTask;
import net.pl3x.map.util.Colors;

public class FlowerMapRenderer extends Renderer {
    @SuppressWarnings("deprecation")
    private final RandomSource random = RandomSource.createThreadSafe();
    private final Map<BlockState, Integer> colorMap = new HashMap<>();

    public FlowerMapRenderer(RendererHolder holder, ScanTask scanTask) {
        super(holder, scanTask);
        this.colorMap.put(Blocks.DANDELION.defaultBlockState(), 0xFFFFFF00);
        this.colorMap.put(Blocks.POPPY.defaultBlockState(), 0xFFFF0000);
        this.colorMap.put(Blocks.ALLIUM.defaultBlockState(), 0xFF9900FF);
        this.colorMap.put(Blocks.AZURE_BLUET.defaultBlockState(), 0xFFFFFDDD);
        this.colorMap.put(Blocks.RED_TULIP.defaultBlockState(), 0xFFFF4D62);
        this.colorMap.put(Blocks.ORANGE_TULIP.defaultBlockState(), 0xFFFFB55A);
        this.colorMap.put(Blocks.WHITE_TULIP.defaultBlockState(), 0xFFDDFFFF);
        this.colorMap.put(Blocks.PINK_TULIP.defaultBlockState(), 0xFFF5B4FF);
        this.colorMap.put(Blocks.OXEYE_DAISY.defaultBlockState(), 0xFFFFEEDD);
        this.colorMap.put(Blocks.CORNFLOWER.defaultBlockState(), 0xFF4100FF);
        this.colorMap.put(Blocks.LILY_OF_THE_VALLEY.defaultBlockState(), 0xFFFFFFFF);
        this.colorMap.put(Blocks.BLUE_ORCHID.defaultBlockState(), 0xFF00BFFF);
    }

    @Override
    public void scanData(RegionCoordinate region, ScanData.Data scanData) {
        // scan each block's data
        for (ScanData data : scanData.values()) {
            // get the current tile coordinates
            int pixelX = data.getCoordinate().getBlockX() & Image.SIZE - 1;
            int pixelZ = data.getCoordinate().getBlockZ() & Image.SIZE - 1;

            int pixelColor = 0xFF7F7F7F;

            // https://github.com/Draradech/FlowerMap (CC0-1.0 license)
            List<ConfiguredFeature<?, ?>> flowers = data.getBlockBiome().getGenerationSettings().getFlowerFeatures();
            if (!flowers.isEmpty()) {
                RandomPatchConfiguration config = (RandomPatchConfiguration) flowers.get(0).config();
                SimpleBlockConfiguration flower = (SimpleBlockConfiguration) config.feature().value().feature().value().config();
                BlockState state = flower.toPlace().getState(this.random, data.getBlockPos());
                pixelColor = this.colorMap.getOrDefault(state, 0xFF7F7F7F);
            }

            // work out the heightmap
            pixelColor = Colors.blend(getHeightmap().getColor(data.getCoordinate(), data, scanData), pixelColor);

            // fluid stuff
            if (data.getFluidPos() != null) {
                if (getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS) {
                    pixelColor = Colors.blend(fancyFluids(data, scanData, data.getFluidState(), (data.getFluidPos().getY() - data.getBlockPos().getY()) * 0.025F), pixelColor);
                } else {
                    pixelColor = getRender().getBiomeColors().getWaterColor(data, scanData);
                }
            }

            // draw color data to image
            getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
        }
    }
}
