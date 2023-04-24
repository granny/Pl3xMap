package net.pl3x.map.core.markers.layer;

import java.util.function.Supplier;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a layer for worlds.
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class WorldLayer extends SimpleLayer {
    private final World world;

    private Options options;

    /**
     * Create a new spawn layer.
     *
     * @param key           key for layer
     * @param world         world
     * @param labelSupplier label
     */
    public WorldLayer(@NonNull String key, @NonNull World world, @NonNull Supplier<@NonNull String> labelSupplier) {
        super(key, labelSupplier);
        this.world = world;
    }

    public @NonNull World getWorld() {
        return this.world;
    }

    public @Nullable Options getOptions() {
        return this.options;
    }

    public @NonNull WorldLayer setOptions(@Nullable Options options) {
        this.options = options;
        return this;
    }
}
