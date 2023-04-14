package net.pl3x.map.core.world;

public class Light {
    private int sky;
    private int block;

    public Light(int sky, int block) {
        this.sky = sky;
        this.block = block;
    }

    public Light set(int sky, int block) {
        this.sky = sky;
        this.block = block;
        return this;
    }

    public int getSky() {
        return sky;
    }

    public int getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return "Light{"
                + "sky=" + getSky()
                + ",block=" + getBlock()
                + "}";
    }
}
