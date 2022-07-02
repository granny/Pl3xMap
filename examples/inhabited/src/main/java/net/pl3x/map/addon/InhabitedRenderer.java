package net.pl3x.map.addon;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.render.renderer.Renderer;
import net.pl3x.map.render.renderer.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.scanner.Scanners;
import net.pl3x.map.render.scanner.builtin.BasicScanner;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.Mathf;
import org.bukkit.plugin.java.JavaPlugin;

public class InhabitedRenderer extends JavaPlugin {
    @Override
    public void onEnable() {
        // register our custom renderer with Pl3xMap
        Scanners.INSTANCE.register("inhabited", InhabitedScanner.class);
    }

    public static class InhabitedScanner extends BasicScanner {
        public InhabitedScanner(Renderer render, RegionCoordinate region, Collection<Long> chunks) {
            super(render, region, chunks);
        }

        @Override
        public int scanBlock(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int[] lastY, int x) {
            // get regular pixel color from basic render
            int pixelColor = super.scanBlock(chunk, pos, lastY, x);

            // we hsb lerp between blue and red with ratio being the
            // percent inhabited time is of the maxed out inhabited time
            float ratio = Mathf.inverseLerp(0F, 3600000, chunk.getInhabitedTime());
            int inhabitedRGB = Colors.lerpHSB(0xFF0000FF, 0xFFFF0000, ratio, false);

            // set the color, mixing our heatmap on top
            // set a low enough alpha, so we can see the basic map underneath
            return Colors.mix(pixelColor, Colors.setAlpha(0x88, inhabitedRGB));
        }
    }
}
