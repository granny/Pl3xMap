package net.pl3x.map.core.markers.area;

import java.util.Map;
import net.pl3x.map.core.world.World;

public interface Area {
    boolean containsBlock(int blockX, int blockZ);

    boolean containsChunk(int chunkX, int chunkZ);

    boolean containsRegion(int regionX, int regionZ);

    Map<String, Object> serialize();

    static Area deserialize(World world, Map<String, Object> map) {
        return switch (String.valueOf(map.get("type"))) {
            case "circle" -> Circle.deserialize(map);
            case "rectangle" -> Rectangle.deserialize(map);
            case "world-border" -> Border.deserialize(world, map);
            default -> throw new IllegalArgumentException("Unknown area type");
        };
    }
}
