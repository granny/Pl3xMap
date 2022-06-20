package net.pl3x.map.progress;

import java.util.Arrays;

public class CPSTracker {
    final int[] avg = new int[15];
    int index = 0;

    public void add(int val) {
        this.index++;
        if (this.index == 15) {
            this.index = 0;
        }
        this.avg[this.index] = val;
    }

    public double average() {
        return Arrays.stream(this.avg).filter(i -> i != 0).average().orElse(0.00D);
    }
}
