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

import de.bluecolored.bluenbt.NBTName;
import lombok.Getter;
import net.pl3x.map.core.util.MCAMath;
import net.pl3x.map.core.util.PackedIntArrayAccess;
import org.jetbrains.annotations.Nullable;

public class Chunk_1_18 extends Chunk {
    private final boolean full;
    private long inhabitedTime;

    private int worldMinY;

    private boolean hasWorldSurfaceHeights;
    private PackedIntArrayAccess worldSurfaceHeights;

    private Section[] sections;
    private int sectionMin, sectionMax;

    public Chunk_1_18(World world, Region region, Data data, int index) {
        super(world, region, data, index);

        this.full = data.status.endsWith("full"); // 1.20 uses minecraft namespace here
        if (!this.full) {
            return;
        }

        this.inhabitedTime = data.inhabitedTime;

        this.worldMinY = world.getMinBuildHeight();

        int worldHeight = world.getDimensionHeight();
        int bitsPerHeightmapElement = MCAMath.ceilLog2(worldHeight + 1);

        this.worldSurfaceHeights = new PackedIntArrayAccess(bitsPerHeightmapElement, data.heightmaps.worldSurface);
        this.hasWorldSurfaceHeights = this.worldSurfaceHeights.isCorrectSize(VALUES_PER_HEIGHTMAP);

        SectionData[] sectionsData = data.sections;
        if (sectionsData != null && sectionsData.length > 0) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            // find section min/max y
            for (SectionData sectionData : sectionsData) {
                int y = sectionData.getY();
                if (min > y) min = y;
                if (max < y) max = y;
            }

            // load sections into ordered array
            this.sections = new Section[1 + max - min];
            for (SectionData sectionData : sectionsData) {
                Section section = new Section(getWorld(), sectionData);
                int y = section.getSectionY();

                if (min > y) min = y;
                if (max < y) max = y;

                this.sections[section.sectionY - min] = section;
            }

            this.sectionMin = min;
            this.sectionMax = max;
        } else {
            this.sections = new Section[0];
            this.sectionMin = 0;
            this.sectionMax = 0;
        }

    }

    @Override
    public boolean isFull() {
        return full;
    }

    @Override
    public long getInhabitedTime() {
        return inhabitedTime;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        int sectionY = y >> 4;
        Section section = getSection(sectionY);
        return section == null ? Blocks.AIR.getDefaultState() : section.getBlockState(x, y, z);
    }

    @Override
    public Biome getBiome(int x, int y, int z) {
        int sectionY = y >> 4;
        Section section = getSection(sectionY);
        return section == null ? Biome.DEFAULT : section.getBiome(x, y, z);
    }

    @Override
    public int getLight(int x, int y, int z) {
        int sectionY = y >> 4;
        Section section = getSection(sectionY);
        return section == null ? ((sectionY < this.sectionMin) ? 0 : getWorld().getSkylight()) : section.getLight(x, y, z);
    }

    @Override
    public int getMinY() {
        return sectionMin * 16;
    }

    @Override
    public int getMaxY() {
        return sectionMax * 16 + 15;
    }

    @Override
    public boolean hasWorldSurfaceHeights() {
        return hasWorldSurfaceHeights;
    }

    @Override
    public int getWorldSurfaceY(int x, int z) {
        return worldSurfaceHeights.get((z & 0xF) << 4 | x & 0xF) + worldMinY;
    }

    private @Nullable Section getSection(int y) {
        y -= sectionMin;
        if (y < 0 || y >= this.sections.length) return null;
        return this.sections[y];
    }

    protected static class Section {

        private final int sectionY;
        private final BlockState[] blockPalette;
        private final Biome[] biomePalette;
        private final PackedIntArrayAccess blocks;
        private final PackedIntArrayAccess biomes;
        private final byte[] blockLight;

        public Section(World world, SectionData sectionData) {
            this.sectionY = sectionData.y;

            this.blockPalette = sectionData.blockStates.palette;

            this.biomePalette = new Biome[sectionData.biomes.palette.length];
            for (int i = 0; i < this.biomePalette.length; i++) {
                this.biomePalette[i] = world.getBiomeRegistry().getOrDefault(sectionData.biomes.palette[i], Biome.DEFAULT);
            }

            this.blocks = new PackedIntArrayAccess(sectionData.blockStates.data, BLOCKS_PER_SECTION);
            this.biomes = new PackedIntArrayAccess(Math.max(MCAMath.ceilLog2(this.biomePalette.length), 1), sectionData.biomes.data);

            this.blockLight = sectionData.blockLight;
        }

        public BlockState getBlockState(int x, int y, int z) {
            if (blockPalette.length == 1) return blockPalette[0];
            if (blockPalette.length == 0) return Blocks.AIR.getDefaultState();

            int id = blocks.get((y & 0xF) << 8 | (z & 0xF) << 4 | x & 0xF);
            if (id >= blockPalette.length) {
                return Blocks.AIR.getDefaultState();
            }

            return blockPalette[id];
        }

        public Biome getBiome(int x, int y, int z) {
            if (biomePalette.length == 1) return biomePalette[0];
            if (biomePalette.length == 0) return Biome.DEFAULT;

            int id = biomes.get((y & 0b1100) << 2 | z & 0b1100 | (x & 0b1100) >> 2);
            if (id >= biomePalette.length) {
                return Biome.DEFAULT;
            }

            return biomePalette[id];
        }

        public int getLight(int x, int y, int z) {
            if (blockLight.length == 0) return 0;

            int blockByteIndex = (y & 0xF) << 8 | (z & 0xF) << 4 | x & 0xF;
            int blockHalfByteIndex = blockByteIndex >> 1; // blockByteIndex / 2
            boolean largeHalf = (blockByteIndex & 0x1) != 0; // (blockByteIndex % 2) == 0

            return MCAMath.getByteHalf(this.blockLight[blockHalfByteIndex], largeHalf);
        }

        public int getSectionY() {
            return sectionY;
        }

    }

    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Data extends Chunk.Data {

        @NBTName("Status")
        private String status = "minecraft:empty";

        @NBTName("InhabitedTime")
        private long inhabitedTime = 0;

        @NBTName("Heightmaps")
        private HeightmapsData heightmaps = new HeightmapsData();

        private SectionData @Nullable [] sections = null;

    }

    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    public static class HeightmapsData {

        @NBTName("WORLD_SURFACE")
        private long[] worldSurface = EMPTY_LONG_ARRAY;

    }

    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    public static class SectionData {

        @NBTName("Y")
        private int y = 0;

        @NBTName("BlockLight")
        private byte[] blockLight = EMPTY_BYTE_ARRAY;

        private BlockStatesData blockStates = new BlockStatesData();

        private BiomesData biomes = new BiomesData();

    }

    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    public static class BlockStatesData {

        private BlockState[] palette = EMPTY_BLOCKSTATE_ARRAY;

        private long[] data = EMPTY_LONG_ARRAY;

    }

    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    public static class BiomesData {

        private String[] palette = EMPTY_STRING_ARRAY;

        private long[] data = EMPTY_LONG_ARRAY;

    }

}
