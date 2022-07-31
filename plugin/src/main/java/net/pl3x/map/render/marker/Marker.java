package net.pl3x.map.render.marker;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.pl3x.map.render.marker.option.MarkerOptions;
import org.jetbrains.annotations.NotNull;

/**
 * Base marker.
 */
public abstract class Marker {
    private static final Gson GSON = new GsonBuilder()
            //.setPrettyPrinting()
            .disableHtmlEscaping()
            //.serializeNulls()
            .setLenient()
            .create();

    private MarkerOptions options = new MarkerOptions();

    /**
     * Get the options of this marker.
     *
     * @return marker options
     */
    @NotNull
    public MarkerOptions getOptions() {
        return this.options;
    }

    /**
     * Set new options for this marker.
     *
     * @param options new options
     * @return this marker
     */
    @NotNull
    public Marker setOptions(@NotNull MarkerOptions options) {
        Preconditions.checkNotNull(options);
        this.options = options;
        return this;
    }

    /**
     * Serialize this marker into a json string.
     *
     * @return serialized json string
     */
    public String serialize() {
        return GSON.toJson(this);
    }
}
