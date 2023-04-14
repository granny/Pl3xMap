package net.pl3x.map.core.registry;

import javax.management.openmbean.KeyAlreadyExistsException;
import net.pl3x.map.core.world.Block;

public class BlockRegistry extends Registry<Block> {
    public Block register(String id, int color) {
        Block block = getOrDefault(id, null);
        if (block != null) {
            if (block.getKey().equals("minecraft:air")) {
                return block;
            }
            throw new KeyAlreadyExistsException("Block already registered: " + id);
        }
        return register(id, new Block(id, color));
    }

    @Override
    public Block get(String id) {
        return getOrDefault(id, Block.AIR);
    }
}
