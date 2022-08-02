package net.pl3x.map.render.marker.option;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.render.marker.data.JsonSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stroke properties of a marker.
 */
public class Stroke implements JsonSerializable {
    private int weight;
    private int color;

    /**
     * Create a stroke rule with default options.
     */
    public Stroke() {
        this(3, 0xFFFF0000);
    }

    /**
     * Create a stroke rule.
     *
     * @param weight stroke weight
     * @param color  argb color
     */
    public Stroke(int weight, int color) {
        setWeight(weight);
        setColor(color);
    }

    /**
     * Get the weight of this stroke rule.
     *
     * @return stroke weight
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * Set the weight for this stroke rule.
     *
     * @param weight new stroke weight
     * @return this stroke rule
     */
    @NotNull
    public Stroke setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    /**
     * Get the color of this stroke rule.
     *
     * @return argb color
     */
    public int getColor() {
        return this.color;
    }

    /**
     * Set the color of this stroke rule.
     *
     * @param color argb color
     * @return this stroke rule
     */
    @NotNull
    public Stroke setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        json.add(getWeight());
        json.add(getColor());
        return json;
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
        Stroke other = (Stroke) o;
        return getWeight() == other.getWeight()
                && getColor() == other.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWeight(), getColor());
    }
}
