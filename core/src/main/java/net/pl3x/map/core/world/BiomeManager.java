package net.pl3x.map.core.world;

import java.util.Iterator;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BiomeManager implements Iterable<@NonNull Biome> {
    private final World world;
    private final long hashedSeed;

    public BiomeManager(@NonNull World world) {
        this.world = world;
        //this.hashedSeed = Hashing.sha256().hashLong(world.getSeed()).asLong();
        this.hashedSeed = world.hashSeed(world.getSeed());
    }

    public @NonNull Biome getBiome(@NonNull Region region, int x, int y, int z) {
        int i = x - 2;
        int j = y - 2;
        int k = z - 2;
        int l = i >> 2;
        int m = j >> 2;
        int n = k >> 2;
        double d = (double) (i & 3) / 4.0D;
        double e = (double) (j & 3) / 4.0D;
        double f = (double) (k & 3) / 4.0D;
        int o = 0;
        double g = Double.POSITIVE_INFINITY;
        for (int p = 0; p < 8; ++p) {
            boolean bl = (p & 4) == 0;
            boolean bl2 = (p & 2) == 0;
            boolean bl3 = (p & 1) == 0;
            int q = bl ? l : l + 1;
            int r = bl2 ? m : m + 1;
            int s = bl3 ? n : n + 1;
            double h = bl ? d : d - 1.0D;
            double t = bl2 ? e : e - 1.0D;
            double u = bl3 ? f : f - 1.0D;
            double v = getFiddledDistance(this.hashedSeed, q, r, s, h, t, u);
            if (g > v) {
                o = p;
                g = v;
            }
        }
        x = ((o & 4) == 0 ? l : l + 1) << 2;
        y = ((o & 2) == 0 ? m : m + 1) << 2;
        z = ((o & 1) == 0 ? n : n + 1) << 2;
        return this.world.getChunk(region, x >> 4, z >> 4).getBiome(x, y, z);
    }

    private double getFiddledDistance(long seed, int i, int j, int k, double d, double e, double f) {
        long m = salt(seed, i);
        m = salt(m, j);
        m = salt(m, k);
        m = salt(m, i);
        m = salt(m, j);
        m = salt(m, k);
        double g = fiddle(m);
        m = salt(m, seed);
        double h = fiddle(m);
        m = salt(m, seed);
        double n = fiddle(m);
        return square(f + n) + square(e + h) + square(d + g);
    }

    private double fiddle(long l) {
        double d = (double) Math.floorMod(l >> 24, 1024) / 1024.0D;
        return (d - 0.5D) * 0.9D;
    }

    private long salt(long seed, long salt) {
        return seed * (seed * 6364136223846793005L + 1442695040888963407L) + salt;
    }

    private double square(double n) {
        return n * n;
    }

    @Override
    public @NonNull Iterator<@NonNull Biome> iterator() {
        return this.world.getBiomeRegistry().iterator();
    }
}
