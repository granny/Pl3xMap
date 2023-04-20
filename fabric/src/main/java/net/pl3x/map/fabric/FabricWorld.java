package net.pl3x.map.fabric;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.ColorsConfig;
import net.pl3x.map.core.configuration.WorldConfig;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FabricWorld extends World {
    private final ServerLevel level;

    public FabricWorld(@NonNull ServerLevel level, @NonNull String name, @NonNull WorldConfig worldConfig) {
        super(
                name,
                level.getSeed(),
                Point.of(level.getLevelData().getXSpawn(), level.getLevelData().getXSpawn()),
                Type.get(level.dimension().location().toString()),
                level.getChunkSource().getDataStorage().dataFolder.toPath().getParent().resolve("region"),
                worldConfig
        );
        this.level = level;

        if (!isEnabled()) {
            return;
        }

        // register biomes
        for (Map.Entry<ResourceKey<Biome>, Biome> entry : level.registryAccess().registryOrThrow(Registries.BIOME).entrySet()) {
            String id = entry.getKey().location().toString();
            Biome biome = entry.getValue();
            float temperature = Mathf.clamp(0.0F, 1.0F, biome.getBaseTemperature());
            float humidity = Mathf.clamp(0.0F, 1.0F, biome.climateSettings.downfall());
            getBiomeRegistry().register(
                    id,
                    ColorsConfig.BIOME_COLORS.getOrDefault(id, 0),
                    ColorsConfig.BIOME_FOLIAGE.getOrDefault(id, biome.getSpecialEffects().getFoliageColorOverride().orElse(Colors.getDefaultFoliageColor(temperature, humidity))),
                    ColorsConfig.BIOME_GRASS.getOrDefault(id, biome.getSpecialEffects().getGrassColorOverride().orElse(Colors.getDefaultGrassColor(temperature, humidity))),
                    ColorsConfig.BIOME_WATER.getOrDefault(id, biome.getSpecialEffects().getWaterColor()),
                    (x, z, color) -> biome.getSpecialEffects().getGrassColorModifier().modifyColor(x, z, color)
            );
        }

        getBiomeRegistry().saveToDisk(this);
    }

    @Override
    @NonNull
    @SuppressWarnings({"unchecked"})
    public <T> T getLevel() {
        return (T) this.level;
    }

    @Override
    public long hashSeed(long seed) {
        return BiomeManager.obfuscateSeed(seed);
    }

    @Override
    public boolean hasCeiling() {
        return this.level.dimensionType().hasCeiling();
    }

    @Override
    public int getMinBuildHeight() {
        return this.level.getMinBuildHeight();
    }

    @Override
    @NonNull
    public Border getWorldBorder() {
        return new Border(this.level.getWorldBorder().getCenterX(),
                this.level.getWorldBorder().getCenterZ(),
                this.level.getWorldBorder().getSize());
    }

    @Override
    @NonNull
    public Collection<Player> getPlayers() {
        return this.<ServerLevel>getLevel().players().stream()
                .map(player -> Pl3xMap.api().getPlayerRegistry().get(player.getUUID()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
