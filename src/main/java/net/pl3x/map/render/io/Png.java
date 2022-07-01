package net.pl3x.map.render.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import net.pl3x.map.logger.Logger;

public class Png extends IO.Type {
    private static final String EXTENSION = "png";

    @Override
    public String extension() {
        return EXTENSION;
    }

    @Override
    public BufferedImage read(Path path) {
        BufferedImage buffer = null;
        ImageReader reader = null;
        try (ImageInputStream in = ImageIO.createImageInputStream(Files.newInputStream(path))) {
            reader = ImageIO.getImageReadersBySuffix(extension()).next();
            reader.setInput(in, false, true);
            buffer = reader.read(0);
            in.flush();
        } catch (IOException e) {
            if (!e.getMessage().contains("InterruptedException")) {
                Logger.warn("Could not read tile image: " + path);
                e.printStackTrace();
            }
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
        return buffer;
    }

    @Override
    public void write(Path path, BufferedImage buffer) {
        ImageWriter writer = null;
        try (ImageOutputStream out = ImageIO.createImageOutputStream(Files.newOutputStream(path))) {
            writer = ImageIO.getImageWritersBySuffix(extension()).next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                if (param.getCompressionType() == null) {
                    param.setCompressionType(param.getCompressionTypes()[0]);
                }
                param.setCompressionQuality(0.0F);
            }
            writer.setOutput(out);
            writer.write(buffer);
            out.flush();
        } catch (IOException e) {
            if (!e.getMessage().contains("InterruptedException")) {
                Logger.warn("Could not write tile image: " + path);
                e.printStackTrace();
            }
        } finally {
            if (writer != null) {
                writer.dispose();
            }
        }
    }
}
