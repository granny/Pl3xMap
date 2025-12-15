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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.ColorsConfig;
import net.pl3x.map.core.event.world.WorldLoadedEvent;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.registry.BiomeRegistry;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.world.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BukkitWorld extends World {
    private static Field LEVEL_STORAGE_ACCESS_FIELD = null;

    static {
        if (LEVEL_STORAGE_ACCESS_FIELD == null) {
            Arrays.stream(ServerLevel.class.getFields())
                    .filter(field -> field.getType().equals(LevelStorageSource.LevelStorageAccess.class))
                    .findAny().ifPresent(field -> LEVEL_STORAGE_ACCESS_FIELD = field);
        }
    }

    private static LevelStorageSource.LevelStorageAccess getLevelStorageAccess(ServerLevel level) {
        try {
            return (LevelStorageSource.LevelStorageAccess) LEVEL_STORAGE_ACCESS_FIELD.get(level);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final ServerLevel level;

    public BukkitWorld(ServerLevel level, String name) {
        super(
                name,
                level.getSeed(),
                Point.of(level.getLevelData().getRespawnData().pos().getX(), level.getLevelData().getRespawnData().pos().getZ()),
                Type.get(level.dimension().identifier().toString()),
                BukkitWorld.getLevelStorageAccess(level).getDimensionPath(level.dimension()).resolve("region")
        );
        this.level = level;

        if (!isEnabled()) {
            return;
        }

        init();

        // register biomes
        Set<Map.Entry<ResourceKey<Biome>, Biome>> entries = level.registryAccess().lookupOrThrow(Registries.BIOME).entrySet();
        for (Map.Entry<ResourceKey<Biome>, Biome> entry : entries) {
            String id = entry.getKey().identifier().toString();
            Biome biome = entry.getValue();
            float temperature = Mathf.clamp(0.0F, 1.0F, biome.getBaseTemperature());
            float humidity = Mathf.clamp(0.0F, 1.0F, biome.climateSettings.downfall());
            getBiomeRegistry().register(
                    id,
                    ColorsConfig.BIOME_COLORS.getOrDefault(id, 0),
                    ColorsConfig.BIOME_DRY_FOLIAGE.getOrDefault(id, biome.getSpecialEffects().dryFoliageColorOverride().orElse(Colors.getDefaultDryFoliageColor(temperature, humidity))),
                    ColorsConfig.BIOME_FOLIAGE.getOrDefault(id, biome.getSpecialEffects().foliageColorOverride().orElse(Colors.getDefaultFoliageColor(temperature, humidity))),
                    ColorsConfig.BIOME_GRASS.getOrDefault(id, biome.getSpecialEffects().grassColorOverride().orElse(Colors.getDefaultGrassColor(temperature, humidity))),
                    ColorsConfig.BIOME_WATER.getOrDefault(id, biome.getSpecialEffects().waterColor()),
                    (x, z, color) -> biome.getSpecialEffects().grassColorModifier().modifyColor(x, z, color)
            );
        }

        getBiomeRegistry().saveToDisk(this);

        Pl3xMap.api().getEventRegistry().callEvent(new WorldLoadedEvent(this));
    }

    @Override
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
        return this.level.getMinY();
    }

    @Override
    public int getMaxBuildHeight() {
        return this.level.getMaxY() + 1;
    }

    @Override
    public int getDimensionHeight() {
        return this.level.dimensionType().height();
    }

    @Override
    public int getLogicalHeight() {
        return this.level.getLogicalHeight();
    }

    @Override
    public double getBorderMinX() {
        return this.level.getWorldBorder().getMinX();
    }

    @Override
    public double getBorderMinZ() {
        return this.level.getWorldBorder().getMinZ();
    }

    @Override
    public double getBorderMaxX() {
        return this.level.getWorldBorder().getMaxX();
    }

    @Override
    public double getBorderMaxZ() {
        return this.level.getWorldBorder().getMaxZ();
    }

    @Override
    public Collection<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        for (ServerPlayer serverPlayer : this.<ServerLevel>getLevel().players()) {
            Player player = Pl3xMap.api().getPlayerRegistry().get(serverPlayer.getUUID());
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    public String toString() {
        return "BukkitWorld{"
                + "name=" + getName()
                + ",seed=" + getSeed()
                + ",spawn=" + getSpawn()
                + "}";
    }
}
