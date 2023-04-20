package net.pl3x.map.core.markers;

import com.google.gson.JsonElement;
import org.checkerframework.checker.nullness.qual.NonNull;

public record Vector(double x, double z) implements JsonSerializable {
    @NonNull
    public static Vector of(int x, int z) {
        return new Vector(x, z);
    }

    @NonNull
    public static Vector of(double x, double z) {
        return new Vector(x, z);
    }

    @Override
    @NonNull
    public JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("x", x());
        wrapper.addProperty("z", z());
        return wrapper.getJsonObject();
    }
}
