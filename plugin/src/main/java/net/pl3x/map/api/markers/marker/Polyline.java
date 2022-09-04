package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import net.pl3x.map.api.JsonArrayWrapper;
import net.pl3x.map.api.markers.Line;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a polyline marker.
 */
public class Polyline extends Marker {
    private final Collection<Line> lines = new ArrayList<>();

    private Polyline() {
        super("line");
    }

    public Polyline(@NotNull Line line) {
        this();
        addLine(line);
    }

    public Polyline(@NotNull Line @NotNull ... lines) {
        this();
        addLine(lines);
    }

    public Polyline(@NotNull Collection<Line> lines) {
        this();
        addLine(lines);
    }

    public static Polyline of(@NotNull Line line) {
        return new Polyline(line);
    }

    public static Polyline of(@NotNull Line @NotNull ... lines) {
        return new Polyline(lines);
    }

    public static Polyline of(@NotNull Collection<Line> lines) {
        return new Polyline(lines);
    }

    @NotNull
    public Collection<Line> getLines() {
        return this.lines;
    }

    @NotNull
    public Polyline clearLines() {
        this.lines.clear();
        return this;
    }

    @NotNull
    public Polyline addLine(@NotNull Line line) {
        Preconditions.checkNotNull(line, "Polyline line is null");
        this.lines.add(line);
        return this;
    }

    @NotNull
    public Polyline addLine(@NotNull Line @NotNull ... lines) {
        Preconditions.checkNotNull(lines, "Polyline lines is null");
        addLine(Arrays.asList(lines));
        return this;
    }

    @NotNull
    public Polyline addLine(@NotNull Collection<Line> lines) {
        Preconditions.checkNotNull(lines, "Polyline lines is null");
        this.lines.addAll(lines);
        return this;
    }

    @NotNull
    public Polyline removeLine(@NotNull Line line) {
        Preconditions.checkNotNull(line, "Polyline line is null");
        this.lines.remove(line);
        return this;
    }

    @NotNull
    public Polyline removeLine(@NotNull Line @NotNull ... lines) {
        Preconditions.checkNotNull(lines, "Polyline lines is null");
        removeLine(Arrays.asList(lines));
        return this;
    }

    @NotNull
    public Polyline removeLine(@NotNull Collection<Line> lines) {
        Preconditions.checkNotNull(lines, "Polyline lines is null");
        this.lines.removeAll(lines);
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        getLines().forEach(wrapper::add);
        return wrapper.getJsonArray();
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
        Polyline other = (Polyline) o;
        return Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions());
    }

    @Override
    public String toString() {
        return "Polyline{lines=" + getLines() + ",options=" + getOptions() + "}";
    }
}
