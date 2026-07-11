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
import net.pl3x.map.core.util.MCAMath;
import net.pl3x.map.core.util.PackedIntArrayAccess;
import org.jetbrains.annotations.Nullable;

public class Chunk_1_16 extends Chunk {

    private final boolean full;
    private long inhabitedTime;

    private boolean hasWorldSurfaceHeights;
    private PackedIntArrayAccess worldSurfaceHeights;

    private Section[] sections;
    private int sectionMin, sectionMax;

    private int[] biomes;

    public Chunk_1_16(World world, Region region, Data data, int index) {
        super(world, region, data, index);

        Level level = data.level;

        this.full = level.status.equals("full");
        if (!this.full) {
            return;
        }

        this.inhabitedTime = level.inhabitedTime;

        int worldHeight = world.getDimensionHeight();
        int bitsPerHeightmapElement = MCAMath.ceilLog2(worldHeight + 1);

        this.worldSurfaceHeights = new PackedIntArrayAccess(bitsPerHeightmapElement, level.heightmaps.worldSurface);
        this.hasWorldSurfaceHeights = this.worldSurfaceHeights.isCorrectSize(VALUES_PER_HEIGHTMAP);

        this.biomes = level.biomes;

        SectionData[] sectionsData = level.sections;
        if (sectionsData != null && sectionsData.length > 0) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            // find section min/max y
            for (SectionData sectionData : sectionsData) {
                int y = sectionData.y;
                if (min > y) min = y;
                if (max < y) max = y;
            }

            // load sections into ordered array
            this.sections = new Section[1 + max - min];
            for (SectionData sectionData : sectionsData) {
                Section section = new Section(sectionData);
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
        if (this.biomes.length < 16) return Biome.DEFAULT;

        int biomeIntIndex = (y & 0b1100) << 2 | z & 0b1100 | (x & 0b1100) >> 2;

        // shift y up/down if not in range
        if (biomeIntIndex >= biomes.length) biomeIntIndex -= (((biomeIntIndex - biomes.length) >> 4) + 1) * 16;
        if (biomeIntIndex < 0) biomeIntIndex -= (biomeIntIndex >> 4) * 16;

        return LegacyBiomes.get(biomes[biomeIntIndex]);
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
        return worldSurfaceHeights.get((z & 0xF) << 4 | x & 0xF);
    }

    private @Nullable Section getSection(int y) {
        y -= sectionMin;
        if (y < 0 || y >= this.sections.length) return null;
        return this.sections[y];
    }

    protected static class Section {

        private final int sectionY;
        private final BlockState[] blockPalette;
        private final PackedIntArrayAccess blocks;
        private final byte[] blockLight;

        public Section(SectionData sectionData) {
            this.sectionY = sectionData.y;

            this.blockPalette = sectionData.palette;
            this.blocks = new PackedIntArrayAccess(sectionData.blockStates, BLOCKS_PER_SECTION);

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

    @SuppressWarnings("FieldMayBeFinal")
    public static class Data extends Chunk.Data {

        @NBTName("Level")
        private Level level = new Level();

    }

    @SuppressWarnings("FieldMayBeFinal")
    public static class Level {

        @NBTName("Status")
        private String status = "empty";

        @NBTName("InhabitedTime")
        private long inhabitedTime = 0;

        @NBTName("Heightmaps")
        private HeightmapsData heightmaps = new HeightmapsData();

        @NBTName("Sections")
        private SectionData @Nullable [] sections = null;

        @NBTName("Biomes")
        private int[] biomes = EMPTY_INT_ARRAY;

    }

    @SuppressWarnings("FieldMayBeFinal")
    public static class HeightmapsData {

        @NBTName("WORLD_SURFACE")
        private long[] worldSurface = EMPTY_LONG_ARRAY;

    }

    @SuppressWarnings("FieldMayBeFinal")
    public static class SectionData {

        @NBTName("Y")
        private int y = 0;

        @NBTName("BlockLight")
        private byte[] blockLight = EMPTY_BYTE_ARRAY;

        @NBTName("Palette")
        private BlockState[] palette = EMPTY_BLOCKSTATE_ARRAY;

        @NBTName("BlockStates")
        private long[] blockStates = EMPTY_LONG_ARRAY;

    }

}
