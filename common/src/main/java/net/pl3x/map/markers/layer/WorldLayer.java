package net.pl3x.map.markers.layer;

import java.util.function.Supplier;
import net.pl3x.map.Key;
import net.pl3x.map.markers.option.Options;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a layer for worlds.
 */
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
    public WorldLayer(@NotNull Key key, @NotNull World world, @NotNull Supplier<String> labelSupplier) {
        super(key, labelSupplier);
        this.world = world;
    }

    @NotNull
    public World getWorld() {
        return this.world;
    }

    @Nullable
    public Options getOptions() {
        return this.options;
    }

    @NotNull
    public WorldLayer setOptions(@Nullable Options options) {
        this.options = options;
        return this;
    }
}
