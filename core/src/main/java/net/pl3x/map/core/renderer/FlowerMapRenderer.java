/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.renderer;

import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.renderer.task.RegionScanTask;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.Block;
import net.pl3x.map.core.world.Blocks;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import org.jetbrains.annotations.NotNull;

public class FlowerMapRenderer extends Renderer {
    private final Map<@NotNull Block, @NotNull Integer> colorMap = new HashMap<>();

    public FlowerMapRenderer(@NotNull RegionScanTask task, @NotNull Builder builder) {
        super(task, builder);
        this.colorMap.put(Blocks.DANDELION, 0xFFFF00);
        this.colorMap.put(Blocks.POPPY, 0xFF0000);
        this.colorMap.put(Blocks.ALLIUM, 0x9900FF);
        this.colorMap.put(Blocks.AZURE_BLUET, 0xFFFDDD);
        this.colorMap.put(Blocks.RED_TULIP, 0xFF4D62);
        this.colorMap.put(Blocks.ORANGE_TULIP, 0xFFB55A);
        this.colorMap.put(Blocks.WHITE_TULIP, 0xDDFFFF);
        this.colorMap.put(Blocks.PINK_TULIP, 0xF5B4FF);
        this.colorMap.put(Blocks.OXEYE_DAISY, 0xFFEEDD);
        this.colorMap.put(Blocks.CORNFLOWER, 0x4100FF);
        this.colorMap.put(Blocks.LILY_OF_THE_VALLEY, 0xFFFFFF);
        this.colorMap.put(Blocks.BLUE_ORCHID, 0x00BFFF);
    }

    @Override
    public void scanBlock(@NotNull Region region, @NotNull Chunk chunk, Chunk.@NotNull BlockData data, int blockX, int blockZ) {
        int pixelColor = 0x7F7F7F;

        Biome biome = data.getBiome(region, blockX, blockZ);

        Block flower = Pl3xMap.api().getFlower(region.getWorld(), biome, blockX, data.getBlockY(), blockZ);
        if (flower != null) {
            pixelColor = (0xFF << 24) | (this.colorMap.getOrDefault(flower, pixelColor) & 0xFFFFFF);
        }

        // work out the heightmap
        pixelColor = Colors.blend(getHeightmap().getColor(region, blockX, blockZ), pixelColor);

        // fluid stuff
        if (data.getFluidState() != null) {
            if (getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS) {
                int fluidColor = fancyFluids(region, biome, data.getFluidState(), blockX, blockZ, (data.getFluidY() - data.getBlockY()) * 0.025F);
                pixelColor = Colors.blend(fluidColor, pixelColor);
            } else {
                pixelColor = Colors.getWaterColor(region, biome, blockX, blockZ);
            }
        }

        // draw color data to image
        getTileImage().setPixel(blockX, blockZ, pixelColor);
    }
}
