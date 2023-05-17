/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pl3x.map.core.world;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.core.registry.BiomeRegistry;
import net.pl3x.map.core.util.MCAMath;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChunkAnvil118 extends Chunk {
    private int sectionMin = Integer.MAX_VALUE;

    private Section[] sections = new Section[0];

    protected ChunkAnvil118(@NotNull World world, @NotNull Region region, @NotNull CompoundTag chunkTag, int index) {
        super(world, region, chunkTag, index, 37);

        this.full = chunkTag.getString("Status").equals("full");
        if (!this.full) {
            return;
        }

        if (chunkTag.containsKey("Heightmaps")) {
            CompoundTag heightmaps = chunkTag.getCompoundTag("Heightmaps");
            this.worldSurfaceHeights = heightmaps.getLongArray("WORLD_SURFACE");
        }

        if (chunkTag.containsKey("sections")) {
            ListTag<CompoundTag> sections = chunkTag.getListTag("sections").asCompoundTagList();
            List<Section> list = new ArrayList<>(sections.size());
            int sectionMax = Integer.MIN_VALUE;
            for (CompoundTag sectionTag : sections) {
                Section section = new Section(world, sectionTag);
                int y = section.sectionY;
                if (this.sectionMin > y) this.sectionMin = y;
                if (sectionMax < y) sectionMax = y;
                list.add(section);
            }
            this.sections = new Section[1 + sectionMax - this.sectionMin];
            for (Section section : list) {
                this.sections[section.sectionY - this.sectionMin] = section;
            }
        }
    }

    @Override
    public @NotNull BlockState getBlockState(int x, int y, int z) {
        int sectionY = y >> 4;
        Section section = getSection(sectionY);
        return section == null ? Blocks.AIR.getDefaultState() : section.getBlockState(x, y, z);
    }

    @Override
    public int getLight(int x, int y, int z) {
        int sectionY = y >> 4;
        Section section = getSection(sectionY);
        return section == null ? ((sectionY < this.sectionMin) ? 0 : getWorld().getSkylight()) : section.getLight(x, y, z);
    }

    @Override
    public @NotNull Biome getBiome(int x, int y, int z) {
        int sectionY = y >> 4;
        Section section = getSection(sectionY);
        return section == null ? Biome.DEFAULT : section.getBiome(x, y, z);
    }

    private @Nullable Section getSection(int y) {
        y -= this.sectionMin;
        return y < 0 || y >= this.sections.length ? null : this.sections[y];
    }

    protected static class Section extends Chunk.Section {
        private final BiomeRegistry biomeRegistry;
        private long[] biomes = new long[0];
        private Biome[] biomePalette = new Biome[0];
        private int bitsPerBiome;

        public Section(@NotNull World world, @NotNull CompoundTag nbt) {
            super(nbt);
            this.biomeRegistry = world.getBiomeRegistry();
        }

        @Override
        protected void init(@NotNull CompoundTag nbt) {
            //this.blocks = blocks(nbt.getLongArray("BlockStates")); // blocks array is set inside blockPalette() method
            this.blockLight = light(nbt.getByteArray("BlockLight"));
            this.blockPalette = blockPalette(nbt);
            this.bitsPerBlock = this.blocks.length >> 6;

            CompoundTag biomesTag = nbt.getCompoundTag("biomes");
            if (biomesTag != null) {
                this.biomes = biomesTag.getLongArray("data");
                ListTag<StringTag> paletteTag = biomesTag.getListTag("palette").asStringTagList();
                if (paletteTag != null) {
                    this.biomePalette = new Biome[paletteTag.size()];
                    for (int i = 0; i < this.biomePalette.length; i++) {
                        biomePalette[i] = this.biomeRegistry.getOrDefault(paletteTag.get(i).getValue(), Biome.DEFAULT);
                    }
                }
            }
            this.bitsPerBiome = Integer.SIZE - Integer.numberOfLeadingZeros(this.biomePalette.length - 1);
        }

        @Override
        protected String paletteKey() {
            return "palette";
        }

        @Override
        protected @NotNull BlockState[] blockPalette(@NotNull CompoundTag nbt) {
            BlockState[] palette = new BlockState[0];
            CompoundTag tag = nbt.getCompoundTag("block_states");
            if (tag != null) {
                this.blocks = blocks(tag.getLongArray("data"));
                palette = super.blockPalette(tag);
            }
            return palette;
        }

        public @NotNull Biome getBiome(int x, int y, int z) {
            if (this.biomePalette.length == 0) {
                return Biome.DEFAULT;
            }
            if (this.biomePalette.length == 1 || this.biomes.length == 0) {
                return this.biomePalette[0];
            }
            int biomeIndex = (((y & 0xF) >> 2) << 4) + (((z & 0xF) >> 2) << 2) + ((x & 0xF) >> 2);
            long value = MCAMath.getValueFromLongArray(this.biomes, biomeIndex, this.bitsPerBiome);
            if (value >= this.biomePalette.length) {
                return Biome.DEFAULT;
            }
            return this.biomePalette[(int) value];
        }
    }
}
