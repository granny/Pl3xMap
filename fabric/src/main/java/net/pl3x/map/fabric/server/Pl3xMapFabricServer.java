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
package net.pl3x.map.fabric.server;

import com.google.common.collect.ImmutableList;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.event.server.ServerLoadedEvent;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.player.PlayerListener;
import net.pl3x.map.core.world.World;
import net.pl3x.map.fabric.server.command.FabricCommandManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Pl3xMapFabricServer extends Pl3xMap implements DedicatedServerModInitializer {
    @SuppressWarnings("deprecation")
    private final RandomSource randomSource = RandomSource.createThreadSafe();
    private final PlayerListener playerListener = new PlayerListener();

    private MinecraftServer server;
    private ModContainer modContainer;
    private MinecraftServerAudiences adventure;
    private Map<Biome, List<ConfiguredFeature<?,?>>> biomeFeatureCache = new LinkedHashMap<>();
    ArrayList<ResourceKey<ConfiguredFeature<?, ?>>> canSpawnFromBonemealList = new ArrayList<>(10);

    private boolean firstTick = true;

    private FabricNetwork network;

    public Pl3xMapFabricServer() {
        super(false);
    }

    @Override
    public void onInitializeServer() {
        try {
            new FabricCommandManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (this.firstTick) {
                Pl3xMap.api().getEventRegistry().callEvent(new ServerLoadedEvent());
                this.firstTick = false;

                canSpawnFromBonemealList.add(VegetationFeatures.FLOWER_DEFAULT);
                canSpawnFromBonemealList.add(VegetationFeatures.FLOWER_FLOWER_FOREST);
                canSpawnFromBonemealList.add(VegetationFeatures.FLOWER_SWAMP);
                canSpawnFromBonemealList.add(VegetationFeatures.FLOWER_PLAIN);
                canSpawnFromBonemealList.add(VegetationFeatures.FLOWER_MEADOW);
                canSpawnFromBonemealList.add(VegetationFeatures.FLOWER_CHERRY);
                canSpawnFromBonemealList.add(VegetationFeatures.WILDFLOWER);
                canSpawnFromBonemealList.add(VegetationFeatures.FLOWER_PALE_GARDEN);
            }
            getScheduler().tick();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            Player fabricPlayer = getPlayerRegistry().getOrDefault(player.getUUID(), () -> new FabricPlayer(player));
            this.playerListener.onJoin(fabricPlayer);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            Player fabricPlayer = getPlayerRegistry().unregister(player.getUUID());
            if (fabricPlayer != null) {
                this.playerListener.onQuit(fabricPlayer);
            }
        });

        ServerLevelEvents.LOAD.register((server, level) -> {
            if (isEnabled()) {
                String name = level.dimension().identifier().toString();
                Pl3xMap.api().getWorldRegistry().getOrDefault(name, () -> new FabricWorld(level, name));
            }
        });

        ServerLevelEvents.UNLOAD.register((server, level) -> {
            String name = level.dimension().identifier().toString();
            Pl3xMap.api().getWorldRegistry().unregister(name);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.adventure = MinecraftServerAudiences.of(this.server);

            enable();

            this.network = new FabricNetwork(this);
            this.network.register();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (this.network != null) {
                this.network.unregister();
                this.network = null;
            }

            disable();

            if (this.adventure != null) {
                this.adventure.close();
                this.adventure = null;
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                getPlayerRegistry().getOrDefault(newPlayer.getUUID(), () -> new FabricPlayer(newPlayer)).setPlayer(newPlayer)
        );
    }

    public ModContainer getModContainer() {
        if (this.modContainer == null) {
            this.modContainer = FabricLoader.getInstance().getModContainer("pl3xmap").orElseThrow();
        }
        return this.modContainer;
    }

    @Override
    public String getPlatform() {
        return this.server.getServerModName().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getVersion() {
        return getModContainer().getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public int getMaxPlayers() {
        return this.server.getMaxPlayers();
    }

    @Override
    public boolean getOnlineMode() {
        return this.server.usesAuthentication();
    }

    @Override
    public String getServerVersion() {
        return SharedConstants.getCurrentVersion().name();
    }

    @Override
    public AudienceProvider adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return this.adventure;
    }

    @Override
    public Path getMainDir() {
        return FabricLoader.getInstance().getGameDir().resolve("config").resolve("pl3xmap");
    }

    @Override
    public Path getJarPath() {
        return getModContainer().getOrigin().getPaths().getFirst();
    }

    @Override
    public int getColorForPower(byte power) {
        return RedStoneWireBlock.getColorForPower(power);
    }

    private List<ConfiguredFeature<?,?>> getBoneMealFeatures(Biome biome) {
        // https://github.com/Draradech/FlowerMap (CC0-1.0 license)
        // the biomes created from the builtin registry are missing tags
        // with the new can_spawn_from_bonemeal tag for vegetation features we can no longer just call getFlowerFeatures (now called getBonemealFeatures)
        // iterate through the feature stream and collect matching features manually
        if (!biomeFeatureCache.containsKey(biome)) {
            biomeFeatureCache.put(biome,
                    biome.getGenerationSettings().features().stream()
                            .flatMap(HolderSet::stream)
                            .flatMap(feature -> ((PlacedFeature) feature.value()).getFeatures())
                            .filter(feature -> {
                                Optional<ResourceKey<ConfiguredFeature<?, ?>>> key = feature.unwrapKey();
                                return key.isPresent() && canSpawnFromBonemealList.contains(key.get());
                            })
                            .map(Holder::value)
                            .collect(ImmutableList.toImmutableList()));
        }
        return biomeFeatureCache.get(biome);
    }

    @Override
    public net.pl3x.map.core.world.@Nullable Block getFlower(World world, net.pl3x.map.core.world.Biome biome, int blockX, int blockY, int blockZ) {
        // https://github.com/Draradech/FlowerMap (CC0-1.0 license)
        Biome nms = world.<ServerLevel>getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getValue(Identifier.parse(biome.getKey()));
        if (nms == null) {
            return null;
        }
        List<ConfiguredFeature<?, ?>> flowers = this.getBoneMealFeatures(nms);
        if (flowers.isEmpty()) {
            return null;
        }
        SimpleBlockConfiguration flowerMap = (SimpleBlockConfiguration) flowers.getFirst().config();
        Block block = flowerMap.toPlace().getState(world.getLevel(), this.randomSource, new BlockPos(blockX, blockY, blockZ)).getBlock();
        return getBlockRegistry().get(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    @Override
    protected void loadBlocks() {
        Set<Map.Entry<ResourceKey<Block>, Block>> entries = this.server.registryAccess().lookupOrThrow(Registries.BLOCK).entrySet();
        for (Map.Entry<ResourceKey<Block>, Block> entry : entries) {
            String id = entry.getKey().identifier().toString();
            int color = entry.getValue().defaultMapColor().col;
            getBlockRegistry().register(id, color);
        }
        getBlockRegistry().saveToDisk();
    }

    @Override
    protected void loadWorlds() {
        this.server.getAllLevels().forEach(level -> {
            String name = level.dimension().identifier().toString();
            Pl3xMap.api().getWorldRegistry().getOrDefault(name, () -> new FabricWorld(level, name));
        });
    }

    @Override
    protected void loadPlayers() {
        this.server.getPlayerList().getPlayers().forEach(player -> {
            UUID uuid = player.getUUID();
            getPlayerRegistry().getOrDefault(uuid, () -> new FabricPlayer(player));
        });
    }

    @Override
    public World cloneWorld(World world) {
        return new FabricWorld(world.getLevel(), world.getName());
    }

    public @Nullable MinecraftServer getServer() {
        return this.server;
    }
}
