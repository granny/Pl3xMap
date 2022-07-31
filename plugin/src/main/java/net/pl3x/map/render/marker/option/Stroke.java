package net.pl3x.map.render.marker.option;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Stroke {
    private int weight;
    private int color;

    public Stroke() {
        this(3, 0xFFFF0000);
    }

    public Stroke(int weight, int color) {
        setWeight(weight);
        setColor(color);
    }

    public int getWeight() {
        return this.weight;
    }

    @NotNull
    public Stroke setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public int getColor() {
        return this.color;
    }

    @NotNull
    public Stroke setColor(int color) {
        this.color = color;
        return this;
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
