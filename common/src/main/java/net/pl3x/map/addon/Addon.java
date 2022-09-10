package net.pl3x.map.addon;

import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Pl3xMap addon.
 */
public abstract class Addon extends Keyed {
    AddonInfo info;
    boolean enabled;

    public Addon() {
        super(Key.NONE);
    }

    @Override
    @NotNull
    public Key getKey() {
        return getInfo().getKey();
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

    /**
     * Get this addon's name.
     *
     * @return name of addon
     */
    @NotNull
    public String getName() {
        return getInfo().getName();
    }

    /**
     * Get this addon's version.
     *
     * @return version of addon
     */
    @NotNull
    public String getVersion() {
        return getInfo().getVersion();
    }

    /**
     * Get this addon's information.
     *
     * @return information of addon
     */
    @NotNull
    public AddonInfo getInfo() {
        return this.info;
    }

    /**
     * Check if addon is enabled.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }
}
