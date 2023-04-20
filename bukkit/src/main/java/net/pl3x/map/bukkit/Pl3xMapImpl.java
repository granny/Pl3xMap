package net.pl3x.map.bukkit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.world.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Pl3xMapImpl extends Pl3xMap {
    private final JavaPlugin plugin;

    @SuppressWarnings("deprecation")
    private final RandomSource randomSource = RandomSource.createThreadSafe();

    public Pl3xMapImpl(@NonNull JavaPlugin plugin) {
        super();

        this.plugin = plugin;

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
    public void useJar(@NonNull Consumer<Path> consumer) {
        // https://github.com/PEXPlugins/PermissionsEx/blob/master/api/src/main/java/ca/stellardrift/permissionsex/util/TranslatableProvider.java#L150-L158 (Apache-2.0 license)
        URL sourceUrl = Pl3xMap.class.getProtectionDomain().getCodeSource().getLocation();
        // Some class loaders give the full url to the class, some give the URL to its jar.
        // We want the containing jar, so we will unwrap jar-schema code sources.
        if (sourceUrl.getProtocol().equals("jar")) {
            int exclamationIdx = sourceUrl.getPath().lastIndexOf('!');
            if (exclamationIdx != -1) {
                try {
                    sourceUrl = new URL(sourceUrl.getPath().substring(0, exclamationIdx));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            FileUtil.openJar(Paths.get(sourceUrl.toURI()), fs -> consumer.accept(fs.getPath("/")));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NonNull
    public Path getMainDir() {
        return this.plugin.getDataFolder().toPath();
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
    public void loadBlocks() {
        for (Map.Entry<ResourceKey<Block>, Block> entry : MinecraftServer.getServer().registryAccess().registryOrThrow(Registries.BLOCK).entrySet()) {
            String id = entry.getKey().location().toString();
            if (getBlockRegistry().has(id)) {
                continue;
            }
            int color = entry.getValue().defaultMaterialColor().col;
            if (id.startsWith("minecraft:")) {
                Logger.warn("Registering unknown block vanilla " + id + ": " + Colors.toHex(color));
            }
            getBlockRegistry().register(id, color);
        }
        getBlockRegistry().saveToDisk();
    }

    @Override
    public void loadWorlds() {
        Bukkit.getWorlds().forEach(world -> {
            ServerLevel level = ((CraftWorld) world).getHandle();
            WorldConfig worldConfig = new WorldConfig(world.getName());
            getWorldRegistry().register(new BukkitWorld(level, world.getName(), worldConfig));
        });
    }

    @Override
    public void loadPlayers() {
        Bukkit.getOnlinePlayers().forEach(player ->
                getPlayerRegistry().register(player.getUniqueId().toString(), new BukkitPlayer(player)));
    }

    @Override
    public int getMaxPlayers() {
        return MinecraftServer.getServer().getMaxPlayers();
    }
}
