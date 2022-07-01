package net.pl3x.map.addon;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.renderer.Renderer;
import net.pl3x.map.render.renderer.iterator.coordinate.Coordinate;
import net.pl3x.map.render.renderer.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.scanner.Scanner;
import net.pl3x.map.render.scanner.Scanners;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.Mathf;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InhabitedRenderer extends JavaPlugin {
    // the most difficult part of this example is determining what is the
    // highest inhabited time in the entire world so we know what our "hottest"
    // value on the heatmap actually is. the best we can do is compare each
    // chunk as we see them. i'm open to suggestions for a better method.
    private static final Map<UUID, Long> highestInhabitedTime = new HashMap<>();

    @Override
    public void onEnable() {
        // register our custom renderer with Pl3xMap
        Scanners.INSTANCE.register("inhabited", InhabitedScanner.class);

        // check chunks already loaded before plugin started
        Bukkit.getWorlds().forEach(world -> {
            UUID uuid = world.getUID();
            for (Chunk chunk : world.getLoadedChunks()) {
                long inhabited = chunk.getInhabitedTime();
                checkHighestTime(uuid, inhabited);
            }
        });

        // check inhabited times on chunk load
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onChunkLoad(ChunkLoadEvent event) {
                UUID uuid = event.getWorld().getUID();
                long inhabited = event.getChunk().getInhabitedTime();
                checkHighestTime(uuid, inhabited);
            }
        }, this);
    }

    // try our best to know the world's highest inhabited time
    private static void checkHighestTime(UUID uuid, long inhabited) {
        long highestKnown = highestInhabitedTime.computeIfAbsent(uuid, k -> inhabited);
        if (inhabited > highestKnown) {
            highestInhabitedTime.put(uuid, inhabited);
        }
    }

    public static class InhabitedScanner extends Scanner {
        private final Scanner basic;

        public InhabitedScanner(Renderer render, RegionCoordinate region, Collection<Long> chunks) {
            super(render, region, chunks);

            // setup a "fake" basic renderer that we can overlay with our heatmap
            this.basic = Scanners.INSTANCE.createScanner("basic", render, region, chunks);

            // we need to give it a image holder since it's not passing through a scan job
            this.basic.setImageHolder(new Image.Holder(getWorld(), getRegion()));
        }

        @Override
        public void scanChunk(int chunkX, int chunkZ) {
            // using Pl3xMap's chunk helper here really speeds up chunk loading
            // you can use Bukkit API, but it will slow down renders drastically
            ChunkAccess chunk = getChunkHelper().getChunk(getWorld().getLevel(), chunkX, chunkZ);
            if (chunk == null) {
                // chunk doesnt exist
                return;
            }

            // do a basic render scan
            this.basic.scanChunk(chunkX, chunkZ);

            // world coordinates of northwest block of chunk
            int blockX = Coordinate.chunkToBlock(chunkX);
            int blockZ = Coordinate.chunkToBlock(chunkZ);

            // get the color of this chunk to use
            // set a low enoug alpha so we can see the basic map underneath
            int rgb = Colors.setAlpha(0x88, getInhabitedColor(chunk));

            // put the color on the tile image
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    // convert our known coordinates into pixel coordinates
                    int pixelX = (blockX + x) & Image.SIZE - 1;
                    int pixelZ = (blockZ + z) & Image.SIZE - 1;

                    // get the color from the basic scan of this pixel
                    int basicRGB = this.basic.getImageHolder().getImage().getPixel(pixelX, pixelZ);

                    // set the color at this pixel, mixing our heatmap on top
                    getImageHolder().getImage().setPixel(pixelX, pixelZ, Colors.mix(basicRGB, rgb));
                }
            }
        }

        private int getInhabitedColor(ChunkAccess chunk) {
            UUID uuid = getWorld().getUUID();
            long inhabited = chunk.getInhabitedTime();

            // check highest time because we loaded this
            // chunk ourselves with chunk helper
            checkHighestTime(uuid, inhabited);

            // get the current highest known inhabited time
            long highestKnown = highestInhabitedTime.get(uuid);

            // we hsb lerp between blue and red with ratio being the
            // percent inhabited time is of the highest known inhabited time
            float ratio = Mathf.inverseLerp(0F, highestKnown, inhabited);
            return Colors.lerpHSB(0xFF0000FF, 0xFFFF0000, ratio, false);
        }
    }
}
