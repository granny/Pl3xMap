package net.pl3x.map.renderer;

public class BasicRenderer extends AbstractRenderer {
    public BasicRenderer(int maxHeight, int minHeight, boolean iterateUp, boolean checkerboardLava, boolean checkerboardWater, boolean biomes, int biomesBlend, boolean clearGlass, boolean clearWater) {
        super(maxHeight, minHeight, iterateUp, checkerboardLava, checkerboardWater, biomes, biomesBlend, clearGlass, clearWater);
    }

    @Override
    public void renderRegion(int x, int z) {
    }
}
