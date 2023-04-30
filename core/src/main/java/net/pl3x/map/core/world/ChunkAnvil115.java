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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.util.MCAMath;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChunkAnvil115 extends Chunk {
    private Section[] sections = new Section[0];

    private int[] biomes;

    protected long[] worldSurfaceHeights = new long[0];

    private final boolean full;

    protected ChunkAnvil115(@NonNull World world, @NonNull Region region, @NonNull CompoundTag chunkTag) {
        super(world, region, chunkTag);

        CompoundTag levelData = chunkTag.getCompoundTag("Level");

        this.full = levelData.getString("Status").equals("full");
        if (!this.full) {
            return;
        }

        if (levelData.containsKey("Heightmaps")) {
            CompoundTag heightmaps = levelData.getCompoundTag("Heightmaps");
            this.worldSurfaceHeights = heightmaps.getLongArray("WORLD_SURFACE");
        }

        if (levelData.containsKey("Sections")) {
            this.sections = new Section[32]; //32 supports a max world-height of 512 which is the max that the hightmaps of Minecraft V1.13+ can store with 9 bits, i believe?
            for (CompoundTag sectionTag : levelData.getListTag("Sections").asCompoundTagList()) {
                Section section = new Section(sectionTag);
                if (section.getSectionY() >= 0 && section.getSectionY() < this.sections.length) {
                    this.sections[section.getSectionY()] = section;
                }
            }
        }

        Tag<?> tag = levelData.get("Biomes"); //tag can be byte-array or int-array
        if (tag instanceof ByteArrayTag) {
            byte[] bs = ((ByteArrayTag) tag).getValue();
            this.biomes = new int[bs.length];
            for (int i = 0; i < bs.length; i++) {
                this.biomes[i] = bs[i] & 0xFF;
            }
        } else if (tag instanceof IntArrayTag) {
            this.biomes = ((IntArrayTag) tag).getValue();
        }
        if (this.biomes == null || this.biomes.length == 0) {
            this.biomes = new int[1024];
        }
        if (this.biomes.length < 1024) {
            this.biomes = Arrays.copyOf(this.biomes, 1024);
        }
    }

    @Override
    public boolean isFull() {
        return this.full;
    }

    @Override
    public @NonNull BlockState getBlockState(int x, int y, int z) {
        int sectionY = y >> 4;
        if (sectionY < 0 || sectionY >= this.sections.length) {
            return Blocks.AIR.getDefaultState();
        }
        Section section = this.sections[sectionY];
        return section == null ? Blocks.AIR.getDefaultState() : section.getBlockState(x, y, z);
    }

    @Override
    public int getLight(int x, int y, int z) {
        int sectionY = y >> 4;
        if (sectionY < 0 || sectionY >= this.sections.length) {
            return (y < 0) ? 0 : getWorld().getSkylight();
        }
        Section section = this.sections[sectionY];
        return section == null ? getWorld().getSkylight() : section.getLight(x, y, z);
    }

    @Override
    public @NonNull Biome getBiome(int x, int y, int z) {
        int index = ((y >> 2) << 4) + (((z & 0xF) >> 2) << 2) + ((x & 0xF) >> 2);
        if (index < 0) {
            return Biome.DEFAULT;
        }
        if (index >= this.biomes.length) {
            return Biome.DEFAULT;
        }
        Biome biome = LegacyBiomes.get(this.biomes[index]);
        return biome == null ? Biome.DEFAULT : biome;
    }

    @Override
    public boolean noHeightmap() {
        return this.worldSurfaceHeights.length < 36;
    }

    @Override
    public int getWorldSurfaceY(int x, int z) {
        if (noHeightmap()) {
            return 0;
        }
        return (int) MCAMath.getValueFromLongArray(this.worldSurfaceHeights, ((z & 0xF) << 4) + (x & 0xF), 9);
    }

    @Override
    public @NonNull Chunk populate() {
        if (this.populated) {
            return this;
        }

        // scan chunk for relevant data
        // block coordinates for most northwest block in chunk
        int startX = getX() << 4;
        int startZ = getZ() << 4;

        // iterate each block in this chunk
        for (int blockZ = startX; blockZ < startX + 16; blockZ++) {
            for (int blockX = startZ; blockX < startZ + 16; blockX++) {
                BlockData data = new BlockData();
                data.blockY = noHeightmap() ? getWorld().getMaxBuildHeight() : getWorldSurfaceY(blockX, blockZ) + 1;

                // if world has ceiling iterate down until we find air
                if (getWorld().hasCeiling()) {
                    data.blockY = getWorld().getLogicalHeight();
                    do {
                        data.blockY -= 1;
                        data.blockstate = getBlockState(blockX, data.blockY, blockZ);
                    } while (data.blockY > getWorld().getMinBuildHeight() && !data.blockstate.getBlock().isAir());
                }

                // iterate down until we find a renderable block
                do {
                    data.blockY -= 1;
                    data.blockstate = getBlockState(blockX, data.blockY, blockZ);
                    if (data.blockstate.getBlock().isFluid()) {
                        if (data.fluidstate == null) {
                            // get fluid information for the top fluid block
                            data.fluidY = data.blockY;
                            data.fluidstate = data.blockstate;
                            // do not get biome here! causes stackoverflow!
                            // instead, biome will be lazy loaded on first get
                            //data.fluidBiome = getWorld().getBiome(blockX, data.fluidY, blockZ);
                        }
                        continue;
                    }

                    // test if block is renderable. we ignore blocks with black color
                    if (Colors.getRawBlockColor(data.blockstate.getBlock()) > 0) {
                        break;
                    }
                } while (data.blockY > getWorld().getMinBuildHeight());

                // determine the biome of final block
                // do not get biome here! causes stackoverflow!
                // instead, biome will be lazy loaded on first get
                //data.blockBiome = getWorld().getBiome(blockX, data.blockY, blockZ);

                // save data
                this.data[((blockZ & 0xF) << 4) + (blockX & 0xF)] = data;
            }
        }

        this.populated = true;

        for (Section section : this.sections) {
            if (section != null) {
                section.blocks = new long[0];
                section.palette = new BlockState[0];
            }
        }

        return this;
    }

    protected static class Section {
        private final int sectionY;
        private byte[] blockLight;
        private long[] blocks;
        private BlockState[] palette = new BlockState[0];
        private final int bitsPerBlock;

        public Section(@NonNull CompoundTag sectionData) {
            this.sectionY = sectionData.getByte("Y");
            this.blockLight = sectionData.getByteArray("BlockLight");
            this.blocks = sectionData.getLongArray("BlockStates");

            if (this.blocks.length < 256 && this.blocks.length > 0) {
                this.blocks = Arrays.copyOf(this.blocks, 256);
            }
            if (this.blockLight.length < 2048 && this.blockLight.length > 0) {
                this.blockLight = Arrays.copyOf(this.blockLight, 2048);
            }

            ListTag<CompoundTag> paletteTag = sectionData.getListTag("Palette").asCompoundTagList();
            if (paletteTag != null) {
                this.palette = new BlockState[paletteTag.size()];
                for (int i = 0; i < this.palette.length; i++) {
                    CompoundTag stateTag = paletteTag.get(i);
                    String id = stateTag.getString("Name");
                    Block block = Pl3xMap.api().getBlockRegistry().getOrDefault(id, Blocks.AIR);
                    Map<String, String> properties = new HashMap<>();
                    CompoundTag propertiesTag = stateTag.getCompoundTag("Properties");
                    if (propertiesTag != null) {
                        for (Map.Entry<String, Tag<?>> property : propertiesTag) {
                            properties.put(property.getKey().toLowerCase(), ((StringTag) property.getValue()).getValue().toLowerCase());
                        }
                    }
                    this.palette[i] = new BlockState(block, properties);
                }
            }

            this.bitsPerBlock = this.blocks.length >> 6;
        }

        public int getSectionY() {
            return this.sectionY;
        }

        public @NonNull BlockState getBlockState(int x, int y, int z) {
            if (this.palette.length == 1) {
                return this.palette[0];
            }
            if (this.blocks.length == 0) {
                return Blocks.AIR.getDefaultState();
            }
            int index = ((y & 0xF) << 8) + ((z & 0xF) << 4) + (x & 0xF);
            long value = MCAMath.getValueFromLongArray(this.blocks, index, this.bitsPerBlock);
            if (value >= this.palette.length) {
                return Blocks.AIR.getDefaultState();
            }
            return this.palette[(int) value];
        }

        public int getLight(int x, int y, int z) {
            if (this.blockLight.length == 0) {
                return 0;
            }
            int index = ((y & 0xF) << 8) + ((z & 0xF) << 4) + (x & 0xF);
            int half = index >> 1;
            boolean upper = (index & 0x1) != 0;
            return MCAMath.getByteHalf(this.blockLight[half], upper);
        }
    }
}
