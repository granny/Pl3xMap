/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pl3x.map.core.world;

import de.bluecolored.bluenbt.BlueNBT;
import de.bluecolored.bluenbt.NamingStrategy;
import de.bluecolored.bluenbt.TypeToken;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class ChunkLoader {

    public static final @Nullable CompressionType[] CHUNK_COMPRESSION_MAP = new CompressionType[255];
    static {
        CHUNK_COMPRESSION_MAP[0] = CompressionType.NONE;
        CHUNK_COMPRESSION_MAP[1] = CompressionType.GZIP;
        CHUNK_COMPRESSION_MAP[2] = CompressionType.DEFLATE;
        CHUNK_COMPRESSION_MAP[3] = CompressionType.NONE;
        CHUNK_COMPRESSION_MAP[4] = CompressionType.LZ4;
    }

    private final static BlueNBT BLUENBT = new BlueNBT();
    static {
        BLUENBT.setNamingStrategy(NamingStrategy.lowerCaseWithDelimiter("_"));
        BLUENBT.register(TypeToken.of(BlockState.class), new BlockStateDeserializer());
    }

    private final World world;
    private final Region region;

    public ChunkLoader(World world, Region region) {
        this.world = world;
        this.region = region;
    }

    // sorted list of chunk-versions, loaders at the start of the list are preferred over loaders at the end
    private static final List<ChunkVersionLoader<?>> CHUNK_VERSION_LOADERS = List.of(
            new ChunkVersionLoader<>(Chunk_1_18.Data.class, Chunk_1_18::new, 2844),
            new ChunkVersionLoader<>(Chunk_1_16.Data.class, Chunk_1_16::new, 2500),
            new ChunkVersionLoader<>(Chunk_1_15.Data.class, Chunk_1_15::new, 2200),
            new ChunkVersionLoader<>(Chunk_1_13.Data.class, Chunk_1_13::new, 1519)
    );

    private ChunkVersionLoader<?> lastUsedLoader = CHUNK_VERSION_LOADERS.getFirst();

    public Chunk load(RandomAccessFile raf, long offset, int index) throws IOException {
        raf.seek(offset + 4);
        int compressionTypeId = Byte.toUnsignedInt(raf.readByte());

        CompressionType compression = CHUNK_COMPRESSION_MAP[compressionTypeId];
        if (compression == null)
            throw new IOException("Unknown chunk compression-id: " + compressionTypeId);

        // optimistic: try last used version
        ChunkVersionLoader<?> usedLoader = lastUsedLoader;
        Chunk chunk;
        InputStream decompressedIn = new BufferedInputStream(compression.decompress(new FileInputStream(raf.getFD())));
        chunk = usedLoader.load(world, region, decompressedIn, index);

        // check version and reload chunk if the wrong loader has been used and a better one has been found
        ChunkVersionLoader<?> actualLoader = findBestLoaderForVersion(chunk.getDataVersion());
        if (actualLoader != null && usedLoader != actualLoader) {
            raf.seek(offset + 5);
            decompressedIn = new BufferedInputStream(compression.decompress(new FileInputStream(raf.getFD())));
            chunk = actualLoader.load(world, region, decompressedIn, index);
            lastUsedLoader = actualLoader;
        }

        return chunk.isFull() ? chunk : new EmptyChunk(world, region, index);
    }

    private @Nullable ChunkVersionLoader<?> findBestLoaderForVersion(int version) {
        for (ChunkVersionLoader<?> loader : CHUNK_VERSION_LOADERS) {
            if (loader.mightSupport(version)) return loader;
        }
        return null;
    }

    private static class ChunkVersionLoader<D extends Chunk.Data> {

        private final Class<D> dataType;
        private final ChunkConstructor<D> constructor;
        private final int dataVersion;

        ChunkVersionLoader(Class<D> dataType, ChunkConstructor<D> constructor, int dataVersion) {
            this.dataType = dataType;
            this.constructor = constructor;
            this.dataVersion = dataVersion;
        }

        public Chunk load(World world, Region region, InputStream in, int index) throws IOException {
            try {
                D data = BLUENBT.read(in, dataType);
                return mightSupport(data.getDataVersion()) ? constructor.create(world, region, data, index) : new EmptyChunk(world, region, index);
            } catch (Exception e) {
                throw new IOException("Failed to parse chunk-data (%s): %s".formatted(dataType.getSimpleName(), e), e);
            }
        }

        public boolean mightSupport(int dataVersion) {
            return dataVersion >= this.dataVersion;
        }

    }

    private interface ChunkConstructor<D extends Chunk.Data> {

        Chunk create(World world, Region region, D data, int index);

    }

}
