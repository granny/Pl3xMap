/*
 * This file is part of Pl3xMap, licensed under the MIT License (MIT).
 *
 * BLinear-v3 (.b_linear) region file reader.
 *
 * Format CONFIRMED directly against LuminolMC/Shiroha's reference
 * implementation, BufferedLinearRegionFile.java. Key insight that fixed
 * the final remaining bug: LZ4 compression is used ONLY in the transient
 * swap file (.swp); when a bucket is flushed to the MASTER file (the
 * .b_linear file this reader parses), buildBucketRecord() DECOMPRESSES
 * each chunk's LZ4 payload first, then writes the raw NBT bytes into the
 * section (which is then Zstd-compressed as a whole bucket). So inside a
 * Zstd-decompressed bucket, chunk payloads are PLAIN NBT, not LZ4 data --
 * confirmed by the previous attempt's "LZ4Exception: Malformed input at
 * 22" (16-byte meta header + 6 bytes into what is actually a raw NBT
 * TAG_Compound stream, not an LZ4 frame).
 *
 *   -- Master file header (14 bytes, offset 0) --
 *   long superblock       (8 bytes)  -0x200812250269L
 *   byte version          (1 byte)   0x03 (MASTER_FILE_VERSION_BUCKET)
 *   byte compressionLevel (1 byte)   write-time zstd level; unused for reads
 *   int  xxHashSeed       (4 bytes)  unused for reads
 *
 *   -- Position table (128 bytes, offset 14) --
 *   long[16] bucketOffsets            ABSOLUTE file offsets, one per
 *                                     bucket. 0 = bucket has no chunks.
 *
 *   -- Bucket record (at each non-zero offset above) --
 *   int decompressedSize  (4 bytes)  size of the FULL section stream below
 *   int compressedSize    (4 bytes)  zstd-compressed byte count that follows
 *   byte[compressedSize]             zstd frame; decompresses to a stream
 *                                     of 64 INTERLEAVED per-chunk sections,
 *                                     read strictly sequentially (each
 *                                     slot's size-prefix is immediately
 *                                     followed by that slot's full payload,
 *                                     then the next slot's size-prefix):
 *
 *     for each of the 64 chunk slots in this bucket, IN ORDER:
 *       int sectionSize             0 = empty slot; if >0, IMMEDIATELY
 *                                     followed (no gap) by:
 *         int  dataLen      (4)     length of the raw NBT payload below
 *         long timestamp    (8)     unused for reads
 *         int  xxhash32     (4)     xxHash32 of the NBT payload (seed
 *                                    0x0721); NOT verified here, see TODO
 *         byte[dataLen]             RAW (uncompressed) NBT data -- no
 *                                    LZ4 layer at this level
 *
 * TODO: xxHash32 integrity verification is not performed here; a hash
 * mismatch would currently surface later as a garbled/failed NBT parse
 * instead of a clean "corrupt chunk" error.
 */
package net.pl3x.map.core.world;

import com.github.luben.zstd.ZstdInputStream;
import java.io.BufferedInputStream;
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
    static boolean DEBUG = true;

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

        // CONFIRMED (reference source): 1-byte compressionLevel + 4-byte
        // xxHashSeed follow the version byte -- 5 bytes total.
        raf.skipBytes(1 + 4);

        // CONFIRMED: 16 plain, absolute, unshifted file offsets. 0 = empty bucket.
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

        raf.seek(bucketOffset);

        // CONFIRMED: 4-byte decompressed size + 4-byte compressed size,
        // then a Zstd frame (no separate per-bucket compression flag).
        int decompressedSize = raf.readInt();
        int compressedSize = raf.readInt();

        if (DEBUG) {
            Logger.debug("BLinear bucket#" + bucketIndex + " seekTo=" + bucketOffset
                    + " decompressedSize=" + decompressedSize + " compressedSize=" + compressedSize);
        }

        if (compressedSize <= 0) {
            return new EmptyChunk(region.getWorld(), region, index);
        }
        if (compressedSize > MAX_BUCKET_LENGTH || decompressedSize > MAX_BUCKET_LENGTH * 4) {
            throw new IOException("Implausible BLinear bucket size (decompressed=" + decompressedSize
                    + ", compressed=" + compressedSize + ") in " + path + " at bucket " + bucketIndex);
        }

        byte[] compressed = new byte[compressedSize];
        raf.readFully(compressed);

        try (DataInputStream bucketIn = new DataInputStream(new BufferedInputStream(
                new ZstdInputStream(new ByteArrayInputStream(compressed))))) {

            // CONFIRMED: 64 chunk slots read strictly SEQUENTIALLY (not a
            // flat table followed by a data area) -- each slot's 4-byte
            // sectionSize is immediately followed by that slot's full
            // payload, then the next slot's sectionSize comes right after.
            for (int i = 0; i < CHUNKS_PER_BUCKET; i++) {
                int sectionSize = bucketIn.readInt();

                if (i != chunkInBucket) {
                    // not the slot we want: skip its payload (0 bytes if
                    // empty) and move to the next slot's size-prefix
                    if (sectionSize > 0) {
                        skipFully(bucketIn, sectionSize);
                    }
                    continue;
                }

                // this is our slot
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
                sectionIn.skipBytes(4); // xxhash32, unverified (see class javadoc TODO)

                if (dataLen < 0 || dataLen > MAX_SECTION_LENGTH) {
                    throw new IOException("Implausible BLinear chunk dataLen (" + dataLen + ") in " + path
                            + " at bucket " + bucketIndex + " chunk " + chunkInBucket);
                }
                if (SECTOR_META_SIZE + dataLen != sectionSize) {
                    throw new IOException("BLinear section size mismatch: header says dataLen=" + dataLen
                            + " (expected sectionSize=" + (SECTOR_META_SIZE + dataLen) + ") but actual sectionSize=" + sectionSize
                            + " in " + path + " at bucket " + bucketIndex + " chunk " + chunkInBucket);
                }

                // FIX (confirmed against buildBucketRecord() in the
                // reference source): the payload here is RAW, already-
                // decompressed NBT -- LZ4 is only used in the transient
                // swap file and is unwrapped before the master file is
                // ever written. No LZ4 decompression step needed here.
                byte[] nbt = new byte[dataLen];
                sectionIn.readFully(nbt);

                return chunkLoader.load(new ByteArrayInputStream(nbt), index);
            }

            // shouldn't reach here (loop always returns once i == chunkInBucket),
            // but keep the compiler happy and fail safe just in case
            return new EmptyChunk(region.getWorld(), region, index);
        }
    }

    private static void skipFully(InputStream in, long toSkip) throws IOException {
        long remaining = toSkip;
        while (remaining > 0) {
            long skipped = in.skip(remaining);
            if (skipped <= 0) {
                if (in.read() < 0) {
                    throw new IOException("Unexpected EOF while skipping " + toSkip + " bytes within bucket");
                }
                remaining--;
            } else {
                remaining -= skipped;
            }
        }
    }
}
