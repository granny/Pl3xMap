package net.pl3x.map.addon;

import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.render.task.Renderers;
import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.render.task.ScanTask;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.Mathf;
import org.bukkit.plugin.java.JavaPlugin;

public class InhabitedRenderer extends JavaPlugin {
    @Override
    public void onEnable() {
        // register our custom renderer with Pl3xMap
        Renderers.INSTANCE.register("inhabited", InhabitedScanner.class);
    }

    @Override
    public void onDisable() {
        // register our custom renderer with Pl3xMap
        Renderers.INSTANCE.unregister("inhabited");
    }

    public static final class InhabitedScanner extends Renderer {
        public InhabitedScanner(String name, ScanTask scanTask) {
            super(name, scanTask);
        }

        @Override
        public void scanData(RegionCoordinate region, ScanData.Data scanData) {
            Renderer basic = getScanTask().getRenderer("basic");
            for (ScanData data : scanData.values()) {
                int pixelX = data.getCoordinate().getBlockX() & Image.SIZE - 1;
                int pixelZ = data.getCoordinate().getBlockZ() & Image.SIZE - 1;

                // get basic pixel color
                int pixelColor;
                if (basic != null) {
                    pixelColor = basic.getImageHolder().getImage().getPixel(pixelX, pixelZ);
                } else {
                    pixelColor = basicPixelColor(data, scanData);
                }

                // we hsb lerp between blue and red with ratio being the
                // percent inhabited time is of the maxed out inhabited time
                float ratio = Mathf.inverseLerp(0F, 3600000, data.getChunk().getInhabitedTime());
                int inhabitedRGB = Colors.lerpHSB(0xFF0000FF, 0xFFFF0000, ratio, false);

                // set the color, mixing our heatmap on top
                // set a low enough alpha, so we can see the basic map underneath
                pixelColor = Colors.mix(pixelColor, Colors.setAlpha(0x88, inhabitedRGB));

                getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
            }
        }
    }
}
