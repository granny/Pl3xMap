package net.pl3x.map.fabric;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.WorldConfig;
import net.pl3x.map.core.world.World;
import net.pl3x.map.fabric.command.FabricCommandManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Pl3xMapFabric extends Pl3xMap implements DedicatedServerModInitializer {
    @SuppressWarnings("deprecation")
    private final RandomSource randomSource = RandomSource.createThreadSafe();

    private MinecraftServer server;
    private ModContainer modContainer;

    private FabricServerAudiences adventure;

    public Pl3xMapFabric() {
        super();

        try {
            Field api = Provider.class.getDeclaredField("api");
            api.setAccessible(true);
            api.set(null, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        init();
    }

    @Override
    public void onInitializeServer() {
        try {
            new FabricCommandManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void enable(@NonNull MinecraftServer server) {
        this.server = server;
        this.adventure = FabricServerAudiences.of(this.server);
        enable();
    }

    @Override
    public void disable() {
        super.disable();
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public @NonNull ModContainer getModContainer() {
        if (this.modContainer == null) {
            this.modContainer = FabricLoader.getInstance().getModContainer("pl3xmap").orElseThrow();
        }
        return this.modContainer;
    }

    @Override
    public @NonNull String getVersion() {
        return getModContainer().getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public int getMaxPlayers() {
        return this.server.getMaxPlayers();
    }

    @Override
    public int getOperatorUserPermissionLevel() {
        return this.server.getOperatorUserPermissionLevel();
    }

    @Override
    public @NonNull AudienceProvider adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return this.adventure;
    }

    @Override
    public @NonNull Path getMainDir() {
        return FabricLoader.getInstance().getGameDir().resolve("config").resolve("pl3xmap");
    }

    @Override
    public void useJar(@NonNull Consumer<@NonNull Path> consumer) {
        consumer.accept(getModContainer().getRootPaths().get(0));
    }

    @Override
    public int getColorForPower(byte power) {
        return RedStoneWireBlock.getColorForPower(power);
    }

    @Override
    public net.pl3x.map.core.world.@Nullable Block getFlower(@NonNull World world, net.pl3x.map.core.world.@NonNull Biome biome, int blockX, int blockY, int blockZ) {
        // https://github.com/Draradech/FlowerMap (CC0-1.0 license)
        Biome nms = world.<ServerLevel>getLevel().registryAccess().registryOrThrow(Registries.BIOME).get(new ResourceLocation(biome.id()));
        if (nms == null) {
            return null;
        }
        List<ConfiguredFeature<?, ?>> flowers = nms.getGenerationSettings().getFlowerFeatures();
        if (flowers.isEmpty()) {
            return null;
        }
        RandomPatchConfiguration config = (RandomPatchConfiguration) flowers.get(0).config();
        SimpleBlockConfiguration flower = (SimpleBlockConfiguration) config.feature().value().feature().value().config();
        Block block = flower.toPlace().getState(this.randomSource, new BlockPos(blockX, blockY, blockZ)).getBlock();
        return getBlockRegistry().get(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    @Override
    protected void loadBlocks() {
        for (Map.Entry<ResourceKey<Block>, Block> entry : this.server.registryAccess().registryOrThrow(Registries.BLOCK).entrySet()) {
            String id = entry.getKey().location().toString();
            int color = entry.getValue().defaultMaterialColor().col;
            getBlockRegistry().register(id, color);
        }
        getBlockRegistry().saveToDisk();
    }

    @Override
    protected void loadWorlds() {
        this.server.getAllLevels().forEach(level -> {
            String name = level.dimension().location().toString();
            WorldConfig worldConfig = new WorldConfig(name);
            if (worldConfig.ENABLED) {
                getWorldRegistry().register(new FabricWorld(level, name, worldConfig));
            }
        });
    }

    @Override
    protected void loadPlayers() {
        this.server.getPlayerList().getPlayers().forEach(player -> {
            UUID uuid = player.getUUID();
            getPlayerRegistry().getOrDefault(uuid, () -> new FabricPlayer(player));
        });
    }
}
