package net.pl3x.map.core.world;

import net.pl3x.map.core.util.Colors;

public record Biome(String id, int color, int foliage, int grass, int water, GrassModifier grassModifier) {
    public static final Biome DEFAULT = new Biome("minecraft:default", 0x000070, 0x73A74E, 0x8EB971, 0x3F76E4, (x, z, def) -> def);

    public int grass(int x, int z) {
        return grassModifier().modify(x, z, grass());
    }

    @Override
    public String toString() {
        return "Biome{"
                + "id=" + id()
                + ", color=" + Colors.toHex8(color())
                + ", foliage=" + Colors.toHex8(foliage())
                + ", grass=" + Colors.toHex8(grass())
                + ", water=" + Colors.toHex8(water())
                + ", grassModifier=" + grassModifier()
                + "}";
    }

    @FunctionalInterface
    public interface GrassModifier {
        int modify(int x, int z, int def);
    }
}
