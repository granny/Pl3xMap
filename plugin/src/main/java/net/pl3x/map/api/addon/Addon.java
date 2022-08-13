package net.pl3x.map.api.addon;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a Pl3xMap addon.
 */
public abstract class Addon {
    private AddonDescription description = null;

    public Addon() {
    }

    /**
     * Called when this addon is enabled.
     */
    public void onEnable() {
    }

    /**
     * Called when this addon is disabled.
     */
    public void onDisable() {
    }

    @NotNull
    public String getName() {
        return getDescription().getName();
    }

    @NotNull
    public String getVersion() {
        return getDescription().getVersion();
    }

    @NotNull
    public AddonDescription getDescription() {
        return this.description;
    }
}
