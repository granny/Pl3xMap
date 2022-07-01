package net.pl3x.map.render.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.util.Mth;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.render.image.io.IO;
import net.pl3x.map.render.renderer.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.util.Colors;
import net.pl3x.map.world.MapWorld;

public class Image {
    private static final Map<Path, ReadWriteLock> FILE_LOCKS = new ConcurrentHashMap<>();

    public static final int SIZE = 512;
    public static final String DIR_PATH = "%d/";
    public static final String FILE_PATH = "%d_%d.%s";

    private final MapWorld mapWorld;
    private final int regionX;
    private final int regionZ;

    private final int[] pixels = new int[SIZE * SIZE];
    private final Path worldDir;

    private final IO.Type io;

    public Image(MapWorld mapWorld, int regionX, int regionZ) {
        this.mapWorld = mapWorld;
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
            Path dirPath = this.worldDir.resolve(String.format(DIR_PATH, zoom));

            // create directories if they don't exist
            createDirs(dirPath);

            // calculate correct sizes for this zoom level
            int step = (int) Math.pow(2, zoom);
            int size = SIZE / step;

            Path filePath = dirPath.resolve(String.format(FILE_PATH,
                    Mth.floor((double) this.regionX / step),
                    Mth.floor((double) this.regionZ / step),
                    this.io.extension()));

            ReadWriteLock lock = FILE_LOCKS.computeIfAbsent(filePath, k -> new ReentrantReadWriteLock(true));
            lock.writeLock().lock();

            // read existing image from disk
            BufferedImage buffer = getBuffer(filePath);

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

    private BufferedImage getBuffer(Path path) {
        BufferedImage buffer = null;
        try {
            if (Files.exists(path) && Files.size(path) > 0) {
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
                    // parts of the buffer of existing images
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

    public static class Holder {
        private final MapWorld mapWorld;
        private final RegionCoordinate region;
        private final Image image;

        public Holder(MapWorld mapWorld, RegionCoordinate region) {
            this.mapWorld = mapWorld;
            int regionX = region.getRegionX();
            int regionZ = region.getRegionZ();
            this.region = new RegionCoordinate(regionX, regionZ);
            this.image = new Image(mapWorld, regionX, regionZ);
        }

        public Image getImage() {
            return this.image;
        }

        public void save() {
            getImage().saveToDisk();

            // mark this region as done
            this.mapWorld.setScannedRegion(this.region);
        }
    }
}
