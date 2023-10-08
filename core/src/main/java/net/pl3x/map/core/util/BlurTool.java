package net.pl3x.map.core.util;

public class BlurTool {
    public static void Blur(byte[] data, int sizeX, int sizeZ, int range) {
        BlurTool.BoxBlurHorizontal(data, range, 0, 0, sizeX, sizeZ);
        BlurTool.BoxBlurVertical(data, range, 0, 0, sizeX, sizeZ);
    }

    public static void BoxBlurHorizontal(byte[] map, int range, int xStart, int yStart, int xEnd, int yEnd) {
        int w = xEnd - xStart;
        int h = yEnd - yStart;

        int halfRange = range / 2;
        int index = yStart * w;
        byte[] newColors = new byte[w];

        for (int y = yStart; y < yEnd; y++) {
            int hits = 0;
            int r = 0;
            for (int x = xStart - halfRange; x < xEnd; x++) {
                int oldPixel = x - halfRange - 1;
                if (oldPixel >= xStart) {
                    byte col = map[index + oldPixel];
                    if (col != 0) {
                        r -= col;
                    }
                    hits--;
                }

                int newPixel = x + halfRange;
                if (newPixel < xEnd) {
                    byte col = map[index + newPixel];
                    if (col != 0) {
                        r += col;
                    }
                    hits++;
                }

                if (x >= xStart) {
                    byte color = (byte) (r / hits);
                    newColors[x] = color;
                }
            }

            for (int x = xStart; x < xEnd; x++) {
                map[index + x] = newColors[x];
            }

            index += w;
        }
    }

    public static void BoxBlurVertical(byte[] map, int range, int xStart, int yStart, int xEnd, int yEnd) {
        int w = xEnd - xStart;
        int h = yEnd - yStart;

        int halfRange = range / 2;

        byte[] newColors = new byte[h];
        int oldPixelOffset = -(halfRange + 1) * w;
        int newPixelOffset = (halfRange) * w;

        for (int x = xStart; x < xEnd; x++) {
            int hits = 0;
            int r = 0;
            int index = yStart * w - halfRange * w + x;
            for (int y = yStart - halfRange; y < yEnd; y++) {
                int oldPixel = y - halfRange - 1;
                if (oldPixel >= yStart) {
                    byte col = map[index + oldPixelOffset];
                    if (col != 0) {
                        r -= col;
                    }
                    hits--;
                }

                int newPixel = y + halfRange;
                if (newPixel < yEnd) {
                    byte col = map[index + newPixelOffset];
                    if (col != 0) {
                        r += col;
                    }
                    hits++;
                }

                if (y >= yStart) {
                    byte color = (byte) (r / hits);
                    newColors[y] = color;
                }

                index += w;
            }

            for (int y = yStart; y < yEnd; y++) {
                map[y * w + x] = newColors[y];
            }
        }
    }
}
