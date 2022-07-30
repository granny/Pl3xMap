package net.pl3x.map.render.task.builtin;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.pl3x.map.PaletteManager;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.render.task.ScanTask;
import net.pl3x.map.util.ByteUtil;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.util.Palette;

public class BlockInfo extends Renderer {
    private ByteBuffer byteBuffer;

    public BlockInfo(String name, ScanTask scanTask) {
        super(name, scanTask);
    }

    @Override
    public void allocateData() {
        this.byteBuffer = ByteBuffer.allocate(Image.SIZE * Image.SIZE * 4 + 20);
    }

    @Override
    public void saveData() {
        Path tilesDir = getScanTask().getWorld().getWorldTilesDir();
        // TODO - make work for higher zoom levels?
        Path dir = tilesDir.resolve(String.format(Image.DIR_PATH, 0, getName()));
        String filename = String.format(Image.FILE_PATH, getRegion().getRegionX(), getRegion().getRegionZ(), "pl3xmap.gz");
        try {
            FileUtil.saveGzip(this.byteBuffer.array(), dir.resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void scanData(RegionCoordinate region, ScanData.Data scanData) {
        int minY = getWorld().getLevel().getMinBuildHeight();

        this.byteBuffer.clear();

        this.byteBuffer.put(0, ByteUtil.toBytes(0x706C3378)); // pl3x
        this.byteBuffer.put(4, ByteUtil.toBytes(0x6D617001)); // map1
        this.byteBuffer.put(8, ByteUtil.toBytes(region.getRegionX()));
        this.byteBuffer.put(12, ByteUtil.toBytes(region.getRegionZ()));
        this.byteBuffer.put(16, ByteUtil.toBytes(minY));

        Palette<Block> blockPalette = PaletteManager.INSTANCE.getBlockPalette();
        Palette<Biome> biomePalette = PaletteManager.INSTANCE.getBiomePalette(getWorld());

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
            this.byteBuffer.put(20 + index * 4, ByteUtil.toBytes(packed));
        }
    }
}
