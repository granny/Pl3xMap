package net.pl3x.map.core.renderer.task;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.HashSet;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.world.World;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class RegionFileWatcher extends Thread {
    private final World world;

    public RegionFileWatcher(World world) {
        super(null, null, String.format("Pl3xMap-FileWatcher-%s", world.getName()), 0);
        this.world = world;
    }

    @Override
    public void run() {
        boolean stopped = false;
        Path dir = this.world.getRegionDirectory();

        try (WatchService watcher = dir.getFileSystem().newWatchService()) {

            dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
            Logger.debug("Region file watcher started for " + dir);

            WatchKey key;
            while ((key = watcher.take()) != null) {
                Collection<Path> modifiedFiles = new HashSet<>();

                Logger.debug("Region file watcher got a key!");
                for (WatchEvent<?> event : key.pollEvents()) {
                    Logger.debug("Region file watcher detected event: " + event.kind().name());
                    if (event.kind() != OVERFLOW) {
                        Path file = (Path) event.context();
                        Logger.debug("Detected file change: " + file.getFileName());
                        modifiedFiles.add(dir.resolve(file));
                    }
                }
                key.reset();

                Collection<Point> points = FileUtil.regionPathsToPoints(this.world, modifiedFiles);
                Pl3xMap.api().getRegionProcessor().addRegions(this.world, points);
            }

        } catch (ClosedWatchServiceException | InterruptedException ignore) {
            stopped = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (stopped) {
            Logger.debug("Region file watcher stopped!");
        } else {
            Logger.debug("Region file watcher stopped! Trying to start again..");
            this.world.getRegionFileWatcher().start();
        }
    }
}
