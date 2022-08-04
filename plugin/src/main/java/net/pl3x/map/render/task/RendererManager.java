package net.pl3x.map.render.task;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.render.task.builtin.BasicRenderer;
import net.pl3x.map.render.task.builtin.BiomeRenderer;
import net.pl3x.map.render.task.builtin.BlockInfo;

public class RendererManager {
    public static final Class<? extends Renderer> BASIC;
    public static final Class<? extends Renderer> BIOMES;
    public static final Class<? extends Renderer> BLOCKINFO;

    static {
        RendererManager manager = Pl3xMap.api().getRendererManager();
        BASIC = manager.register("basic", BasicRenderer.class);
        BIOMES = manager.register("biomes", BiomeRenderer.class);
        BLOCKINFO = manager.register("blockinfo", BlockInfo.class);
    }

    private final Map<String, Class<? extends Renderer>> renderers = new HashMap<>();

    public Class<? extends Renderer> register(String name, Class<? extends Renderer> renderer) {
        if (this.renderers.containsKey(name)) {
            throw new IllegalStateException(String.format("Renderer already registered with name %s", name));
        }
        this.renderers.put(name, renderer);
        return renderer;
    }

    public Class<? extends Renderer> unregister(String name) {
        if (!this.renderers.containsKey(name)) {
            throw new IllegalStateException(String.format("No renderer registered with name %s", name));
        }
        return this.renderers.remove(name);
    }

    public Renderer createRenderer(String name, ScanTask scanTask) {
        Class<? extends Renderer> clazz = this.renderers.get(name);
        if (clazz == null) {
            throw new IllegalStateException(String.format("No renderer registered with name %s", name));
        }

        try {
            Constructor<? extends Renderer> ctor = clazz.getConstructor(String.class, ScanTask.class);
            return ctor.newInstance(name, scanTask);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
