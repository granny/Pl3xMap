package net.pl3x.map.palette;

public class Palette {
    private final int index;
    private final String name;

    public Palette(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }
}
