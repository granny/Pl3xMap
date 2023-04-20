package net.pl3x.map.core.registry;

import java.lang.reflect.InvocationTargetException;
import net.pl3x.map.core.renderer.BasicRenderer;
import net.pl3x.map.core.renderer.BiomeRenderer;
import net.pl3x.map.core.renderer.BlockInfoRenderer;
import net.pl3x.map.core.renderer.FlowerMapRenderer;
import net.pl3x.map.core.renderer.Renderer;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RendererRegistry extends Registry<Renderer.Builder> {
    public static final String BASIC = "basic";
    public static final String BIOMES = "biomes";
    public static final String BLOCKINFO = "blockinfo";
    public static final String FLOWERMAP = "flowermap";

    public void register() {
        register(BASIC, new Renderer.Builder(BASIC, "Basic", BasicRenderer.class));
        register(BIOMES, new Renderer.Builder(BIOMES, "Biomes", BiomeRenderer.class));
        register(BLOCKINFO, new Renderer.Builder(BLOCKINFO, "BlockInfo", BlockInfoRenderer.class));
        register(FLOWERMAP, new Renderer.Builder(FLOWERMAP, "FlowerMap", FlowerMapRenderer.class));
    }

    @NonNull
    public Renderer createRenderer(@NonNull World world, Renderer.@NonNull Builder builder) {
        try {
            return builder.clazz().getConstructor(World.class, Renderer.Builder.class).newInstance(world, builder);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
