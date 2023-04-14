package net.pl3x.map.core.world;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Objects;
import net.querz.mca.CompressionType;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Region {
    private final World world;
    private final int regionX;
    private final int regionZ;
    private final File regionFile;

    // hard reference to neighbor regions
    // order is CW starting at NW corner (NW, N, NE, E, SE, S, SW, W)
    //private final Region[] neighbors = new Region[8];
    //private final int minX, maxX, minZ, maxZ;

    private final Chunk[] chunks = new Chunk[32 << 5];

    private final int hash;

    public Region(World world, int regionX, int regionZ, Path regionFile) {
        this.world = world;
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.regionFile = regionFile.toFile();

        /*this.minX = regionX << 5;
        this.minZ = regionZ << 5;
        this.maxX = this.minX + 0x1F;
        this.maxZ = this.minZ + 0x1F;*/

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

    /*public int getBlockY(int blockX, int blockZ) {
        return getChunk(blockX >> 4, blockZ >> 4).getBlockY(blockX, blockZ);
    }*/

    private int getChunkIndex(int chunkX, int chunkZ) {
        return (chunkX & 0x1F) + ((chunkZ & 0x1F) << 5);
    }

    @NonNull
    public Chunk getChunk(int chunkX, int chunkZ) {
        /*Region neighbor = getNeighbor(chunkX, chunkZ);
        if (neighbor != null) {
            return neighbor.getChunk(chunkX, chunkZ);
        }*/
        int index = getChunkIndex(chunkX, chunkZ);
        Chunk chunk = this.chunks[index];
        if (chunk == null) {
            try (RandomAccessFile raf = new RandomAccessFile(getRegionFile(), "r")) {
                chunk = loadChunk(raf, index);
            } catch (IOException e) {
                e.printStackTrace();
                return this.chunks[index] = new EmptyChunk(getWorld(), this);
            }
        }
        return chunk;
    }

    /*private Region getNeighbor(int chunkX, int chunkZ) {
        if (chunkX < minX) { // west
            if (chunkZ < minZ) return this.neighbors[0]; // NW
            if (chunkZ > maxZ) return this.neighbors[6]; // SW
            return this.neighbors[7]; // W
        }
        if (chunkX > maxX) { // east
            if (chunkZ < minZ) return this.neighbors[2]; // NE
            if (chunkZ > maxZ) return this.neighbors[4]; // SE
            return this.neighbors[3]; // E
        }
        if (chunkZ < minZ) return this.neighbors[1]; // N
        if (chunkZ > maxZ) return this.neighbors[5]; // S
        return null;
    }

    public void loadNeighbors() {
        this.neighbors[0] = getWorld().getRegion(this.regionX - 1, this.regionZ - 1); // NW
        this.neighbors[1] = getWorld().getRegion(this.regionX, this.regionZ - 1); // N
        this.neighbors[2] = getWorld().getRegion(this.regionX + 1, this.regionZ - 1); // NE
        this.neighbors[3] = getWorld().getRegion(this.regionX + 1, this.regionZ); // E
        this.neighbors[4] = getWorld().getRegion(this.regionX + 1, this.regionZ + 1); // SE
        this.neighbors[5] = getWorld().getRegion(this.regionX, this.regionZ + 1); // S
        this.neighbors[6] = getWorld().getRegion(this.regionX - 1, this.regionZ + 1); // SW
        this.neighbors[7] = getWorld().getRegion(this.regionX - 1, this.regionZ); // W
    }*/

    public void loadChunks() throws IOException {
        if (!getRegionFile().exists() || getRegionFile().length() <= 0) {
            return;
        }
        try (RandomAccessFile raf = new RandomAccessFile(getRegionFile(), "r")) {
            for (int index = 0; index < this.chunks.length; index++) {
                loadChunk(raf, index);
            }
        }
    }

    @NonNull
    public Chunk loadChunk(RandomAccessFile raf, int index) throws IOException {
        raf.seek(index * 4L);
        int offset = raf.read() << 16;
        offset |= (raf.read() & 0xFF) << 8;
        offset |= raf.read() & 0xFF;
        if (raf.readByte() == 0) {
            return this.chunks[index] = new EmptyChunk(getWorld(), this);
        }
        raf.seek(4096L * offset + 4); // +4 skip chunk size

        byte compressionTypeByte = raf.readByte();
        CompressionType compressionType = CompressionType.getFromID(compressionTypeByte);
        if (compressionType == null) {
            throw new IOException("Invalid compression type " + compressionTypeByte);
        }

        DataInputStream dis = new DataInputStream(new BufferedInputStream(compressionType.decompress(new FileInputStream(raf.getFD()))));
        NamedTag tag = new NBTInputStream(dis).readTag(Tag.DEFAULT_MAX_DEPTH);
        if (tag != null && tag.getTag() instanceof CompoundTag compoundTag) {
            return this.chunks[index] = Chunk.create(getWorld(), this, compoundTag).populate();
        } else {
            throw new IOException("Invalid data tag: " + (tag == null ? "null" : tag.getName()));
        }
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
