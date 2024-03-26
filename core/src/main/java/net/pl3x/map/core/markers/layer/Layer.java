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
package net.pl3x.map.core.markers.layer;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import net.pl3x.map.core.Keyed;
import net.pl3x.map.core.markers.JsonObjectWrapper;
import net.pl3x.map.core.markers.JsonSerializable;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.util.Preconditions;
import net.pl3x.map.core.util.TickUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a layer of markers and other metadata.
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class Layer extends Keyed implements JsonSerializable {
    private Supplier<@NotNull String> labelSupplier;
    private int updateInterval = TickUtil.toTicks(15);
    private boolean showControls = true;
    private boolean defaultHidden = false;
    private int priority = 99;
    private Integer zIndex = 99;
    private String pane;
    private String css;
    private boolean liveUpdate = false;

    /**
     * Create a layer.
     *
     * @param key key for layer
     */
    public Layer(@NotNull String key, @NotNull Supplier<@NotNull String> labelSupplier) {
        super(key);
        this.labelSupplier = labelSupplier;
    }

    /**
     * Get the label of this layer, shown in the control box.
     *
     * @return layer label
     */
    public @NotNull String getLabel() {
        return this.labelSupplier.get();
    }

    /**
     * Set the label of this layer, shown in the control box.
     *
     * @param label new label
     * @return this layer
     */
    public @NotNull Layer setLabel(@NotNull String label) {
        Preconditions.checkNotNull(label, "Layer label is null");
        this.labelSupplier = () -> label;
        return this;
    }

    /**
     * Set the label supplier of this layer, shown in the control box.
     *
     * @param labelSupplier new label supplier
     * @return this layer
     */
    public @NotNull Layer setLabel(@NotNull Supplier<@NotNull String> labelSupplier) {
        Preconditions.checkNotNull(labelSupplier, "Layer label supplier is null");
        this.labelSupplier = labelSupplier;
        return this;
    }

    /**
     * Get this layer's update interval (in seconds).
     *
     * @return update interval
     */
    public int getUpdateInterval() {
        return this.getUpdateInterval(false);
    }

    /**
     * Get this layer's update interval (in seconds or in ticks).
     *
     * @param ticks set to true to get update interval as ticks instead of seconds
     * @return update interval
     */
    public int getUpdateInterval(boolean ticks) {
        return ticks ? this.updateInterval : (int) TickUtil.toSeconds(this.updateInterval);
    }

    /**
     * Set this layer's update interval (in seconds).
     *
     * @param updateInterval new update interval
     * @return this layer
     */
    public @NotNull Layer setUpdateInterval(int updateInterval) {
        this.setUpdateInterval(updateInterval, false);
        return this;
    }

    /**
     * Set this layer's update interval (in seconds or in ticks).
     *
     * @param updateInterval new update interval
     * @param ticks set to true to treat the interval value as ticks instead of seconds
     * @return this layer
     */
    public @NotNull Layer setUpdateInterval(int updateInterval, boolean ticks) {
        this.updateInterval = ticks ? updateInterval : TickUtil.toTicks(updateInterval);
        return this;
    }

    /**
     * Get whether to show this layer in the control box.
     *
     * @return true if showing controls
     */
    public boolean shouldShowControls() {
        return this.showControls;
    }

    /**
     * Set whether to show this layer in the control box.
     *
     * @param showControls true to show
     * @return this layer
     */
    public @NotNull Layer setShowControls(boolean showControls) {
        this.showControls = showControls;
        return this;
    }

    /**
     * Get if this layer is hidden by default in the control box.
     *
     * @return true if hidden by default
     */
    public boolean isDefaultHidden() {
        return this.defaultHidden;
    }

    /**
     * Set if this layer is hidden by default in the control box.
     *
     * @param defaultHidden true to hide by default
     * @return this layer
     */
    public @NotNull Layer setDefaultHidden(boolean defaultHidden) {
        this.defaultHidden = defaultHidden;
        return this;
    }

    /**
     * Get the indexed order for this layer in the control box.
     * <p>
     * Falls back to alphanumeric ordering based on label if there are order conflicts.
     *
     * @return layer priority
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * Set the indexed order for this layer in the control box.
     * <p>
     * Falls back to alphanumeric ordering based on label if there are order conflicts.
     *
     * @param priority new priority
     * @return this layer
     */
    public @NotNull Layer setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Get the z-index for this layer. Used in determining what layers are visually on top of other layers.
     * <p>
     * Falls back to alphanumeric ordering based on name if there are order conflicts.
     * <p>
     * Defaults to '<code>{@link #getPriority()}</code>' if null.
     *
     * @return layer z-index
     */
    public @Nullable Integer getZIndex() {
        return zIndex == null ? this.getPriority() : zIndex;
    }

    /**
     * Set the z-index for this layer. Used in determining what layers are visually on top of other layers.
     * <p>
     * Falls back to alphanumeric ordering based on name if there are order conflicts.
     * <p>
     * Defaults to '<code>{@link #getPriority()}</code>' if null.
     *
     * @param zIndex new z-index
     * @return this layer
     */
    public @NotNull Layer setZIndex(@Nullable Integer zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    /**
     * Get the map pane for this layer.
     *
     * @return map pane
     */
    public @Nullable String getPane() {
        return this.pane;
    }

    /**
     * Set the map pane for this layer.
     * <p>
     * If the pane does not exist, it will be created the first time it is used.
     *
     * @param pane new map pane
     * @return this layer
     */
    public @NotNull Layer setPane(@Nullable String pane) {
        this.pane = pane;
        return this;
    }

    /**
     * Get the custom CSS to add for this layer.
     *
     * @return custom CSS
     */
    public @Nullable String getCss() {
        return this.css;
    }

    /**
     * Set the custom CSS to add for this layer.
     *
     * @param css new custom CSS
     * @return this layer
     */
    public @NotNull Layer setCss(@Nullable String css) {
        this.css = css;
        return this;
    }

    /**
     * Get if this layer gets pushed through sse.
     *
     * @return true if being sent through sse
     */
    public @Nullable boolean isLiveUpdate() {
        return this.liveUpdate;
    }

    /**
     * Set whether to push this layer through sse.
     *
     * @param liveUpdate true to push this layer through sse.
     * @return this layer
     */
    public @NotNull Layer setLiveUpdate(@Nullable boolean liveUpdate) {
        this.liveUpdate = liveUpdate;
        return this;
    }

    /**
     * Get the markers to display in this Layer.
     *
     * @return markers to display
     */
    public abstract @NotNull Collection<@NotNull Marker<?>> getMarkers();

    public @NotNull JsonObject toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("label", getLabel());
        wrapper.addProperty("updateInterval", getUpdateInterval());
        wrapper.addProperty("showControls", shouldShowControls());
        wrapper.addProperty("defaultHidden", isDefaultHidden());
        wrapper.addProperty("priority", getPriority());
        wrapper.addProperty("zIndex", getZIndex());
        wrapper.addProperty("pane", getPane());
        wrapper.addProperty("css", getCss());
        return wrapper.getJsonObject();
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
        Layer other = (Layer) o;
        return getKey().equals(other.getKey())
                && getLabel().equals(other.getLabel())
                && getUpdateInterval() == other.getUpdateInterval()
                && shouldShowControls() == other.shouldShowControls()
                && isDefaultHidden() == other.isDefaultHidden()
                && getPriority() == other.getPriority()
                && Objects.equals(getZIndex(), other.getZIndex())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getCss(), other.getCss());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getLabel(), getUpdateInterval(), shouldShowControls(), isDefaultHidden(), getPriority(), getZIndex(), getPane(), getCss());
    }

    @Override
    public @NotNull String toString() {
        return "Layer{"
                + "key=" + getKey()
                + ",label=" + getLabel()
                + ",updateInterval=" + getUpdateInterval()
                + ",showControls=" + shouldShowControls()
                + ",defaultHidden=" + isDefaultHidden()
                + ",priority=" + getPriority()
                + ",zIndex=" + getZIndex()
                + ",pane=" + getPane()
                + ",css=" + getCss()
                + "}";
    }
}
