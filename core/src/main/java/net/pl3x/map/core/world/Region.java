/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.world;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Objects;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.log.Logger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Region {
    private final World world;
    private final int regionX;
    private final int regionZ;
    private final File regionFile;

    private final ChunkLoader chunkLoader;

    private final Chunk[] chunks = new Chunk[32 << 5];

    private final int hash;

    public Region(World world, int regionX, int regionZ, Path regionFile) {
        this.world = world;
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.regionFile = regionFile.toFile();

        this.chunkLoader = new ChunkLoader(world, this);

        this.hash = Objects.hash(world, regionX, regionZ);
    }

    public World getWorld() {
        return this.world;
    }

    public int getX() {
        return this.regionX;
    }

    public int getZ() {
        return this.regionZ;
    }

    public File getRegionFile() {
        return this.regionFile;
    }

    private int getChunkIndex(int chunkX, int chunkZ) {
        return (chunkX & 0x1F) + ((chunkZ & 0x1F) << 5);
    }

    public Chunk getChunk(int chunkX, int chunkZ) {
        int index = getChunkIndex(chunkX, chunkZ);
        Chunk chunk = this.chunks[index];
        if (chunk == null) {
            try (RandomAccessFile raf = new RandomAccessFile(getRegionFile(), "r")) {
                chunk = loadChunk(raf, index);
            } catch (EOFException | FileNotFoundException ignore) {
            } catch (IOException e) {
                Logger.severe("Failed to load chunk at region [%d, %d]".formatted(chunkX, chunkZ), e);
            }
            if (chunk == null) {
                return this.chunks[index] = new EmptyChunk(getWorld(), this, index);
            }
        }
        return chunk;
    }

    public void loadChunks() throws IOException {
        if (!getRegionFile().exists() || getRegionFile().length() <= 0) {
            return;
        }
        try (RandomAccessFile raf = new RandomAccessFile(getRegionFile(), "r")) {
            for (int index = 0; index < this.chunks.length; index++) {
                Pl3xMap.api().getRegionProcessor().checkPaused();
                loadChunk(raf, index);
            }
        } catch (EOFException ignore) {
        }
    }

    public Chunk loadChunk(RandomAccessFile raf, int index) throws IOException {
        raf.seek(index * 4L);

        byte[] header = new byte[4];
        raf.readFully(header, 0, 4);

        long offset = (header[0] & 0xFF) << 16;
        offset |= (header[1] & 0xFF) << 8;
        offset |= header[2] & 0xFF;
        offset *= 4096;
        int size = (header[3] & 0xFF) * 4096;

        if (size <= 0) return this.chunks[index] = new EmptyChunk(world, this, index);
        return this.chunks[index] = chunkLoader.load(raf, offset, index);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Region other = (Region) o;
        return getWorld().equals(other.getWorld())
                && getX() == other.getX()
                && getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public String toString() {
        return "Region{"
                + "world=" + getWorld()
                + ",x=" + getX()
                + ",z=" + getZ()
                + "}";
    }
}
