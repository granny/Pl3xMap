package net.pl3x.map.core.renderer.heightmap;

import java.util.Arrays;
import java.util.Objects;
import net.pl3x.map.core.Keyed;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.world.Region;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Heightmap extends Keyed {
    public final int[] x = new int[16];
    public final int[] z = new int[16];

    public Heightmap(String name) {
        super(name);
    }

    public abstract int getColor(Region region, int blockX, int blockZ);

    public int getColor(int y1, int y2, int heightColor, int step) {
        if (y1 > y2) {
            heightColor -= step;
        } else if (y1 < y2) {
            heightColor += step;
        }
        return Mathf.clamp(0x00, 0x44, heightColor);
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
        Heightmap other = (Heightmap) o;
        return getKey().equals(other.getKey())
                && Arrays.equals(this.x, other.x)
                && Arrays.equals(this.z, other.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), Arrays.hashCode(this.x), Arrays.hashCode(this.z));
    }

    @Override
    public String toString() {
        return "Heightmap{"
                + "key=" + getKey()
                + ",x=" + Arrays.toString(this.x)
                + ",z=" + Arrays.toString(this.z)
                + "}";
    }
}
