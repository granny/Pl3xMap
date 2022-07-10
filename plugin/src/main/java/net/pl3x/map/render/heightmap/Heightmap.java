package net.pl3x.map.render.heightmap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;

public abstract class Heightmap {
    public int[] x = new int[16];
    public int[] z = new int[16];

    public abstract int scan(BlockPos pos, int x, int z, boolean flat);

    public enum Type {
        OLD_SCHOOL(() -> new OldSchoolHeightmap()),
        MODERN(() -> new ModernHeightmap()),
        DYNMAP(() -> new DynmapHeightmap());

        private static final Map<String, Type> BY_NAME = new HashMap<>();

        private final Supplier<Heightmap> supplier;

        Type(Supplier<Heightmap> supplier) {
            this.supplier = supplier;
        }

        public Heightmap createHeightmap() {
            return this.supplier.get();
        }

        public static Type get(String name) {
            Type type = BY_NAME.get(name.toUpperCase(Locale.ROOT)
                    .replaceAll("\\s+", "_")
                    .replaceAll("\\W", ""));
            return type == null ? MODERN : type;
        }

        static {
            for (Type type : values()) {
                BY_NAME.put(type.name(), type);
            }
        }
    }
}
