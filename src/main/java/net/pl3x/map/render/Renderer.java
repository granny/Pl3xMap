package net.pl3x.map.render;

import java.util.HashMap;
import java.util.Map;

public abstract class Renderer {
    private final int maxHeight;
    private final int minHeight;
    private final boolean iterateDown;
    private final ScanType scanType;
    private final boolean biomes;
    private final int biomesBlend;
    private final boolean checkerboardLava;
    private final boolean checkerboardWater;
    private final boolean translucentGlass;
    private final boolean translucentWater;

    private final Map<String, Integer> blockColors = new HashMap<>();
    private final Map<String, Integer> biomeColors = new HashMap<>();

    public Renderer(int maxHeight, int minHeight, boolean iterateDown, ScanType scanType, boolean checkerboardLava, boolean checkerboardWater, boolean biomes, int biomesBlend, boolean translucentGlass, boolean translucentWater) {
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
        this.iterateDown = iterateDown;
        this.scanType = scanType;
        this.checkerboardLava = checkerboardLava;
        this.checkerboardWater = checkerboardWater;
        this.biomes = biomes;
        this.biomesBlend = biomesBlend;
        this.translucentGlass = translucentGlass;
        this.translucentWater = translucentWater;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public int getMinHeight() {
        return this.minHeight;
    }

    public boolean isIterateDown() {
        return this.iterateDown;
    }

    public ScanType getScanType() {
        return this.scanType;
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

    public boolean isTranslucentGlass() {
        return this.translucentGlass;
    }

    public boolean isTranslucentWater() {
        return this.translucentWater;
    }

    public abstract void renderRegion(int x, int z);

    public enum ScanType {
        BLOCKS, BIOMES
    }
}
