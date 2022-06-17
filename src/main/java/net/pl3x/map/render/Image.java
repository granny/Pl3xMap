package net.pl3x.map.render;

import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.util.Colors;
import net.pl3x.map.world.MapWorld;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Image {
    public static final int SIZE = 512;

    private final Path path;
    private final int[] pixels = new int[Image.SIZE * Image.SIZE];

    public Image(Path path) {
        this.path = path;
    }

    public Path path() {
        return this.path;
    }

    public void getPixels(BufferedImage buffer) {
        for (int x = 0; x < buffer.getWidth(); x++) {
            for (int z = 0; z < buffer.getHeight(); z++) {
                buffer.setRGB(x, z, Colors.rgb2bgr(getPixel(x, z)));
            }
        }
    }

    public int getPixel(int x, int z) {
        return this.pixels[z * Image.SIZE + x];
    }

    public void setPixels(BufferedImage buffer) {
        for (int x = 0; x < Image.SIZE; x++) {
            for (int z = 0; z < Image.SIZE; z++) {
                setPixel(x, z, buffer.getRGB(x, z));
            }
        }
    }

    public void setPixel(int x, int z, int color) {
        this.pixels[z * Image.SIZE + x] = Colors.rgb2bgr(color);
    }

    public void save() {
        // create directories if they don't exist
        if (!Files.exists(path().getParent())) {
            try {
                Files.createDirectories(path().getParent());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // write file to tmp on disk
        // this helps prevent corrupt pngs
        ImageWriter writer = null;
        Exception error = null;
        Path tmpPath = path().resolveSibling(path().getFileName() + ".tmp");
        try (ImageOutputStream out = ImageIO.createImageOutputStream(Files.newOutputStream(tmpPath))) {
            BufferedImage buffer = new BufferedImage(Image.SIZE, Image.SIZE, BufferedImage.TYPE_INT_ARGB);
            ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(buffer);
            writer = ImageIO.getImageWriters(type, "png").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                // low quality == high compression
                param.setCompressionQuality(0.0f);
            }
            getPixels(buffer);
            writer.setOutput(out);
            writer.write(null, new IIOImage(buffer, null, null), param);
        } catch (IOException e) {
            // store error so we can return early after finally
            error = e;
        } finally {
            if (writer != null) {
                writer.dispose();
            }
        }

        // error out if we couldn't save tmp file
        if (error != null) {
            Logger.warn("Could not save tile image: " + path());
            error.printStackTrace();
            return;
        }

        // move tmp file into proper place
        try {
            Files.move(tmpPath, path(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e1) {
            try {
                Files.move(tmpPath, path(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static class Set {
        public static final String PATH = "%s/%d_%d.png";

        private final int x, z;
        private final Image blocks, biomes, heights, fluids;

        public Set(AbstractRender render, RegionCoordinate region) {
            this(render, region, 0);
        }

        public Set(AbstractRender render, RegionCoordinate region, int zoom) {
            this.x = region.getRegionX();
            this.z = region.getRegionZ();

            Path worldDir = MapWorld.TILES_DIR.resolve(render.getWorld().getName());
            Path tileDir = worldDir.resolve(Integer.toString(zoom));

            this.blocks = new Image(tileDir.resolve(String.format(PATH, "blocks", this.x, this.z)));
            this.biomes = new Image(tileDir.resolve(String.format(PATH, "biomes", this.x, this.z)));
            this.heights = new Image(tileDir.resolve(String.format(PATH, "heights", this.x, this.z)));
            this.fluids = new Image(tileDir.resolve(String.format(PATH, "fluids", this.x, this.z)));
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
            Logger.debug("Saving images for region " + this.x + ", " + this.z);
            this.blocks.save();
            this.biomes.save();
            this.heights.save();
            this.fluids.save();
        }
    }
}
