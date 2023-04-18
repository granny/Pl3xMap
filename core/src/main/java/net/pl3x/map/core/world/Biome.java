package net.pl3x.map.core.world;

public record Biome(int index, String id, int color, int foliage, int grass, int water, GrassModifier grassModifier) {
    public static final Biome DEFAULT = new Biome(0, "minecraft:default", 0x000070, 0x73A74E, 0x8EB971, 0x3F76E4, (x, z, def) -> def);

    public int grass(int x, int z) {
        return grassModifier().modify(x, z, grass());
    }

    @FunctionalInterface
    public interface GrassModifier {
        int modify(int x, int z, int def);
    }
}
