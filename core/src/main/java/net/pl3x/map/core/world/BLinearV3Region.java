package net.pl3x.map.core.world;

import com.github.luben.zstd.ZstdInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.pl3x.map.core.log.Logger;

/**
 * Read-only reader for Luminol/Shiroha "BLinear v3" (.b_linear) region files.
 */
final class BLinearV3Region {

    // set to false to silence the verbose per-field debug logging
    static boolean DEBUG = false;

    static final String FILE_SUFFIX = ".b_linear";

    private static final long SUPERBLOCK = -0x200812250269L;
    private static final byte VERSION = 0x03; // MASTER_FILE_VERSION_BUCKET

    private static final int BUCKET_SHIFT = 6;
    private static final int CHUNKS_PER_BUCKET = 1 << BUCKET_SHIFT; // 64
    private static final int BUCKET_COUNT = 1024 / CHUNKS_PER_BUCKET; // 16

    // per-chunk section meta header: dataLen(int) + timestamp(long) + xxhash32(int)
    private static final int SECTOR_META_SIZE = Integer.BYTES + Long.BYTES + Integer.BYTES; // 16

    // sane upper bounds so a garbage/misaligned read fails fast instead of
    // trying to allocate gigabytes or spin forever
    private static final int MAX_BUCKET_LENGTH = 64 * 1024 * 1024;   // 64 MB
    private static final int MAX_SECTION_LENGTH = 16 * 1024 * 1024;  // 16 MB

    // tracks which files we've already logged a top-level failure for, so we
    // don't spam the log 1024 times (once per chunk) for the same bad file
    private static final Set<String> SUPPRESSED_FILES =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    // NEW: per-thread single-slot decompressed-bucket cache (see class
    // javadoc "PERFORMANCE FIX" above). Renderer worker threads each get
    // their own slot, so there is no cross-thread contention/locking.
    private static final ThreadLocal<BucketCache> BUCKET_CACHE = new ThreadLocal<>();

    private static final class BucketCache {
        RandomAccessFile raf;
        long bucketOffset;
        byte[] decompressed;
    }

    private BLinearV3Region() {
    }

    /**
     * Loads a single chunk from a BLinear-v3 region file. Never throws --
     * any parsing failure is logged (once per file) and degrades to an
     * {@link EmptyChunk} for the requested index, so a single bad/garbage
     * chunk (or an entirely wrong-layout file) never aborts the rest of
     * {@link Region#loadChunks()}'s loop.
     */
    static Chunk loadChunk(Region region, ChunkLoader chunkLoader, RandomAccessFile raf, int index) {
        String path = region.getRegionFile().toString();
        try {
            return loadChunkUnsafe(region, chunkLoader, raf, index);
        } catch (Exception e) {
            if (SUPPRESSED_FILES.add(path)) {
                Logger.severe("Failed to read BLinear-v3 region file (further errors for this file are suppressed): " + path, e);
            }
            if (DEBUG) {
                Logger.debug("BLinear chunk-load failure at index " + index + " in " + path + " -> " + e);
            }
            return new EmptyChunk(region.getWorld(), region, index);
        }
    }

    private static Chunk loadChunkUnsafe(Region region, ChunkLoader chunkLoader, RandomAccessFile raf, int index) throws IOException {
        String path = region.getRegionFile().toString();

        raf.seek(0);

        long superblock = raf.readLong();
        if (superblock != SUPERBLOCK) {
            throw new IOException("Invalid BLinear superblock (" + superblock + ") in " + path);
        }

        byte version = raf.readByte();
        if (version != VERSION) {
            throw new IOException("Unsupported BLinear version (" + version + ") in " + path);
        }

        // 1-byte compressionLevel + 4-byte xxHashSeed follow the version byte
        raf.skipBytes(1 + 4);

        // 16 plain, absolute, unshifted file offsets. 0 = empty bucket.
        long[] bucketOffsets = new long[BUCKET_COUNT];
        for (int i = 0; i < BUCKET_COUNT; i++) {
            bucketOffsets[i] = raf.readLong();
        }
        if (DEBUG) {
            Logger.debug("BLinear bucketOffsets=" + Arrays.toString(bucketOffsets));
        }

        int bucketIndex = index >> BUCKET_SHIFT;
        int chunkInBucket = index & (CHUNKS_PER_BUCKET - 1);

        long bucketOffset = bucketOffsets[bucketIndex];
        if (bucketOffset <= 0) {
            // bucket was never written -> every chunk inside it is empty
            return new EmptyChunk(region.getWorld(), region, index);
        }

        // NEW: check the per-thread cache before touching the file/Zstd at all.
        // Region.loadChunks() walks indices 0..1023 in order, so 64 consecutive
        // calls share the same bucketOffset -- this turns 64 decompressions
        // into 1 for that common access pattern.
        byte[] decompressed = getOrDecompressBucket(raf, bucketOffset, bucketIndex, path);

        DataInputStream bucketIn = new DataInputStream(new ByteArrayInputStream(decompressed));

        // 64 chunk slots read strictly SEQUENTIALLY from the decompressed
        // bucket bytes -- each slot's 4-byte sectionSize is immediately
        // followed by that slot's full payload, then the next slot's
        // sectionSize comes right after. This is all in-memory array
        // navigation now (no stream skip() calls), so it's fast regardless.
        for (int i = 0; i < CHUNKS_PER_BUCKET; i++) {
            int sectionSize = bucketIn.readInt();

            if (i != chunkInBucket) {
                if (sectionSize > 0) {
                    bucketIn.skipBytes(sectionSize);
                }
                continue;
            }

            if (sectionSize <= 0) {
                return new EmptyChunk(region.getWorld(), region, index);
            }
            if (sectionSize > MAX_SECTION_LENGTH) {
                throw new IOException("Implausible BLinear section length (" + sectionSize + ") in " + path
                        + " at bucket " + bucketIndex + " chunk " + chunkInBucket);
            }
            if (sectionSize <= SECTOR_META_SIZE) {
                throw new IOException("BLinear section too short (" + sectionSize + " bytes) to contain meta header in "
                        + path + " at bucket " + bucketIndex + " chunk " + chunkInBucket);
            }

            byte[] section = new byte[sectionSize];
            bucketIn.readFully(section);

            DataInputStream sectionIn = new DataInputStream(new ByteArrayInputStream(section));
            int dataLen = sectionIn.readInt();
            sectionIn.skipBytes(8); // timestamp, unused
            sectionIn.skipBytes(4);

            if (dataLen < 0 || dataLen > MAX_SECTION_LENGTH) {
                throw new IOException("Implausible BLinear chunk dataLen (" + dataLen + ") in " + path
                        + " at bucket " + bucketIndex + " chunk " + chunkInBucket);
            }
            if (SECTOR_META_SIZE + dataLen != sectionSize) {
                throw new IOException("BLinear section size mismatch: header says dataLen=" + dataLen
                        + " (expected sectionSize=" + (SECTOR_META_SIZE + dataLen) + ") but actual sectionSize=" + sectionSize
                        + " in " + path + " at bucket " + bucketIndex + " chunk " + chunkInBucket);
            }

            byte[] nbt = new byte[dataLen];
            sectionIn.readFully(nbt);

            return chunkLoader.load(new ByteArrayInputStream(nbt), index);
        }

        return new EmptyChunk(region.getWorld(), region, index);
    }

    /**
     * Returns the decompressed bytes for the bucket at {@code bucketOffset},
     * using the per-thread single-slot cache when possible instead of
     * re-reading and re-decompressing from disk.
     */
    private static byte[] getOrDecompressBucket(RandomAccessFile raf, long bucketOffset, int bucketIndex, String path) throws IOException {
        BucketCache cache = BUCKET_CACHE.get();
        if (cache != null && cache.raf == raf && cache.bucketOffset == bucketOffset) {
            return cache.decompressed;
        }

        raf.seek(bucketOffset);

        int decompressedSize = raf.readInt();
        int compressedSize = raf.readInt();

        if (DEBUG) {
            Logger.debug("BLinear bucket#" + bucketIndex + " seekTo=" + bucketOffset
                    + " decompressedSize=" + decompressedSize + " compressedSize=" + compressedSize);
        }

        if (compressedSize <= 0) {
            byte[] empty = new byte[0];
            updateCache(raf, bucketOffset, empty);
            return empty;
        }
        if (compressedSize > MAX_BUCKET_LENGTH || decompressedSize > MAX_BUCKET_LENGTH * 4) {
            throw new IOException("Implausible BLinear bucket size (decompressed=" + decompressedSize
                    + ", compressed=" + compressedSize + ") in " + path + " at bucket " + bucketIndex);
        }

        byte[] compressed = new byte[compressedSize];
        raf.readFully(compressed);

        byte[] decompressed;
        try (InputStream zstdIn = new ZstdInputStream(new ByteArrayInputStream(compressed))) {
            decompressed = zstdIn.readAllBytes();
        }

        updateCache(raf, bucketOffset, decompressed);
        return decompressed;
    }

    private static void updateCache(RandomAccessFile raf, long bucketOffset, byte[] decompressed) {
        BucketCache cache = new BucketCache();
        cache.raf = raf;
        cache.bucketOffset = bucketOffset;
        cache.decompressed = decompressed;
        BUCKET_CACHE.set(cache);
    }
}
