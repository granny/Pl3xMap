package net.pl3x.map.heightmap;

import java.util.Arrays;
import java.util.Objects;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import net.pl3x.map.coordinate.BlockCoordinate;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.util.Mathf;
import org.jetbrains.annotations.Nullable;

public abstract class Heightmap extends Keyed {
    public int[] x = new int[16];
    public int[] z = new int[16];

    public Heightmap(String name) {
        super(Key.of(name));
    }

    public abstract int getColor(BlockCoordinate coordinate, ScanData data, ScanData.Data scanData);

    public int getColor(ScanData data1, ScanData data2, int heightColor, int step) {
        if (data2 != null) {
            if (data1.getBlockPos().getY() > data2.getBlockPos().getY()) {
                heightColor -= step;
            } else if (data1.getBlockPos().getY() < data2.getBlockPos().getY()) {
                heightColor += step;
            }
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
        return getKey() == other.getKey()
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
