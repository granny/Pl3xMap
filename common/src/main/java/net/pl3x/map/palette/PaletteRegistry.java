package net.pl3x.map.palette;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;

public class PaletteRegistry {
    private static final Gson GSON = new GsonBuilder()
            //.setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    private static final Palette<Block> BLOCK_PALETTE = new Palette<>();
    private static final Map<MapWorld, Palette<Biome>> BIOME_PALETTES = new HashMap<>();

    public PaletteRegistry() {
        // create block palette
        Registry.BLOCK.forEach(block -> {
            String name = name("block", Registry.BLOCK.getKey(block));
            BLOCK_PALETTE.add(block, name);
        });
        BLOCK_PALETTE.lock();

        // save global block palette
        try {
            FileUtil.saveGzip(GSON.toJson(BLOCK_PALETTE.getMap()), MapWorld.TILES_DIR.resolve("blocks.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(MapWorld mapWorld) {
        Palette<Biome> palette = new Palette<>();
        BIOME_PALETTES.put(mapWorld, palette);

        Registry<Biome> registry = BiomeColors.getBiomeRegistry(mapWorld.getWorld().getLevel());
        registry.forEach(biome -> {
            String name = name("biome", registry.getKey(biome));
            palette.add(biome, name);
        });
        palette.lock();

        try {
            FileUtil.saveGzip(GSON.toJson(palette.getMap()), mapWorld.getTilesDir().resolve("biomes.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregister(MapWorld mapWorld) {
        BIOME_PALETTES.remove(mapWorld);
    }

    private String name(String type, ResourceLocation key) {
        return Language.getInstance().getOrDefault(Util.makeDescriptionId(type, key));
    }

    public Palette<Block> getBlockPalette() {
        return BLOCK_PALETTE;
    }

    public Palette<Biome> getBiomePalette(MapWorld world) {
        return BIOME_PALETTES.get(world);
    }
}
