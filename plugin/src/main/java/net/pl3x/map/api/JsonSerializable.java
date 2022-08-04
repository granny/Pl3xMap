package net.pl3x.map.api;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that can be serialized into a json element.
 */
public interface JsonSerializable {
    /**
     * Jsonify this object.
     *
     * @return object as json element
     */
    @NotNull
    JsonElement toJson();
}
