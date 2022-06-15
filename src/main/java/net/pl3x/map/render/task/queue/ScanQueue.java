package net.pl3x.map.render.task.queue;

import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.AbstractRender;

public class ScanQueue implements AbstractRender.Queue {
    private final RegionCoordinate region;

    public ScanQueue(RegionCoordinate region) {
        this.region = region;
    }

    @Override
    public void run() {
    }
}
