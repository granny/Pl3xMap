package net.pl3x.map.util;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

public class ReflectionHelper {
    private static final Field statesField;
    private static final Field biomesField;
    private static final Field nonEmptyBlockCountField;

    static {
        try {
            //noinspection JavaReflectionMemberAccess
            statesField = LevelChunkSection.class.getDeclaredField("i");
            statesField.setAccessible(true);

            biomesField = LevelChunkSection.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);

            //noinspection JavaReflectionMemberAccess
            nonEmptyBlockCountField = LevelChunkSection.class.getDeclaredField("f");
            nonEmptyBlockCountField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Consumer<String> onError = s -> {
    };

    private ReflectionHelper() {
    }

    public static LevelChunkSection createLevelChunkSection(int chunkYPos, CompoundTag chunkSectionNBT, Registry<Biome> biomeRegistry) {
        //noinspection ConstantConditions
        LevelChunkSection levelChunkSection = new LevelChunkSection(chunkYPos, biomeRegistry, null, null);
        try {
            statesField.set(levelChunkSection, chunkSectionNBT.contains("block_states", 10) ?
                    ChunkSerializer.BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, chunkSectionNBT.getCompound("block_states")).getOrThrow(false, onError) :
                    new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES, null));

            biomesField.set(levelChunkSection, chunkSectionNBT.contains("biomes", 10) ?
                    PalettedContainer.codecRW(biomeRegistry.asHolderIdMap(), biomeRegistry.holderByNameCodec(), PalettedContainer.Strategy.SECTION_BIOMES, biomeRegistry.getHolderOrThrow(Biomes.PLAINS), null)
                            .parse(NbtOps.INSTANCE, chunkSectionNBT.getCompound("biomes")).getOrThrow(false, onError) :
                    new PalettedContainer<>(biomeRegistry.asHolderIdMap(), biomeRegistry.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES, null));

            nonEmptyBlockCountField.set(levelChunkSection, Short.MAX_VALUE);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return levelChunkSection;
    }
}
