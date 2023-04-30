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
package net.pl3x.map.bukkit;

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

public class BukkitWorld extends World {
    private final ServerLevel level;

    public BukkitWorld(@NonNull ServerLevel level, @NonNull String name) {
        super(
                name,
                level.getSeed(),
                Point.of(level.getLevelData().getXSpawn(), level.getLevelData().getZSpawn()),
                Type.get(level.dimension().location().toString()),
                level.convertable.getDimensionPath(level.dimension()).resolve("region"),
                new WorldConfig(name)
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
    @SuppressWarnings({"unchecked"})
    public <@NonNull T> @NonNull T getLevel() {
        return (@NonNull T) this.level;
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
    public @NonNull Border getWorldBorder() {
        return new Border(this.level.getWorldBorder().getCenterX(),
                this.level.getWorldBorder().getCenterZ(),
                this.level.getWorldBorder().getSize());
    }

    @Override
    public @NonNull Collection<@NonNull Player> getPlayers() {
        return this.<ServerLevel>getLevel().players().stream()
                .map(player -> Pl3xMap.api().getPlayerRegistry().get(player.getUUID()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public @NonNull String toString() {
        return "BukkitWorld{"
                + "name=" + getName()
                + ",seed=" + getSeed()
                + ",spawn=" + getSpawn()
                + "}";
    }
}
