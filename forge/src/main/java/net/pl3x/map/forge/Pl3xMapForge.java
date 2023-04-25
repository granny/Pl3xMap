package net.pl3x.map.forge;

import cloud.commandframework.forge.CloudForgeEntrypoint;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.forge.ForgeServerAudiences;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.WorldConfig;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.player.PlayerListener;
import net.pl3x.map.core.player.PlayerRegistry;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.world.World;
import net.pl3x.map.forge.command.ForgeCommandManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Mod("pl3xmap")
public class Pl3xMapForge extends Pl3xMap {
    private final PlayerListener playerListener = new PlayerListener();

    @SuppressWarnings("deprecation")
    private final RandomSource randomSource = RandomSource.createThreadSafe();

    private MinecraftServer server;
    private IModInfo modInfo;

    private ForgeServerAudiences adventure;

    public Pl3xMapForge() {
        super();

        try {
            Field api = Provider.class.getDeclaredField("api");
            api.setAccessible(true);
            api.set(null, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //noinspection InstantiationOfUtilityClass
        new CloudForgeEntrypoint();

        init();

        MinecraftForge.EVENT_BUS.register(this);

        try {
            new ForgeCommandManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.@NonNull ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            getScheduler().tick();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.@NonNull PlayerLoggedInEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        UUID uuid = event.getEntity().getUUID();
        Player forgePlayer = registry.getOrDefault(uuid, () -> new ForgePlayer((ServerPlayer) event.getEntity()));
        this.playerListener.onJoin(forgePlayer);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.@NonNull PlayerLoggedOutEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        String uuid = event.getEntity().getUUID().toString();
        Player forgePlayer = registry.unregister(uuid);
        if (forgePlayer != null) {
            this.playerListener.onQuit(forgePlayer);
        }
    }

    @SubscribeEvent
    public void onServerStarted(@NonNull ServerStartedEvent event) {
        this.server = event.getServer();
        this.adventure = new ForgeServerAudiences(this.server);
        enable();
    }

    @SubscribeEvent
    public void onServerStopping(@NonNull ServerStoppingEvent event) {
        disable();
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public @NonNull IModInfo getModInfo() {
        if (this.modInfo == null) {
            this.modInfo = ModList.get().getModContainerById("pl3xmap").orElseThrow().getModInfo();
        }
        return this.modInfo;
    }

    @Override
    public @NonNull String getVersion() {
        return getModInfo().getVersion().toString();
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
        return FMLPaths.GAMEDIR.get().resolve("config").resolve("pl3xmap");
    }

    @Override
    public void useJar(@NonNull Consumer<@NonNull Path> consumer) {
        try {
            FileUtil.openJar(getModInfo().getOwningFile().getFile().getFilePath(), fs -> consumer.accept(fs.getPath("/")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
        return key == null ? null : getBlockRegistry().get(key.toString());
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
                getWorldRegistry().register(new ForgeWorld(level, name, worldConfig));
            }
        });
    }

    @Override
    protected void loadPlayers() {
        this.server.getPlayerList().getPlayers().forEach(player -> {
            UUID uuid = player.getUUID();
            getPlayerRegistry().getOrDefault(uuid, () -> new ForgePlayer(player));
        });
    }
}
