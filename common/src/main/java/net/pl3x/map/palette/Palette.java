package net.pl3x.map.palette;

import java.util.LinkedHashMap;
import java.util.Map;

public class Palette<T> {
    private final Map<T, Entry> map = new LinkedHashMap<>();

    private boolean locked;

    public void lock() {
        this.locked = true;
    }

    public void add(T type, String name) {
        if (locked) {
            throw new IllegalStateException("Palette is locked");
        }
        int index = this.map.size();
        Entry entry = new Entry(index, name);
        this.map.put(type, entry);
    }

    public Entry get(T type) {
        return this.map.get(type);
    }

    public Map<Integer, String> getMap() {
        Map<Integer, String> result = new LinkedHashMap<>();
        this.map.values().forEach(v -> result.put(v.getIndex(), v.getName()));
        return result;
    }

    public static class Entry {
        private final int index;
        private final String name;

        public Entry(int index, String name) {
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
}
