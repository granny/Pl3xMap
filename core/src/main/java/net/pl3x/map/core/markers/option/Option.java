package net.pl3x.map.core.markers.option;

import net.pl3x.map.core.markers.JsonSerializable;

/**
 * Represents marker option properties
 */
public abstract class Option<T extends Option<T>> implements JsonSerializable {
    /**
     * Check whether all options are defaults (all are null)
     *
     * @return true if all options are null
     */
    public abstract boolean isDefault();
}
