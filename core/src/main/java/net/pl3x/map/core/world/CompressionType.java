package net.pl3x.map.core.world;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public enum CompressionType {

    NONE(out -> out, in -> in),
    GZIP(GZIPOutputStream::new, GZIPInputStream::new),
    DEFLATE(DeflaterOutputStream::new, InflaterInputStream::new),
    LZ4(LZ4BlockOutputStream::new, LZ4BlockInputStream::new);

    private final StreamTransformer<OutputStream> compressor;
    private final StreamTransformer<InputStream> decompressor;

    CompressionType(StreamTransformer<OutputStream> compressor, StreamTransformer<InputStream> decompressor) {
        this.compressor = compressor;
        this.decompressor = decompressor;
    }

    public OutputStream compress(OutputStream out) throws IOException {
        return compressor.apply(out);
    }

    public InputStream decompress(InputStream in) throws IOException {
        return decompressor.apply(in);
    }

    @FunctionalInterface
    private interface StreamTransformer<T> {
        T apply(T original) throws IOException;
    }

}
