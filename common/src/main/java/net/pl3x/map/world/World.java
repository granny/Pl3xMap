package net.pl3x.map.world;

import java.util.Locale;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.pl3x.map.Key;
import net.pl3x.map.markers.Point;
import org.jetbrains.annotations.NotNull;

public abstract class World {
    private final ServerLevel level;
    private final Key key;
    private final Type type;
    private final long seed;

    public World(ServerLevel level) {
        this.level = level;
        this.key = Key.of(level);
        this.type = Type.get(level);
        this.seed = BiomeManager.obfuscateSeed(level.getSeed());
    }

    @NotNull
    public ServerLevel getLevel() {
        return this.level;
    }

    @NotNull
    public Key getKey() {
        return this.key;
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    public long getBiomeSeed() {
        return this.seed;
    }

    @NotNull
    public String getName() {
        return this.key.getKey();
    }

    @NotNull
    public Point getSpawn() {
        return Point.of(getLevel().getSharedSpawnPos());
    }

    public enum Type {
        OVERWORLD,
        NETHER,
        THE_END,
        CUSTOM;

        private final String name;

        Type() {
            this.name = name().toLowerCase(Locale.ROOT);
        }

        public static Type get(ServerLevel level) {
            ResourceKey<Level> key = level.dimension();
            if (key == Level.OVERWORLD) {
                return OVERWORLD;
            } else if (key == Level.NETHER) {
                return NETHER;
            } else if (key == Level.END) {
                return THE_END;
            }
            return CUSTOM;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
