package net.pl3x.map.core.markers.option;

import net.pl3x.map.core.markers.JsonSerializable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents marker option properties
 */
@SuppressWarnings("unused")
public abstract class Option<@NonNull T extends @NonNull Option<@NonNull T>> implements JsonSerializable {
    /**
     * Check whether all options are defaults (all are null)
     *
     * @return true if all options are null
     */
    public abstract boolean isDefault();
}
