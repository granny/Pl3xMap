package net.pl3x.map.render;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Heightmap {
    public int[] x = new int[16];
    public int[] z = new int[16];

    public enum Type {
        OLD_SCHOOL,
        MODERN,
        DYNMAP;

        private static final Map<String, Type> BY_NAME = new HashMap<>();

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
