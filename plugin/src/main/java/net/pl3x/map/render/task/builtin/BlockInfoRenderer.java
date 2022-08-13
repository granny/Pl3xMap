package net.pl3x.map.render.task.builtin;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.coordinate.RegionCoordinate;
import net.pl3x.map.api.image.Image;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.render.task.ScanTask;
import net.pl3x.map.util.ByteUtil;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.util.Palette;
import net.pl3x.map.world.MapWorld;

public class BlockInfoRenderer extends Renderer {
    private static final Map<Path, ReadWriteLock> FILE_LOCKS = new ConcurrentHashMap<>();

    private ByteBuffer byteBuffer;

    public BlockInfoRenderer(String name, ScanTask scanTask) {
        super(name, scanTask);
    }

    @Override
    public void allocateData() {
        this.byteBuffer = ByteBuffer.allocate(Image.SIZE * Image.SIZE * 4 + 12);
        Path path = getScanTask().getWorld().getWorldTilesDir().resolve(String.format(Image.DIR_PATH, 0, getName()));
        try {
            if (Files.exists(path) && Files.size(path) > 0) {
                FileUtil.readGzip(path, this.byteBuffer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveData() {
        MapWorld mapWorld = getScanTask().getWorld();
        Path tilesDir = mapWorld.getWorldTilesDir();
        for (int zoom = 0; zoom <= mapWorld.getConfig().ZOOM_MAX_OUT; zoom++) {
            Path dirPath = tilesDir.resolve(String.format(Image.DIR_PATH, zoom, getName()));

            // create directories if they don't exist
            FileUtil.createDirs(dirPath);

            // calculate correct sizes for this zoom level
            int step = Mathf.pow2(zoom);
            int size = Image.SIZE / step;

            Path filePath = dirPath.resolve(String.format(Image.FILE_PATH,
                    Mth.floor((double) getRegion().getRegionX() / step),
                    Mth.floor((double) getRegion().getRegionZ() / step),
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
                        FileUtil.readGzip(filePath, buffer);
                    }

                    // copy header
                    for (int i = 0; i < 12; i++) {
                        buffer.put(i, this.byteBuffer.get(i));
                    }

                    // write new data
                    int baseX = (getRegion().getRegionX() * size) & (Image.SIZE - 1);
                    int baseZ = (getRegion().getRegionZ() * size) & (Image.SIZE - 1);
                    for (int x = 0; x < Image.SIZE; x += step) {
                        for (int z = 0; z < Image.SIZE; z += step) {
                            int index = z * Image.SIZE + x;
                            int packed = ByteUtil.getInt(this.byteBuffer, 12 + index * 4);
                            int newIndex = (baseZ + (z / step)) * Image.SIZE + (baseX + (x / step));
                            buffer.put(12 + newIndex * 4, ByteUtil.toBytes(packed));
                        }
                    }

                    // finally, save data to disk
                    FileUtil.saveGzip(buffer.array(), filePath);
                } catch (IOException e) {
                    error = e;
                }
            }

            if (error != null) {
                throw new RuntimeException(error);
            }

            lock.writeLock().unlock();
        }
    }

    @Override
    public void scanData(RegionCoordinate region, ScanData.Data scanData) {
        int minY = getWorld().getLevel().getMinBuildHeight();

        this.byteBuffer.clear();

        this.byteBuffer.put(0, ByteUtil.toBytes(0x706C3378)); // pl3x
        this.byteBuffer.put(4, ByteUtil.toBytes(0x6D617001)); // map1
        this.byteBuffer.put(8, ByteUtil.toBytes(minY));

        Palette<Block> blockPalette = Pl3xMap.api().getPaletteManager().getBlockPalette();
        Palette<Biome> biomePalette = Pl3xMap.api().getPaletteManager().getBiomePalette(getWorld());

        for (ScanData data : scanData.values()) {
            boolean fluid = data.getFluidPos() != null;

            Block block = (fluid ? data.getFluidState() : data.getBlockState()).getBlock();
            Biome biome = fluid ? data.getFluidBiome() : data.getBlockBiome();
            BlockPos pos = fluid ? data.getFluidPos() : data.getBlockPos();

            int blockIndex = blockPalette.get(block).getIndex();
            int biomeIndex = biomePalette.get(biome).getIndex();
            int yPos = pos.getY() - minY; // ensure bottom starts at 0

            // 11111111111111111111111111111111 - 32 bits - (4294967295)
            // 1111111111                       - 10 bits - block (1023)
            //           1111111111             - 10 bits - biome (1023)
            //                     111111111111 - 12 bits - yPos  (4095)
            int packed = ((blockIndex & 1023) << 22) | ((biomeIndex & 1023) << 12) | (yPos & 4095);
            int index = (pos.getZ() & Image.SIZE - 1) * Image.SIZE + (pos.getX() & Image.SIZE - 1);
            this.byteBuffer.put(12 + index * 4, ByteUtil.toBytes(packed));
        }
    }
}
