package net.pl3x.map.render.progress;

import java.util.Arrays;

public class CPSTracker {
    private static final int SAMPLE_SIZE = 10;

    private final long[] avg = new long[SAMPLE_SIZE];
    private int index = -1;

    public void add(long val) {
        this.index = (this.index + 1) % SAMPLE_SIZE;
        this.avg[this.index] = val;
    }

    public double average() {
        return Arrays.stream(this.avg).filter(i -> i != 0).average().orElse(0.0D);
    }
}
