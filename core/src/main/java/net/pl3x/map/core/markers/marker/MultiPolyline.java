/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.markers.marker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.core.markers.JsonObjectWrapper;
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a multi-polyline marker.
 */
@SuppressWarnings("UnusedReturnValue")
public class MultiPolyline extends Marker<@NonNull MultiPolyline> {
    private final List<@NonNull Polyline> polylines = new ArrayList<>();

    private MultiPolyline(@NonNull String key) {
        super("multiline", key);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key      identifying key
     * @param polyline polyline to add
     */
    public MultiPolyline(@NonNull String key, @NonNull Polyline polyline) {
        this(key);
        addPolyline(polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     */
    public MultiPolyline(@NonNull String key, @NonNull Polyline @NonNull ... polylines) {
        this(key);
        addPolyline(polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     */
    public MultiPolyline(@NonNull String key, @NonNull Collection<@NonNull Polyline> polylines) {
        this(key);
        addPolyline(polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key      identifying key
     * @param polyline polyline to add
     * @return a new multi-polyline
     */
    public static @NonNull MultiPolyline of(@NonNull String key, @NonNull Polyline polyline) {
        return new MultiPolyline(key, polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static @NonNull MultiPolyline of(@NonNull String key, @NonNull Polyline @NonNull ... polylines) {
        return new MultiPolyline(key, polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static @NonNull MultiPolyline of(@NonNull String key, @NonNull Collection<@NonNull Polyline> polylines) {
        return new MultiPolyline(key, polylines);
    }

    /**
     * Get the list of polylines in this multi-polyline.
     *
     * @return list of polylines
     */
    public @NonNull List<@NonNull Polyline> getPolylines() {
        return this.polylines;
    }

    /**
     * Clear the list of polylines in this multi-polyline.
     *
     * @return this multi-polyline
     */
    public @NonNull MultiPolyline clearPolylines() {
        this.polylines.clear();
        return this;
    }

    /**
     * Add a polyline to this multi-polyline.
     *
     * @param polyline polyline to add
     * @return this multi-polyline
     */
    public @NonNull MultiPolyline addPolyline(@NonNull Polyline polyline) {
        Preconditions.checkNotNull(polyline, "MultiPolyline polyline is null");
        this.polylines.add(polyline);
        return this;
    }

    /**
     * Add polylines to this multi-polyline.
     *
     * @param polylines polylines to add
     * @return this multi-polyline
     */
    public @NonNull MultiPolyline addPolyline(@NonNull Polyline @NonNull ... polylines) {
        Preconditions.checkNotNull(polylines, "MultiPolyline polylines is null");
        for (Polyline polyline : polylines) {
            addPolyline(polyline);
        }
        return this;
    }

    /**
     * Add polylines to this multi-polyline.
     *
     * @param polylines polylines to add
     * @return this multi-polyline
     */
    public @NonNull MultiPolyline addPolyline(@NonNull Collection<@NonNull Polyline> polylines) {
        Preconditions.checkNotNull(polylines, "MultiPolyline polylines is null");
        this.polylines.addAll(polylines);
        return this;
    }

    /**
     * Remove a polyline from this multi-polyline.
     *
     * @param polyline polyline to remove
     * @return this multi-polyline
     */
    public @NonNull MultiPolyline removePolyline(@NonNull Polyline polyline) {
        Preconditions.checkNotNull(polyline, "MultiPolyline polyline is null");
        this.polylines.remove(polyline);
        return this;
    }

    /**
     * Remove polylines from this multi-polyline.
     *
     * @param polylines polylines to remove
     * @return this multi-polyline
     */
    public @NonNull MultiPolyline removePolyline(@NonNull Polyline @NonNull ... polylines) {
        Preconditions.checkNotNull(polylines, "MultiPolyline polylines is null");
        for (Polyline polyline : polylines) {
            removePolyline(polyline);
        }
        return this;
    }

    /**
     * Remove polylines from this multi-polyline.
     *
     * @param polylines polylines to remove
     * @return this multi-polyline
     */
    public @NonNull MultiPolyline removePolyline(@NonNull Collection<@NonNull Polyline> polylines) {
        Preconditions.checkNotNull(polylines, "MultiPolyline polylines is null");
        this.polylines.removeAll(polylines);
        return this;
    }

    @Override
    public @NonNull JsonObject toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("polylines", getPolylines());
        wrapper.addProperty("pane", getPane());
        return wrapper.getJsonObject();
    }

    public static @NonNull MultiPolyline fromJson(@NonNull JsonObject obj) {
        JsonElement el;
        MultiPolyline multiPolyline = MultiPolyline.of(obj.get("key").getAsString());
        if ((el = obj.get("polylines")) != null && !(el instanceof JsonNull)) {
            JsonArray arr = el.getAsJsonArray();
            for (int i = 0; i < arr.size(); i++) {
                multiPolyline.addPolyline(Polyline.fromJson((JsonObject) arr.get(i)));
            }
        }
        if ((el = obj.get("pane")) != null && !(el instanceof JsonNull)) multiPolyline.setPane(el.getAsString());
        return multiPolyline;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        MultiPolyline other = (MultiPolyline) o;
        return getKey().equals(other.getKey())
                && Objects.equals(getPolylines(), other.getPolylines())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getPolylines(), getPane(), getOptions());
    }

    @Override
    public @NonNull String toString() {
        return "MultiPolyline{"
                + "key=" + getKey()
                + ",polylines=" + getPolylines()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
