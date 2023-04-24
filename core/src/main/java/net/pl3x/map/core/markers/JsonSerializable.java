package net.pl3x.map.core.markers;

import com.google.gson.JsonElement;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an object that can be serialized into a json element.
 */
public interface JsonSerializable {
    /**
     * Jsonify this object.
     *
     * @return object as json element
     */
    @NonNull JsonElement toJson();
}
