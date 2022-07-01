package net.pl3x.map.render.scanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.render.renderer.Renderer;
import net.pl3x.map.render.renderer.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.scanner.builtin.BasicScanner;

public class Scanners {
    public static final Scanners INSTANCE = new Scanners();

    public static final Class<? extends Scanner> BASIC = INSTANCE.register("basic", BasicScanner.class);

    private final Map<String, Class<? extends Scanner>> scanners = new HashMap<>();

    private Scanners() {
    }

    public Class<? extends Scanner> register(String name, Class<? extends Scanner> scanner) {
        if (this.scanners.containsKey(name)) {
            throw new IllegalStateException(String.format("Scanner already registered with name %s", name));
        }
        this.scanners.put(name, scanner);
        return scanner;
    }

    public Class<? extends Scanner> unregister(String name) {
        if (!this.scanners.containsKey(name)) {
            throw new IllegalStateException(String.format("No scanner registered with name %s", name));
        }
        return this.scanners.remove(name);
    }

    public Scanner createScanner(String name, Renderer render, RegionCoordinate region, Collection<Long> chunks) {
        Class<? extends Scanner> clazz = this.scanners.get(name);
        if (clazz == null) {
            throw new IllegalStateException(String.format("No scanner registered with name %s", name));
        }

        try {
            Constructor<? extends Scanner> ctor = clazz.getConstructor(Renderer.class, RegionCoordinate.class, Collection.class);
            return ctor.newInstance(render, region, chunks);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
