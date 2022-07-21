package net.pl3x.map.render.task.builtin;

import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.render.task.ScanTask;

public final class BasicRenderer extends Renderer {
    private Image.Holder lightImageHolder;

    public BasicRenderer(String name, ScanTask scanTask) {
        super(name, scanTask);
    }

    @Override
    public void allocateData() {
        super.allocateData();
        this.lightImageHolder = new Image.Holder("light", getWorld(), getRegion());
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
