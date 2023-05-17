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
package net.pl3x.map.core.world;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.util.MCAMath;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Chunk {
    private final World world;
    private final Region region;

    private final int xPos;
    private final int yPos;
    private final int zPos;

    private final long inhabitedTime;

    protected Section[] sections = new Section[0];

    protected int[] biomes;

    private final int minHeightmapLength;
    protected long[] worldSurfaceHeights = new long[0];

    protected boolean full;

    private final BlockData[] data = new BlockData[256];

    private boolean populated;

    protected Chunk(@NotNull World world, @NotNull Region region) {
        this.world = world;
        this.region = region;

        this.xPos = 0;
        this.yPos = 0;
        this.zPos = 0;

        this.inhabitedTime = 0;
        this.minHeightmapLength = 0;
    }

    protected Chunk(@NotNull World world, @NotNull Region region, @NotNull CompoundTag tag, int minHeightmapLength) {
        this.world = world;
        this.region = region;

        Tag<?> pos = tag.get("xPos");
        this.xPos = pos instanceof IntTag ? ((IntTag) pos).asInt() : ((ByteTag) pos).asInt();
        pos = tag.get("yPos");
        this.yPos = pos instanceof IntTag ? ((IntTag) pos).asInt() : ((ByteTag) pos).asInt();
        pos = tag.get("zPos");
        this.zPos = pos instanceof IntTag ? ((IntTag) pos).asInt() : ((ByteTag) pos).asInt();

        this.inhabitedTime = tag.getLong("InhabitedTime");
        this.minHeightmapLength = minHeightmapLength;
    }

    public @NotNull World getWorld() {
        return this.world;
    }

    public @NotNull Region getRegion() {
        return this.region;
    }

    public int getX() {
        return this.xPos;
    }

    public int getY() {
        return this.yPos;
    }

    public int getZ() {
        return this.zPos;
    }

    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    public boolean isFull() {
        return this.full;
    }

    public boolean noHeightmap() {
        return this.worldSurfaceHeights.length < this.minHeightmapLength;
    }

    public int getWorldSurfaceY(int x, int z) {
        if (noHeightmap()) {
            return getWorld().getMinBuildHeight();
        }
        return (int) MCAMath.getValueFromLongStream(this.worldSurfaceHeights, ((z & 0xF) << 4) + (x & 0xF), 9) + getWorld().getMinBuildHeight();
    }

    public @NotNull BlockState getBlockState(int x, int y, int z) {
        int sectionY = y >> 4;
        if (sectionY < 0 || sectionY >= this.sections.length) {
            return Blocks.AIR.getDefaultState();
        }
        Section section = this.sections[sectionY];
        return section == null ? Blocks.AIR.getDefaultState() : section.getBlockState(x, y, z);
    }

    public int getLight(int x, int y, int z) {
        int sectionY = y >> 4;
        if (sectionY < 0 || sectionY >= this.sections.length) {
            return (y < 0) ? 0 : getWorld().getSkylight();
        }
        Section section = this.sections[sectionY];
        return section == null ? getWorld().getSkylight() : section.getLight(x, y, z);
    }

    public abstract @NotNull Biome getBiome(int x, int y, int z);

    public @NotNull Chunk populate() {
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

                    if (data.blockstate.getBlock().isAir()) {
                        // we don't render air (set in colors.yml)
                        continue;
                    }

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

                    if (getWorld().getConfig().RENDER_TRANSLUCENT_GLASS && data.blockstate.getBlock().isGlass()) {
                        // translucent glass. store this color and keep iterating
                        data.glass.addFirst(Colors.setAlpha(0x99, data.blockstate.getBlock().color()));
                        continue;
                    }

                    // test if block is renderable. we ignore blocks with black color
                    if (data.blockstate.getBlock().color() > 0) {
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

        return this;
    }

    public @NotNull BlockData[] getData() {
        return this.data;
    }

    public @Nullable BlockData getData(int x, int z) {
        return this.data[((z & 0xF) << 4) + (x & 0xF)];
    }

    public static @NotNull Chunk create(@NotNull World world, @NotNull Region region, @NotNull CompoundTag tag) {
        // https://minecraft.fandom.com/wiki/Data_version#List_of_data_versions
        int version = tag.getInt("DataVersion");
        Chunk chunk;
        if (version < 1519) chunk = new EmptyChunk(world, region); // wtf, older than 1.13
        else if (version < 2200) chunk = new ChunkAnvil113(world, region, tag); // 1.13 - 1.14
        else if (version < 2500) chunk = new ChunkAnvil115(world, region, tag); // 1.15
        else if (version < 2844) chunk = new ChunkAnvil116(world, region, tag); // 1.16 - 1.18 (21w42a)
        else chunk = new ChunkAnvil118(world, region, tag); // 1.18+ (21w43a+)
        return chunk.isFull() ? chunk : new EmptyChunk(world, region);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Chunk other = (Chunk) o;
        return getWorld().equals(other.getWorld())
                && getX() == other.getX()
                && getY() == other.getY()
                && getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorld(), getX(), getY(), getZ());
    }

    @Override
    public @NotNull String toString() {
        return "Chunk{"
                + "world=" + getWorld()
                + ",xPos=" + getX()
                + ",yPos=" + getY()
                + ",zPos=" + getZ()
                + "}";
    }

    public static class Section {
        protected final int sectionY;
        protected long[] blocks;
        protected byte[] blockLight;
        protected BlockState[] blockPalette;
        protected int bitsPerBlock;

        public Section(@NotNull CompoundTag nbt) {
            this.sectionY = nbt.getByte("Y");
            init(nbt);
        }

        protected void init(@NotNull CompoundTag nbt) {
            this.blocks = blocks(nbt.getLongArray("BlockStates"));
            this.blockLight = light(nbt.getByteArray("BlockLight"));
            this.blockPalette = blockPalette(nbt);
            this.bitsPerBlock = this.blocks.length >> 6;
        }

        protected long[] blocks(long[] blocks) {
            return blocks.length < 256 && blocks.length > 0 ? Arrays.copyOf(blocks, 256) : blocks;
        }

        protected byte[] light(byte[] light) {
            return light.length < 2048 && light.length > 0 ? Arrays.copyOf(light, 2048) : light;
        }

        protected String paletteKey() {
            return "Palette";
        }

        protected @NotNull BlockState[] blockPalette(@NotNull CompoundTag nbt) {
            BlockState[] palette = new BlockState[0];
            ListTag<CompoundTag> paletteTag = nbt.getListTag(paletteKey()).asCompoundTagList();
            if (paletteTag != null) {
                palette = new BlockState[paletteTag.size()];
                for (int i = 0; i < palette.length; i++) {
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
                    palette[i] = new BlockState(block, properties);
                }
            }
            return palette;
        }

        public @NotNull BlockState getBlockState(int x, int y, int z) {
            if (this.blockPalette.length == 1) {
                return this.blockPalette[0];
            }
            if (this.blocks.length == 0) {
                return Blocks.AIR.getDefaultState();
            }
            int index = ((y & 0xF) << 8) + ((z & 0xF) << 4) + (x & 0xF);
            long value = MCAMath.getValueFromLongStream(this.blocks, index, this.bitsPerBlock);
            if (value >= this.blockPalette.length) {
                return Blocks.AIR.getDefaultState();
            }
            return this.blockPalette[(int) value];
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

    public static class BlockData {
        protected int blockY;
        protected int fluidY = 0;
        protected BlockState blockstate;
        protected BlockState fluidstate = null;
        protected Biome biome;

        protected final LinkedList<Integer> glass = new LinkedList<>();

        public int getBlockY() {
            return this.blockY;
        }

        public int getFluidY() {
            return this.fluidY;
        }

        public @NotNull BlockState getBlockState() {
            return this.blockstate;
        }

        public @Nullable BlockState getFluidState() {
            return this.fluidstate;
        }

        public @NotNull Biome getBiome(@NotNull Region region, int x, int z) {
            if (this.biome == null) {
                // calculate real biome
                this.biome = region.getWorld().getBiomeManager().getBiome(region, x, this.blockY, z);
            }
            return this.biome;
        }

        public @NotNull List<Integer> getGlassColors() {
            return this.glass;
        }
    }
}
