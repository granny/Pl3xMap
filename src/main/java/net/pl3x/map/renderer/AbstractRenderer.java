package net.pl3x.map.renderer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRenderer {
    private final int maxHeight;
    private final int minHeight;
    private final boolean iterateUp;
    private final boolean checkerboardLava;
    private final boolean checkerboardWater;
    private final boolean biomes;
    private final int biomesBlend;
    private final boolean clearGlass;
    private final boolean clearWater;

    private final Map<String, Integer> blockColors = new HashMap<>();
    private final Map<String, Integer> biomeColors = new HashMap<>();

    public AbstractRenderer(int maxHeight, int minHeight, boolean iterateUp, boolean checkerboardLava, boolean checkerboardWater, boolean biomes, int biomesBlend, boolean clearGlass, boolean clearWater) {
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
        this.iterateUp = iterateUp;
        this.checkerboardLava = checkerboardLava;
        this.checkerboardWater = checkerboardWater;
        this.biomes = biomes;
        this.biomesBlend = biomesBlend;
        this.clearGlass = clearGlass;
        this.clearWater = clearWater;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public int getMinHeight() {
        return this.minHeight;
    }

    public boolean isIterateUp() {
        return this.iterateUp;
    }

    public boolean isCheckerboardLava() {
        return this.checkerboardLava;
    }

    public boolean isCheckerboardWater() {
        return this.checkerboardWater;
    }

    public boolean isBiomes() {
        return this.biomes;
    }

    public int getBiomesBlend() {
        return this.biomesBlend;
    }

    public boolean isClearGlass() {
        return this.clearGlass;
    }

    public boolean isClearWater() {
        return this.clearWater;
    }

    /**
     * Check if a render is currently in progress on this world
     *
     * @return true if a render is in progress
     */
    public boolean isRendering() {
        return false; // TODO
    }

    public abstract void renderRegion(int x, int z);

    public void stop() {
        // store progress and stop
    }
}
