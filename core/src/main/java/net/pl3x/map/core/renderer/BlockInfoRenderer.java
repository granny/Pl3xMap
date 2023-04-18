package net.pl3x.map.core.renderer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.pl3x.map.core.image.TileImage;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.util.ByteUtil;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.Block;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import net.pl3x.map.core.world.World;

public class BlockInfoRenderer extends Renderer {
    private static final Map<Path, ReadWriteLock> FILE_LOCKS = new ConcurrentHashMap<>();

    private ByteBuffer byteBuffer;

    public BlockInfoRenderer(World world, Builder builder) {
        super(world, builder);
    }

    @Override
    public void allocateData(Point region) {
        this.byteBuffer = ByteBuffer.allocate(512 * 512 * 4 + 12);
        Path path = getWorld().getTilesDirectory()
                .resolve(String.format(TileImage.DIR_PATH, 0, getKey()))
                .resolve(String.format(TileImage.FILE_PATH, region.x(), region.z(), "pl3xmap.gz"));
        try {
            if (Files.exists(path) && Files.size(path) > 0) {
                FileUtil.readGzip(path, this.byteBuffer);
            }
        } catch (IOException ignore) {
            // silently fail - we're clearing and rebuilding the entire byteBuffer anyway in scanData()
        }
    }

    @Override
    public void saveData(Point region) {
        Path tilesDir = getWorld().getTilesDirectory();
        for (int zoom = 0; zoom <= getWorld().getConfig().ZOOM_MAX_OUT; zoom++) {
            Path dirPath = tilesDir.resolve(String.format(TileImage.DIR_PATH, zoom, getKey()));

            // create directories if they don't exist
            FileUtil.createDirs(dirPath);

            // calculate correct sizes for this zoom level
            int step = Mathf.pow2(zoom);
            int size = 512 / step;

            Path filePath = dirPath.resolve(String.format(TileImage.FILE_PATH,
                    (int) Math.floor((double) region.x() / step),
                    (int) Math.floor((double) region.z() / step),
                    "pl3xmap.gz"));

            ReadWriteLock lock = FILE_LOCKS.computeIfAbsent(filePath, k -> new ReentrantReadWriteLock(true));
            lock.writeLock().lock();

            Throwable error = null;

            if (zoom == 0) {
                // short circuit bottom zoom
                try {
                    FileUtil.saveGzip(this.byteBuffer.array(), filePath);
                } catch (IOException e) {
                    error = e;
                }
            } else {
                try {
                    // read existing data from disk
                    ByteBuffer buffer = ByteBuffer.allocate(this.byteBuffer.capacity());
                    if (Files.exists(filePath) && Files.size(filePath) > 0) {
                        try {
                            FileUtil.readGzip(filePath, buffer);
                        } catch (Throwable ignore) {
                            // silently fail - the file's fucked anyway so whatever
                        }
                    }

                    // copy header
                    for (int i = 0; i < 12; i++) {
                        buffer.put(i, this.byteBuffer.get(i));
                    }

                    // write new data
                    int baseX = (region.x() * size) & 511;
                    int baseZ = (region.z() * size) & 511;
                    for (int x = 0; x < 512; x += step) {
                        for (int z = 0; z < 512; z += step) {
                            int index = z * 512 + x;
                            int packed = ByteUtil.getInt(this.byteBuffer, 12 + index * 4);
                            int newIndex = (baseZ + (z / step)) * 512 + (baseX + (x / step));
                            buffer.put(12 + newIndex * 4, ByteUtil.toBytes(packed));
                        }
                    }

                    // finally, save data to disk
                    FileUtil.saveGzip(buffer.array(), filePath);
                } catch (IOException e) {
                    error = e;
                }
            }

            // ensure the file lock closes before throwing any errors
            lock.writeLock().unlock();

            if (error != null) {
                throw new RuntimeException(error);
            }
        }
    }

    @Override
    public void scanData(Region region) {
        this.byteBuffer.clear();

        this.byteBuffer.put(0, ByteUtil.toBytes(0x706C3378)); // pl3x
        this.byteBuffer.put(4, ByteUtil.toBytes(0x6D617001)); // map1
        this.byteBuffer.put(8, ByteUtil.toBytes(getWorld().getMinBuildHeight()));

        super.scanData(region);
    }

    @Override
    public void scanBlock(Region region, Chunk chunk, Chunk.BlockData data, int blockX, int blockZ) {
        boolean fluid = data.getFluidState() != null;

        int y = (fluid ? data.getFluidY() : data.getBlockY()) - getWorld().getMinBuildHeight();

        Block block = (fluid ? data.getFluidState() : data.getBlockState()).getBlock();
        Biome biome = data.getBiome(region, blockX, blockZ);

        // 11111111111111111111111111111111 - 32 bits - (4294967295)
        // 1111111111                       - 10 bits - block (1023)
        //           1111111111             - 10 bits - biome (1023)
        //                     111111111111 - 12 bits - yPos  (4095)
        int packed = ((block.getIndex() & 1023) << 22) | ((biome.index() & 1023) << 12) | (y & 4095);
        int index = (blockZ & 511) * 512 + (blockX & 511);
        this.byteBuffer.put(12 + index * 4, ByteUtil.toBytes(packed));
    }
}
