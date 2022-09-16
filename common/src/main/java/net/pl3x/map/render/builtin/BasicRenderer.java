package net.pl3x.map.render.builtin;

import net.pl3x.map.Key;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.image.Image;
import net.pl3x.map.render.Renderer;
import net.pl3x.map.render.RendererHolder;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.render.ScanTask;

public final class BasicRenderer extends Renderer {
    private Image.Holder lightImageHolder;

    public BasicRenderer(RendererHolder holder, ScanTask scanTask) {
        super(holder, scanTask);
    }

    @Override
    public void allocateData() {
        super.allocateData();
        this.lightImageHolder = new Image.Holder(Key.of("light"), getWorld(), getRegion());
    }

    @Override
    public void saveData() {
        super.saveData();
        this.lightImageHolder.save();
    }

    @Override
    public void scanData(RegionCoordinate region, ScanData.Data scanData) {
        for (ScanData data : scanData.values()) {
            int pixelColor = basicPixelColor(data, scanData);

            int pixelX = data.getCoordinate().getBlockX() & Image.SIZE - 1;
            int pixelZ = data.getCoordinate().getBlockZ() & Image.SIZE - 1;

            getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);

            // get light level right above this block
            int lightPixel = calculateLight(data.getChunk(), data.getBlockPos(), data.getFluidState(), data.getFluidPos(), pixelColor);
            this.lightImageHolder.getImage().setPixel(pixelX, pixelZ, lightPixel);
        }
    }
}
