package net.pl3x.map.core.markers;

import com.google.gson.JsonElement;
import org.checkerframework.checker.nullness.qual.NonNull;

public record Point(int x, int z) implements JsonSerializable {
    public static final Point ZERO = new Point(0, 0);

    @NonNull
    public static Point of(int x, int z) {
        return new Point(x, z);
    }

    @NonNull
    public static Point of(double x, double z) {
        return of((int) Math.floor(x), (int) Math.floor(z));
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
