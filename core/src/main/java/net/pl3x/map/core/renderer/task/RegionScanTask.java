package net.pl3x.map.core.renderer.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.registry.RendererRegistry;
import net.pl3x.map.core.renderer.Renderer;
import net.pl3x.map.core.world.Region;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RegionScanTask implements Runnable {
    private final World world;
    private final Point regionPos;

    private final List<Renderer> renderers = new ArrayList<>();

    public RegionScanTask(@NonNull World world, @NonNull Point regionPos) {
        this.world = world;
        this.regionPos = regionPos;

        RendererRegistry registry = Pl3xMap.api().getRendererRegistry();
        List<Renderer.Builder> rendererBuilders = new ArrayList<>(this.world.getRenderers().values());

        String blockInfo = this.world.getConfig().UI_BLOCKINFO;
        if (blockInfo != null && !blockInfo.isEmpty()) {
            rendererBuilders.add(registry.get(RendererRegistry.BLOCKINFO));
        }

        rendererBuilders.forEach(builder -> {
            Renderer renderer = registry.createRenderer(this.world, builder);
            if (renderer != null) {
                this.renderers.add(renderer);
            }
        });
    }

    @NonNull
    public World getWorld() {
        return this.world;
    }

    public void cleanup() {
        this.renderers.clear();
    }

    @Override
    public void run() {
        try {
            Logger.debug("[" + this.world.getName() + "] Scanning " + regionPos + " -- " + Thread.currentThread().getName());
            allocateImages();
            scanRegion(loadRegion());
            saveImages();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void allocateImages() {
        for (Renderer renderer : this.renderers) {
            renderer.allocateData(this.regionPos);
        }
    }

    @NonNull
    private Region loadRegion() {
        Region region = this.world.getRegion(null, this.regionPos.x(), this.regionPos.z());
        try {
            region.loadChunks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return region;
    }

    private void scanRegion(@NonNull Region region) {
        for (Renderer renderer : this.renderers) {
            renderer.scanData(region);
        }
    }

    private void saveImages() {
        for (Renderer renderer : this.renderers) {
            renderer.saveData(this.regionPos);
        }
    }
}
