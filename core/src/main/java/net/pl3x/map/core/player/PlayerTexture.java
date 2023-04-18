package net.pl3x.map.core.player;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.util.FileUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Task to fetch and save a players skin.
 */
public class PlayerTexture extends Thread {
    private static final Path SKINS_2D_DIR = FileUtil.getWebDir().resolve("images/skins/2D");
    private static final Path SKINS_3D_DIR = FileUtil.getWebDir().resolve("images/skins/3D");
    private static final URL STEVE_SKIN;

    static {
        try {
            Files.createDirectories(SKINS_2D_DIR);
            Files.createDirectories(SKINS_3D_DIR);
            STEVE_SKIN = FileUtil.getWebDir().resolve("images/skins/steve.png").toUri().toURL();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final UUID uuid;
    private final URL url;

    public PlayerTexture(Player player) {
        this.uuid = player.getUUID();
        URL url = player.getSkin();
        if (url == null) {
            url = STEVE_SKIN;
        }
        this.url = url;
    }

    @Override
    public void run() {
        if (this.url == null) {
            return;
        }
        try {
            BufferedImage textureSource = ImageIO.read(this.url);

            BufferedImage head2D = get2DHead(textureSource);
            ImageIO.write(head2D, "png", SKINS_2D_DIR.resolve(this.uuid + ".png").toFile());

            BufferedImage head3D;
            try {
                head3D = get3DHead(textureSource);
            } catch (NoClassDefFoundError e) {
                // happens in headless environments (missing awt's GraphicsEnvironment)
                // just draw a 2d head and put it in the 3d directory for now
                head3D = head2D;
            }
            ImageIO.write(head3D, "png", SKINS_3D_DIR.resolve(this.uuid + ".png").toFile());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @NonNull
    private static BufferedImage get2DHead(@NonNull BufferedImage source) {
        return getPart(source, 8, 8);
    }

    @NonNull
    private static BufferedImage get3DHead(@NonNull BufferedImage source) {
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

    private static BufferedImage getPart(@NonNull BufferedImage source, int x, int y) {
        BufferedImage head = source.getSubimage(x, y, 8, 8);
        BufferedImage helm = source.getSubimage(x + 32, y, 8, 8);
        BufferedImage result = new BufferedImage(32, 32, source.getType());
        for (int x1 = 0; x1 < 32; x1++) {
            for (int z1 = 0; z1 < 32; z1++) {
                int argb = Colors.blend(
                        helm.getRGB(x1 / 4, z1 / 4),
                        head.getRGB(x1 / 4, z1 / 4)
                );
                result.setRGB(x1, z1, argb);
            }
        }
        return result;
    }

    private static BufferedImage flip(@NonNull BufferedImage src) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-src.getWidth(), 0));
        return transform(src, at, AffineTransformOp.TYPE_BILINEAR);
    }

    private static BufferedImage rotate(@NonNull BufferedImage src, double angle) {
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

    private static BufferedImage scale(@NonNull BufferedImage src, double scaleX, double scaleY) {
        AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
        return transform(src, at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }

    private static BufferedImage shear(@NonNull BufferedImage src) {
        AffineTransform at = AffineTransform.getShearInstance(0.577375, 0);
        return transform(src, at, AffineTransformOp.TYPE_BILINEAR);
    }

    private static BufferedImage transform(@NonNull BufferedImage src, @NonNull AffineTransform at, int type) {
        return new AffineTransformOp(at, type).filter(src, null);
    }

    public static class Json {
        public long timestamp;
        public String profileId;
        public String profileName;
        public boolean signatureRequired;
        public Map<String, Map<String, String>> textures;

        /*
{
  "timestamp" : 1681841628334,
  "profileId" : "0b54d4f18ce946b3a7234ffdeeae3d7d",
  "profileName" : "BillyGalbreath",
  "signatureRequired" : true,
  "textures" : {
    "SKIN" : {
      "url" : "http://textures.minecraft.net/texture/53b9eb275fbbc2cfb0c2fb8c0598c3a905e77f82d1f9ba00f7bd83196958d9b2"
    },
    "CAPE" : {
      "url" : "http://textures.minecraft.net/texture/2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933"
    }
  }
}
         */
    }
}
