package net.pl3x.map.util;

import com.destroystokyo.paper.io.PaperFileIOThread;
import com.destroystokyo.paper.io.PrioritizedTaskQueue;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.pl3x.map.render.iterator.coordinate.ChunkCoordinate;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.world.MapWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ChunkHelper {
    private final Map<ChunkCoordinate, ChunkAccess> chunkCache = new HashMap<>();
    private final AbstractRender render;

    private final Consumer<String> onError = s -> {
    };

    public ChunkHelper(AbstractRender render) {
        this.render = render;
    }

    public void clear() {
        this.chunkCache.clear();
    }

    public ChunkAccess getChunk(ServerLevel level, int chunkX, int chunkZ) {
        return this.chunkCache.computeIfAbsent(new ChunkCoordinate(chunkX, chunkZ), k -> getChunkFast(level, chunkX, chunkZ));
    }

    @SuppressWarnings("unused")
    public ChunkAccess getChunkSlow(ServerLevel level, int chunkX, int chunkZ) {
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> future =
                level.getChunkSource().getChunkAtAsynchronously(chunkX, chunkZ, false, false);
        while (!future.isDone()) {
            if (this.render.isCancelled()) {
                return null;
            }
        }
        return future.join().left().orElse(null);
    }

    // ChunkSerializer#loadChunk
    public ChunkAccess getChunkFast(ServerLevel level, int chunkX, int chunkZ) {
        // load chunk NBT from region file
        CompoundTag nbt = PaperFileIOThread.Holder.INSTANCE.loadChunkData(level, chunkX, chunkZ, PrioritizedTaskQueue.HIGHEST_PRIORITY, false, true).chunkData;

        // we only care about "full" chunks (aka, level chunks)
        if (ChunkSerializer.getChunkTypeFromTag(nbt) != ChunkStatus.ChunkType.LEVELCHUNK) {
            return null;
        }

        // build only the required palettes from chunk sections
        ListTag sectionsNBT = nbt.getList("sections", 10);
        LevelChunkSection[] levelChunkSections = new LevelChunkSection[level.getSectionsCount()];
        Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        for (int j = 0; j < sectionsNBT.size(); ++j) {
            CompoundTag chunkSectionNBT = sectionsNBT.getCompound(j);
            byte chunkYPos = chunkSectionNBT.getByte("Y");
            int index = level.getSectionIndexFromSectionY(chunkYPos);
            if (index >= 0 && index < levelChunkSections.length) {
                PalettedContainer<BlockState> states;
                if ((this.render.renderBlocks() || this.render.renderFluids()) && chunkSectionNBT.contains("block_states", 10)) {
                    states = ChunkSerializer.BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, chunkSectionNBT.getCompound("block_states")).getOrThrow(false, onError);
                } else {
                    states = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES, null);
                }
                PalettedContainer<Holder<Biome>> biomes;
                if (this.render.renderBiomes() && chunkSectionNBT.contains("biomes", 10)) {
                    Codec<PalettedContainer<Holder<Biome>>> biomeCodec = PalettedContainer.codecRW(biomeRegistry.asHolderIdMap(), biomeRegistry.holderByNameCodec(), PalettedContainer.Strategy.SECTION_BIOMES, biomeRegistry.getHolderOrThrow(Biomes.PLAINS), null);
                    biomes = biomeCodec.parse(NbtOps.INSTANCE, chunkSectionNBT.getCompound("biomes")).getOrThrow(false, onError);
                } else {
                    biomes = new PalettedContainer<>(biomeRegistry.asHolderIdMap(), biomeRegistry.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES, null);
                }
                levelChunkSections[index] = new LevelChunkSection(chunkYPos, states, biomes);
            }
        }

        // create our chunk
        LevelChunk chunk = new LevelChunk(level.getLevel(), new ChunkPos(chunkX, chunkZ), UpgradeData.EMPTY, new LevelChunkTicks<>(), new LevelChunkTicks<>(), 0, levelChunkSections, o -> {
        }, null);

        // populate the heightmap from NBT
        if (this.render.renderHeights()) {
            chunk.setHeightmap(Heightmap.Types.WORLD_SURFACE, nbt.getCompound("Heightmaps").getLongArray(Heightmap.Types.WORLD_SURFACE.getSerializationKey()));
        }

        // rejoice
        return chunk;
    }

    // BiomeManager#getBiome
    public Holder<Biome> getBiome(MapWorld mapWorld, BlockPos pos) {
        int i = pos.getX() - 2;
        int j = pos.getY() - 2;
        int k = pos.getZ() - 2;
        int l = i >> 2;
        int m = j >> 2;
        int n = k >> 2;
        double d = (double) (i & 3) / 4.0D;
        double e = (double) (j & 3) / 4.0D;
        double f = (double) (k & 3) / 4.0D;
        int o = 0;
        double g = Double.POSITIVE_INFINITY;

        for (int p = 0; p < 8; ++p) {
            boolean bl = (p & 4) == 0;
            boolean bl2 = (p & 2) == 0;
            boolean bl3 = (p & 1) == 0;
            int q = bl ? l : l + 1;
            int r = bl2 ? m : m + 1;
            int s = bl3 ? n : n + 1;
            double h = bl ? d : d - 1.0D;
            double t = bl2 ? e : e - 1.0D;
            double u = bl3 ? f : f - 1.0D;
            double v = getFiddledDistance(mapWorld.getBiomeSeed(), q, r, s, h, t, u);
            if (g > v) {
                o = p;
                g = v;
            }
        }

        int w = (o & 4) == 0 ? l : l + 1;
        int x = (o & 2) == 0 ? m : m + 1;
        int y = (o & 1) == 0 ? n : n + 1;
        // had to copy this entire method just to change this... :3
        //noinspection SuspiciousNameCombination
        return getNoiseBiome(mapWorld.getLevel(), w, x, y);
    }

    // BiomeManager#getFiddledDistance
    private static double getFiddledDistance(long l, int i, int j, int k, double d, double e, double f) {
        long m = LinearCongruentialGenerator.next(l, i);
        m = LinearCongruentialGenerator.next(m, j);
        m = LinearCongruentialGenerator.next(m, k);
        m = LinearCongruentialGenerator.next(m, i);
        m = LinearCongruentialGenerator.next(m, j);
        m = LinearCongruentialGenerator.next(m, k);
        double g = getFiddle(m);
        m = LinearCongruentialGenerator.next(m, l);
        double h = getFiddle(m);
        m = LinearCongruentialGenerator.next(m, l);
        double n = getFiddle(m);
        return Mth.square(f + n) + Mth.square(e + h) + Mth.square(d + g);
    }

    // BiomeManager#getFiddle
    private static double getFiddle(long l) {
        double d = (double) Math.floorMod(l >> 24, 1024) / 1024.0D;
        return (d - 0.5D) * 0.9D;
    }

    // nifty trick - don't schedule a blocking getChunk call..
    // LevelReader#getNoiseBiome
    private Holder<Biome> getNoiseBiome(ServerLevel level, int biomeX, int biomeY, int biomeZ) {
        ChunkAccess chunkAccess = getChunk(level, QuartPos.toSection(biomeX), QuartPos.toSection(biomeZ));
        return chunkAccess != null ? chunkAccess.getNoiseBiome(biomeX, biomeY, biomeZ) : level.getUncachedNoiseBiome(biomeX, biomeY, biomeZ);
    }
}
