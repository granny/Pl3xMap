package net.pl3x.map.core.registry;

import javax.management.openmbean.KeyAlreadyExistsException;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class WorldRegistry extends Registry<@NonNull World> {
    public @NonNull World register(@NonNull World world) {
        if (has(world.getName())) {
            throw new KeyAlreadyExistsException("World already registered: " + world.getName());
        }
        return register(world.getName(), world);
    }

    @Override
    public @Nullable World unregister(@NonNull String id) {
        World world = this.entries.remove(id);
        if (world != null) {
            world.setPaused(true);
            world.getMarkerTask().cancel();
            world.getRegionFileWatcher().stop();
            world.cleanup();
        }
        return world;
    }
}
