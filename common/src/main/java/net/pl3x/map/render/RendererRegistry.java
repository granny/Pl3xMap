package net.pl3x.map.render;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.pl3x.map.Key;
import net.pl3x.map.registry.KeyedRegistry;
import net.pl3x.map.render.builtin.BasicRenderer;
import net.pl3x.map.render.builtin.BiomeRenderer;
import net.pl3x.map.render.builtin.BlockInfoRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RendererRegistry extends KeyedRegistry<RendererHolder> {
    public static final Key BASIC = Key.of("basic");
    public static final Key BIOMES = Key.of("biomes");
    public static final Key BLOCKINFO = Key.of("blockinfo");

    public void register() {
        register(new RendererHolder(BASIC, "Basic", BasicRenderer.class));
        register(new RendererHolder(BIOMES, "Biomes", BiomeRenderer.class));
        register(new RendererHolder(BLOCKINFO, "BlockInfo", BlockInfoRenderer.class, false));
    }

    public Renderer createRenderer(@NotNull RendererHolder holder, @NotNull ScanTask scanTask) {
        try {
            Constructor<? extends Renderer> ctor = holder.getClazz().getConstructor(RendererHolder.class, scanTask.getClass());
            return ctor.newInstance(holder, scanTask);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the registered value for the provided renderer.
     * <p>
     * Will return null if no value registered with provided renderer.
     *
     * @param name renderer name
     * @return registered value or null
     */
    @Nullable
    public RendererHolder get(@NotNull String name) {
        Preconditions.checkNotNull(name);
        return this.entries.get(Key.of(name));
    }
}
