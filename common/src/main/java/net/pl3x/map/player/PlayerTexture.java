package net.pl3x.map.player;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public class PlayerTexture implements Runnable {
    private static final Path SKINS_2D_DIR = World.WEB_DIR.resolve("images/skins/2D");
    private static final Path SKINS_3D_DIR = World.WEB_DIR.resolve("images/skins/3D");

    static {
        try {
            Files.createDirectories(SKINS_2D_DIR);
            Files.createDirectories(SKINS_3D_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final UUID uuid;
    private final URL url;

    public PlayerTexture(Player player) {
        this.uuid = player.getUUID();
        this.url = player.getSkin();
    }

    @Override
    public void run() {
        if (this.url == null) {
            return;
        }
        try {
            ImageIO.write(get2DHead(ImageIO.read(this.url)), "png", SKINS_2D_DIR.resolve(this.uuid + ".png").toFile());
            ImageIO.write(get3DHead(ImageIO.read(this.url)), "png", SKINS_3D_DIR.resolve(this.uuid + ".png").toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private static BufferedImage get2DHead(@NotNull BufferedImage source) {
        return getPart(source, 8, 8);
    }

    @NotNull
    private static BufferedImage get3DHead(@NotNull BufferedImage source) {
        // get parts
        BufferedImage left = getPart(source, 8, 8);
        BufferedImage right = getPart(source, 16, 8);
        BufferedImage top = getPart(source, 8, 0);

        // scale parts up to 512
        left = scale(left, 16, 16 * 0.86062D);
        right = scale(right, 16, 16 * 0.86062D);
        top = scale(top, 16, 16 * 0.86062D);

        // shear parts
        left = shear(left);
        right = flip(shear(flip(right)));
        top = flip(shear(flip(top)));

        // rotate parts
        left = rotate(left, 0.523599);
        right = rotate(right, -0.523599);
        top = rotate(top, 0.523599);

        // combine parts (884x765)
        BufferedImage result = new BufferedImage(1024, 1024, left.getType());
        Graphics2D g2d = result.createGraphics();
        g2d.translate(-151, 257);
        g2d.drawImage(left, 0, 0, null);
        g2d.translate(442, 0);
        g2d.drawImage(right, 0, 0, null);
        g2d.translate(-222, -382);
        g2d.drawImage(top, 0, 0, null);
        g2d.dispose();

        // scale result down to 128
        result = scale(result, 0.125, 0.125);

        return result;
    }

    private static BufferedImage getPart(@NotNull BufferedImage source, int x, int y) {
        BufferedImage head = source.getSubimage(x, y, 8, 8);
        BufferedImage helm = source.getSubimage(x + 32, y, 8, 8);
        BufferedImage result = new BufferedImage(32, 32, source.getType());
        for (int x1 = 0; x1 < 32; x1++) {
            for (int z1 = 0; z1 < 32; z1++) {
                int rgb = helm.getRGB(x1 / 4, z1 / 4);
                if (rgb == 0) {
                    rgb = head.getRGB(x1 / 4, z1 / 4);
                }
                result.setRGB(x1, z1, rgb);
            }
        }
        return result;
    }

    private static BufferedImage flip(BufferedImage src) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-src.getWidth(), 0));
        return transform(src, at, AffineTransformOp.TYPE_BILINEAR);
    }

    private static BufferedImage rotate(BufferedImage src, double angle) {
        int w = src.getWidth();
        int h = src.getHeight();
        double sin = Math.abs(Math.sin(angle));
        double cos = Math.abs(Math.cos(angle));
        int newWidth = (int) (w * cos + h * sin);
        int newHeight = (int) (h * cos + w * sin);

        BufferedImage dest = new BufferedImage(newWidth, newHeight, src.getType());
        Graphics2D g2d = dest.createGraphics();
        g2d.translate((newWidth - w) / 2, (newHeight - h) / 2);
        g2d.rotate(angle, w / 2D, h / 2D);
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();

        return dest;
    }

    private static BufferedImage scale(BufferedImage src, double scaleX, double scaleY) {
        AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
        return transform(src, at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }

    private static BufferedImage shear(BufferedImage src) {
        AffineTransform at = AffineTransform.getShearInstance(0.577375, 0);
        return transform(src, at, AffineTransformOp.TYPE_BILINEAR);
    }

    private static BufferedImage transform(BufferedImage src, AffineTransform at, int type) {
        return new AffineTransformOp(at, type).filter(src, null);
    }
}
