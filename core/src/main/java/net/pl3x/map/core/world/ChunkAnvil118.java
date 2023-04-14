package net.pl3x.map.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.pl3x.map.core.Pl3xMap;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChunkAnvil118 extends Chunk {
    private int sectionMin = Integer.MAX_VALUE;
    private int sectionMax = Integer.MIN_VALUE;

    private Section[] sections = new Section[0];

    protected long[] oceanFloorHeights = new long[0];
    protected long[] worldSurfaceHeights = new long[0];

    protected ChunkAnvil118(World world, Region region, CompoundTag chunkTag) {
        super(world, region, chunkTag);

        if (chunkTag.containsKey("Heightmaps")) {
            CompoundTag heightmaps = chunkTag.getCompoundTag("Heightmaps");
            this.worldSurfaceHeights = heightmaps.getLongArray("WORLD_SURFACE");
            this.oceanFloorHeights = heightmaps.getLongArray("OCEAN_FLOOR");
        }

        if (chunkTag.containsKey("sections")) {
            ListTag<CompoundTag> sections = chunkTag.getListTag("sections").asCompoundTagList();
            List<Section> list = new ArrayList<>(sections.size());
            for (CompoundTag sectionTag : sections) {
                Section section = new Section(world, sectionTag);
                int y = section.getSectionY();
                if (this.sectionMin > y) this.sectionMin = y;
                if (this.sectionMax < y) this.sectionMax = y;
                list.add(section);
            }
            this.sections = new Section[1 + this.sectionMax - this.sectionMin];
            for (Section section : list) {
                this.sections[section.getSectionY() - this.sectionMin] = section;
            }
        }
    }

    @Override
    @NonNull
    public BlockState getBlockState(int x, int y, int z) {
        int sectionY = y >> 4;
        Section section = getSection(sectionY);
        return section == null ? Block.AIR.getDefaultState() : section.getBlockState(x, y, z);
    }

    @Override
    public int getLight(int x, int y, int z) {
        int sectionY = y >> 4;
        Section section = getSection(sectionY);
        if (section == null) {
            return (sectionY < this.sectionMin) ? 0 : getWorld().getSkylight();
        }
        return section.getLight(x, y, z);
    }

    @Override
    @NonNull
    public Biome getBiome(int x, int y, int z) {
        int sectionY = y >> 4;
        Section section = getSection(sectionY);
        return section == null ? Biome.DEFAULT : section.getBiome(x, y, z);
    }

    @Override
    public int getMinY(int x, int z) {
        return sectionMin << 4;
    }

    @Override
    public int getMaxY(int x, int z) {
        return (sectionMax << 4) + 0xF;
    }

    @Override
    public int getWorldSurfaceY(int x, int z) {
        // todo cache
        if (this.worldSurfaceHeights.length < 37) {
            return 0;
        }
        return (int) getValueFromLongArray(this.worldSurfaceHeights, ((z & 0xF) << 4) + (x & 0xF), 9) - 64;
    }

    @Override
    public int getOceanFloorY(int x, int z) {
        // todo cache
        if (this.oceanFloorHeights.length < 37) {
            return 0;
        }
        return (int) getValueFromLongArray(this.oceanFloorHeights, ((z & 0xF) << 4) + (x & 0xF), 9) - 64;
    }

    @Override
    @NonNull
    public Chunk populate() {
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
                data.blockY = getWorldSurfaceY(blockX, blockZ) + 1;

                // if world has ceiling iterate down until we find air
                if (getWorld().hasCeiling()) {
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

        for (Section section : this.sections) {
            section.blocks = new long[0];
            section.blockPalette = new BlockState[0];
        }

        return this;
    }

    @Nullable
    private Section getSection(int y) {
        y -= this.sectionMin;
        return y < 0 || y >= this.sections.length ? null : this.sections[y];
    }

    protected static class Section {
        private final int sectionY;
        private byte[] blockLight;
        //private byte[] skyLight;
        private long[] blocks;
        private long[] biomes = new long[0];
        private BlockState[] blockPalette = new BlockState[0];
        private Biome[] biomePalette = new Biome[0];
        private final int bitsPerBlock;
        private final int bitsPerBiome;

        public Section(World world, CompoundTag sectionData) {
            this.sectionY = sectionData.getByte("Y");
            this.blockLight = sectionData.getByteArray("BlockLight");
            //this.skyLight = sectionData.getByteArray("SkyLight");
            this.blocks = sectionData.getLongArray("BlockStates");

            CompoundTag blockStatesTag = sectionData.getCompoundTag("block_states");
            if (blockStatesTag != null) {
                this.blocks = blockStatesTag.getLongArray("data");
                ListTag<CompoundTag> paletteTag = blockStatesTag.getListTag("palette").asCompoundTagList();
                if (paletteTag != null) {
                    this.blockPalette = new BlockState[paletteTag.size()];
                    for (int i = 0; i < this.blockPalette.length; i++) {
                        CompoundTag entry = paletteTag.get(i);
                        String id = entry.getString("Name");
                        Block block = Pl3xMap.api().getBlockRegistry().getOrDefault(id, Block.AIR);
                        /*Map<String, String> properties = new LinkedHashMap<>();
                        CompoundTag propertiesTag = entry.getCompoundTag("Properties");
                        if (propertiesTag != null) {
                            for (Map.Entry<String, Tag<?>> property : propertiesTag) {
                                properties.put(property.getKey().toLowerCase(), ((StringTag) property.getValue()).getValue().toLowerCase());
                            }
                        }*/
                        this.blockPalette[i] = new BlockState(block);//, properties);
                    }
                }
            }

            CompoundTag biomesTag = sectionData.getCompoundTag("biomes");
            if (biomesTag != null) {
                this.biomes = biomesTag.getLongArray("data");
                ListTag<StringTag> paletteTag = biomesTag.getListTag("palette").asStringTagList();
                if (paletteTag != null) {
                    this.biomePalette = new Biome[paletteTag.size()];
                    for (int i = 0; i < this.biomePalette.length; i++) {
                        biomePalette[i] = world.getBiomeRegistry().getOrDefault(paletteTag.get(i).getValue(), Biome.DEFAULT);
                    }
                }
            }

            if (this.blocks.length < 256 && this.blocks.length > 0) {
                this.blocks = Arrays.copyOf(this.blocks, 256);
            }
            if (this.blockLight.length < 2048 && this.blockLight.length > 0) {
                this.blockLight = Arrays.copyOf(this.blockLight, 2048);
            }
            //if (this.skyLight.length < 2048 && this.skyLight.length > 0) {
            //    this.skyLight = Arrays.copyOf(this.skyLight, 2048);
            //}
            this.bitsPerBlock = this.blocks.length >> 6;
            this.bitsPerBiome = Integer.SIZE - Integer.numberOfLeadingZeros(this.biomePalette.length - 1);
        }

        public int getSectionY() {
            return this.sectionY;
        }

        @NonNull
        public BlockState getBlockState(int x, int y, int z) {
            if (this.blockPalette.length == 1) {
                return this.blockPalette[0];
            }
            if (this.blocks.length == 0) {
                return Block.AIR.getDefaultState();
            }
            int blockIndex = ((y & 0xF) << 8) + ((z & 0xF) << 4) + (x & 0xF);
            long value = getValueFromLongArray(this.blocks, blockIndex, this.bitsPerBlock);
            if (value >= this.blockPalette.length) {
                return Block.AIR.getDefaultState();
            }
            return this.blockPalette[(int) value];
        }

        public int getLight(int x, int y, int z) {
            if (this.blockLight.length == 0) {// && this.skyLight.length == 0) {
                return 0;
            }
            int blockByteIndex = ((y & 0xF) << 8) + ((z & 0xF) << 4) + (x & 0xF);
            int blockHalfByteIndex = blockByteIndex >> 1;
            boolean largeHalf = (blockByteIndex & 0x1) != 0;
            return getByteHalf(this.blockLight[blockHalfByteIndex], largeHalf);
        }

        @NonNull
        public Biome getBiome(int x, int y, int z) {
            if (this.biomePalette.length == 0) {
                return Biome.DEFAULT;
            }
            if (this.biomePalette.length == 1 || this.biomes.length == 0) {
                return this.biomePalette[0];
            }
            int biomeIndex = (((y & 0xF) >> 2) << 4) + (((z & 0xF) >> 2) << 2) + ((x & 0xF) >> 2);
            long value = getValueFromLongArray(this.biomes, biomeIndex, this.bitsPerBiome);
            if (value >= this.biomePalette.length) {
                return Biome.DEFAULT;
            }
            return this.biomePalette[(int) value];
        }
    }
}
