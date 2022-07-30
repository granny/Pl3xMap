package net.pl3x.map.util;

public class ByteUtil {
    public static byte[] toBytes(int packed) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (packed >>> (i * 8));
        }
        return bytes;
    }
}
