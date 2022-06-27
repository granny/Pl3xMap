package net.pl3x.map.render;

import net.minecraft.util.Mth;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.render.io.IO;
import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.util.Colors;
import net.pl3x.map.world.MapWorld;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Image {
    private static final Map<String, ReadWriteLock> FILE_LOCKS = new ConcurrentHashMap<>();

    public static final int SIZE = 512;
    public static final String DIR_PATH = "%d/%s/";
    public static final String FILE_PATH = "%d_%d.%s";

    private final MapWorld mapWorld;
    private final String type;
    private final int regionX;
    private final int regionZ;

    private final int[] pixels = new int[SIZE * SIZE];
    private final Path worldDir;

    private final IO.Type io;

    public Image(MapWorld mapWorld, String type, int regionX, int regionZ) {
        this.mapWorld = mapWorld;
        this.type = type;
        this.regionX = regionX;
        this.regionZ = regionZ;

        this.worldDir = MapWorld.TILES_DIR.resolve(mapWorld.getName());

        this.io = IO.get(Config.WEB_TILE_FORMAT);
    }

    private int getIndex(int x, int z) {
        return z * SIZE + x;
    }

    public int getPixel(int x, int z) {
        return this.pixels[getIndex(x, z)];
    }

    public void setPixel(int x, int z, int color) {
        this.pixels[getIndex(x, z)] = color;
    }

    public void saveToDisk() {
        for (int zoom = 0; zoom <= this.mapWorld.getConfig().ZOOM_MAX_OUT; zoom++) {
            Path dirPath = this.worldDir.resolve(String.format(DIR_PATH, zoom, this.type));

            // create directories if they don't exist
            createDirs(dirPath);

            // calculate correct sizes for this zoom level
            int step = (int) Math.pow(2, zoom);
            int size = SIZE / step;

            Path filePath = dirPath.resolve(String.format(FILE_PATH,
                    Mth.floor((double) this.regionX / step),
                    Mth.floor((double) this.regionZ / step),
                    this.io.extension()));

            ReadWriteLock lock = FILE_LOCKS.computeIfAbsent(filePath.toString(), k -> new ReentrantReadWriteLock(true));
            lock.writeLock().lock();

            // read existing image from disk, except original zoom
            BufferedImage buffer = makeBuffer(filePath, zoom);

            // write new pixels
            writePixels(buffer, size, step);

            // finally, save buffer to disk
            this.io.write(filePath, buffer);

            lock.writeLock().unlock();

        }
    }

    private void createDirs(Path dirPath) {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private BufferedImage makeBuffer(Path path, int zoom) {
        BufferedImage buffer = null;
        try {
            if (zoom != 0 && Files.exists(path) && Files.size(path) > 0) {
                buffer = this.io.read(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // if not file was loaded, create a new image
        if (buffer == null) {
            buffer = this.io.createBuffer();
        }

        return buffer;
    }

    private void writePixels(BufferedImage buffer, int size, int step) {
        int baseX = (this.regionX * size) & (SIZE - 1);
        int baseZ = (this.regionZ * size) & (SIZE - 1);
        for (int x = 0; x < SIZE; x += step) {
            for (int z = 0; z < SIZE; z += step) {
                int rgb = getPixel(x, z);
                if (rgb == 0) {
                    // skipping 0 prevents overwrite existing
                    // parts of the buffer on higher zooms
                    continue;
                }
                if (step > 1) {
                    // merge pixel colors instead of skipping them
                    rgb = downSample(x, z, rgb, step);
                }
                buffer.setRGB(baseX + (x / step), baseZ + (z / step), rgb);
            }
        }
    }

    private int downSample(int x, int z, int rgb, int step) {
        int a = 0, r = 0, g = 0, b = 0, count = 0;
        for (int i = 0; i < step; i++) {
            for (int j = 0; j < step; j++) {
                if (i != 0 && j != 0) {
                    rgb = getPixel(x + i, z + j);
                }
                a += Colors.alpha(rgb);
                r += Colors.red(rgb);
                g += Colors.green(rgb);
                b += Colors.blue(rgb);
                count++;
            }
        }
        return Colors.argb(a / count, r / count, g / count, b / count);
    }

    public static class Set {
        private final Image blocks, biomes, heights, fluids;

        public Set(MapWorld mapWorld, RegionCoordinate region) {
            int regionX = region.getRegionX();
            int regionZ = region.getRegionZ();
            this.blocks = new Image(mapWorld, "blocks", regionX, regionZ);
            this.biomes = new Image(mapWorld, "biomes", regionX, regionZ);
            this.heights = new Image(mapWorld, "heights", regionX, regionZ);
            this.fluids = new Image(mapWorld, "fluids", regionX, regionZ);
        }

        public Image getBlocks() {
            return this.blocks;
        }

        public Image getBiomes() {
            return this.biomes;
        }

        public Image getHeights() {
            return this.heights;
        }

        public Image getFluids() {
            return this.fluids;
        }

        public void save() {
            this.blocks.saveToDisk();
            this.biomes.saveToDisk();
            this.heights.saveToDisk();
            this.fluids.saveToDisk();
        }
    }
}
