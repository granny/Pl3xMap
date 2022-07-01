package net.pl3x.map.render.io;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;
import com.sksamuel.scrimage.nio.ImmutableImageLoader;
import com.sksamuel.scrimage.webp.WebpImageReader;
import com.sksamuel.scrimage.webp.WebpWriter;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.Image;

public class Webp extends IO.Type {
    private static final String EXTENSION = "webp";

    private final ImmutableImageLoader reader;
    private final WebpWriter writer;

    public Webp() {
        // https://developers.google.com/speed/webp/docs/dwebp
        this.reader = ImmutableImage.loader();
        this.reader.detectOrientation(false);
        this.reader.detectMetadata(false);
        this.reader.sourceRegion(new Rectangle(0, 0, Image.SIZE, Image.SIZE));
        this.reader.type(BufferedImage.TYPE_INT_ARGB);
        this.reader.withImageReaders(List.of(new WebpImageReader()));

        // https://developers.google.com/speed/webp/docs/cwebp
        boolean lossless = false; // lossy is faster and smaller file size
        int z = -1; // lossless compression (0-9, default 6)
        int q = 90; // compression factor (0-100, default 75)
        int m = 4; // compression method (0-6, default 4)
        this.writer = new WebpWriter(z, q, m, lossless);
    }

    @Override
    public String extension() {
        return EXTENSION;
    }

    @Override
    public BufferedImage read(Path path) {
        ImmutableImage image = null;
        try {
            image = this.reader.fromPath(path);
        } catch (IOException e) {
            if (!e.getMessage().contains("InterruptedException")) {
                Logger.warn("Could not read tile image: " + path);
                e.printStackTrace();
            }
        }
        return image != null ? image.awt() : null;
    }

    @Override
    public void write(Path path, BufferedImage buffer) {
        try (OutputStream out = Files.newOutputStream(path)) {
            ImmutableImage image = ImmutableImage.wrapAwt(buffer);
            this.writer.write(image, ImageMetadata.empty, out);
            out.flush();
        } catch (IOException e) {
            if (!e.getMessage().contains("InterruptedException")) {
                Logger.warn("Could not write tile image: " + path);
                e.printStackTrace();
            }
        }
    }
}
