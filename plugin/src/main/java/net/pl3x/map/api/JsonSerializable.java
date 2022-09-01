package net.pl3x.map.api;

import com.google.gson.JsonElement;
import net.pl3x.map.api.marker.Point;
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

    default Integer bool(Boolean bool) {
        return bool == null ? null : (bool ? 1 : 0);
    }

    default Integer enumeration(Enum<?> enumeration) {
        return enumeration == null ? null : enumeration.ordinal();
    }

    default JsonElement point(Point point) {
        return point == null ? null : point.toJson();
    }
}
