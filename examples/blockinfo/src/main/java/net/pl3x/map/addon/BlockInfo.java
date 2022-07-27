package net.pl3x.map.addon;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.render.task.Renderers;
import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.render.task.ScanTask;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.world.MapWorld;
import net.pl3x.map.world.WorldManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockInfo extends JavaPlugin {
    private static final Gson GSON = new Gson().newBuilder()
            //.setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    private static final Palette<Block> BLOCK_PALETTE = new Palette<>();
    private static final Map<MapWorld, Palette<Biome>> BIOME_PALETTES = new HashMap<>();

    @Override
    public void onEnable() {
        // register our custom renderer with Pl3xMap
        Renderers.INSTANCE.register("blockinfo", BlockInfoScanner.class);

        // create block palette
        Registry.BLOCK.forEach(block -> {
            String name = name("block", Registry.BLOCK.getKey(block));
            BLOCK_PALETTE.add(block, name);
        });

        // create biome palette
        Bukkit.getWorlds().forEach(world -> {
            MapWorld mapWorld = WorldManager.INSTANCE.getMapWorld(world);
            if (mapWorld == null) {
                return;
            }

            Palette<Biome> palette = new Palette<>();
            BIOME_PALETTES.put(mapWorld, palette);

            ServerLevel level = ((CraftWorld) world).getHandle();
            Registry<Biome> registry = BiomeColors.getBiomeRegistry(level);
            registry.forEach(biome -> {
                String name = name("biome", registry.getKey(biome));
                palette.add(biome, name);
            });

            try {
                saveGzip(GSON.toJson(palette.getMap()), mapWorld.getWorldTilesDir().resolve("biomes.gz"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // save global block palette
        try {
            saveGzip(GSON.toJson(BLOCK_PALETTE.getMap()), MapWorld.TILES_DIR.resolve("blocks.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // start bstats metrics
        new Metrics(this, 15922);
    }

    @Override
    public void onDisable() {
        // register our custom renderer with Pl3xMap
        Renderers.INSTANCE.unregister("blockinfo");
    }

    private String name(String type, ResourceLocation key) {
        return Language.getInstance().getOrDefault(Util.makeDescriptionId(type, key));
    }

    private static void saveGzip(String json, Path file) throws IOException {
        try (
                OutputStream fileOut = Files.newOutputStream(mkDirs(file));
                GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
                Writer writer = new OutputStreamWriter(gzipOut)
        ) {
            writer.write(json);
        }
    }

    private static Path mkDirs(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
        return path;
    }

    public static final class BlockInfoScanner extends Renderer {
        private ByteBuffer byteBuffer;

        public BlockInfoScanner(String name, ScanTask scanTask) {
            super(name, scanTask);
        }

        @Override
        public void allocateData() {
            this.byteBuffer = ByteBuffer.allocate(Image.SIZE * Image.SIZE * 4 + 20);
        }

        @Override
        public void saveData() {
            Path tilesDir = getScanTask().getWorld().getWorldTilesDir();
            // TODO - make work for higher zoom levels?
            Path dir = tilesDir.resolve(String.format(Image.DIR_PATH, 0, getName()));
            String filename = String.format(Image.FILE_PATH, getRegion().getRegionX(), getRegion().getRegionZ(), "pl3xmap.gz");
            try (
                    OutputStream fileOut = Files.newOutputStream(mkDirs(dir.resolve(filename)));
                    GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
            ) {
                gzipOut.write(this.byteBuffer.array());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void scanData(RegionCoordinate region, ScanData.Data scanData) {
            int minY = getWorld().getLevel().getMinBuildHeight();

            Palette<Biome> biomePalette = BIOME_PALETTES.get(getWorld());

            this.byteBuffer.clear();

            this.byteBuffer.put(0, toBytes(0x706C3378)); // pl3x
            this.byteBuffer.put(4, toBytes(0x6D617001)); // map1
            this.byteBuffer.put(8, toBytes(region.getRegionX()));
            this.byteBuffer.put(12, toBytes(region.getRegionZ()));
            this.byteBuffer.put(16, toBytes(minY));

            for (ScanData data : scanData.values()) {
                boolean fluid = data.getFluidPos() != null;

                Block block = (fluid ? data.getFluidState() : data.getBlockState()).getBlock();
                Biome biome = fluid ? data.getFluidBiome() : data.getBlockBiome();
                BlockPos pos = fluid ? data.getFluidPos() : data.getBlockPos();

                int blockIndex = BLOCK_PALETTE.get(block).getIndex();
                int biomeIndex = biomePalette.get(biome).getIndex();
                int yPos = pos.getY() - minY; // ensure bottom starts at 0

                // 11111111111111111111111111111111 - 32 bits - (4294967295)
                // 1111111111                       - 10 bits - block (1023)
                //           1111111111             - 10 bits - biome (1023)
                //                     111111111111 - 12 bits - yPos  (4095)
                int packed = ((blockIndex & 1023) << 22) | ((biomeIndex & 1023) << 12) | (yPos & 4095);
                int index = (pos.getZ() & Image.SIZE - 1) * Image.SIZE + (pos.getX() & Image.SIZE - 1);
                this.byteBuffer.put(20 + index * 4, toBytes(packed));
            }
        }

        private byte[] toBytes(int packed) {
            byte[] bytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                bytes[i] = (byte) (packed >>> (i * 8));
            }
            return bytes;
        }
    }
}
