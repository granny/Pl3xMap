package net.pl3x.map.api.marker;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.pl3x.map.api.JsonSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a map marker.
 */
public abstract class Marker implements JsonSerializable {
    public static final Gson GSON = new GsonBuilder()
            //.setPrettyPrinting()
            //.disableHtmlEscaping()
            //.serializeNulls()
            .registerTypeHierarchyAdapter(Marker.class, new Adapter())
            .setLenient()
            .create();

    private final String type;
    private Options options = null;

    /**
     * Create a new marker.
     *
     * @param type type of marker
     */
    public Marker(@NotNull String type) {
        Preconditions.checkNotNull(type, "Marker type is null");
        this.type = type;
    }

    /**
     * Get the type identifier of this marker.
     * <p>
     * Used in the serialized json for the frontend.
     *
     * @return marker type
     */
    @NotNull
    public String getType() {
        return this.type;
    }

    /**
     * Get the options of this marker.
     * <p>
     * Null options represents "default" values. See wiki about defaults.
     *
     * @return marker options
     */
    @Nullable
    public Options getOptions() {
        return this.options;
    }

    /**
     * Set new options for this marker.
     * <p>
     * Null options represents "default" values. See wiki about defaults.
     *
     * @param options new options or null
     * @return this marker
     */
    @NotNull
    public Marker setOptions(@Nullable Options options) {
        this.options = options;
        return this;
    }

    /**
     * Serialize this marker into a json string.
     *
     * @return serialized json string
     */
    @NotNull
    public String serialize() {
        return GSON.toJson(this);
    }

    private static class Adapter implements JsonSerializer<Marker> {
        @Override
        @NotNull
        public JsonElement serialize(@NotNull Marker marker, @NotNull Type type, @NotNull JsonSerializationContext context) {
            JsonArray json = new JsonArray();
            json.add(marker.getType());
            json.add(marker.toJson());
            if (marker.getOptions() != null) {
                json.add(marker.getOptions().toJson());
            }
            return json;
        }
    }
}
