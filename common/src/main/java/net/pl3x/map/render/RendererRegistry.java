package net.pl3x.map.render;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.pl3x.map.Key;
import net.pl3x.map.Registry;
import net.pl3x.map.render.builtin.BasicRenderer;
import net.pl3x.map.render.builtin.BiomeRenderer;
import net.pl3x.map.render.builtin.BlockInfoRenderer;

public class RendererRegistry extends Registry<RendererHolder> {
    public void register() {
        register(new RendererHolder(new Key("basic"), BasicRenderer.class));
        register(new RendererHolder(new Key("biomes"), BiomeRenderer.class));
        register(new RendererHolder(new Key("blockinfo"), BlockInfoRenderer.class));
    }

    public Renderer createRenderer(String name, ScanTask scanTask) {
        RendererHolder holder = get(new Key(name));
        if (holder == null) {
            throw new IllegalStateException(String.format("No renderer registered with name %s", name));
        }

        try {
            Constructor<? extends Renderer> ctor = holder.getClazz().getConstructor(String.class, scanTask.getClass());
            return ctor.newInstance(name, scanTask);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
