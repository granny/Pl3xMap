package net.pl3x.map.render.marker.data;

public class Point {
    public static final Point ZERO = new Point(0, 0);

    private int x;
    private int z;

    public Point(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public Point setX(int x) {
        this.x = x;
        return this;
    }

    public int getZ() {
        return this.z;
    }

    public Point setZ(int z) {
        this.z = z;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Point other = (Point) o;
        return this.x == other.x && this.z == other.z;
    }

    @Override
    public int hashCode() {
        int prime = 1543;
        int result = 1;
        result = prime * result + getX();
        result = prime * result + getZ();
        return result;
    }
}
