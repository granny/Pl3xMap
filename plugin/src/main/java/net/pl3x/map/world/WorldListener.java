package net.pl3x.map.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Advanced;
import net.pl3x.map.render.job.iterator.coordinate.ChunkCoordinate;
import net.pl3x.map.render.job.iterator.coordinate.Coordinate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.EventExecutor;

public class WorldListener implements Listener {
    private final Pl3xMap plugin;
    private final List<Listener> registeredListeners = new ArrayList<>();

    public WorldListener(Pl3xMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        WorldManager.INSTANCE.loadWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        WorldManager.INSTANCE.unloadWorld(event.getWorld());
    }

    public void registerEvents() {
        if (Advanced.BLOCK_BREAK_EVENT) registerEvent(BlockBreakEvent.class, this::handleBlockEvent);
        if (Advanced.BLOCK_BURN_EVENT) registerEvent(BlockBurnEvent.class, this::handleBlockEvent);
        if (Advanced.BLOCK_FADE_EVENT) registerEvent(BlockFadeEvent.class, this::handleBlockEvent);
        if (Advanced.BLOCK_FORM_EVENT) registerEvent(BlockFormEvent.class, this::handleBlockEvent);
        if (Advanced.BLOCK_GROW_EVENT) registerEvent(BlockGrowEvent.class, this::handleBlockEvent);
        if (Advanced.BLOCK_PHYSICS_EVENT) registerEvent(BlockPhysicsEvent.class, this::handleBlockEvent);
        if (Advanced.BLOCK_PLACE_EVENT) registerEvent(BlockPlaceEvent.class, this::handleBlockEvent);
        if (Advanced.BLOCK_SPREAD_EVENT) registerEvent(BlockSpreadEvent.class, this::handleBlockEvent);
        if (Advanced.CHUNK_LOAD_EVENT) registerEvent(ChunkLoadEvent.class, this::handleChunkEvent);
        if (Advanced.CHUNK_POPULATE_EVENT) registerEvent(ChunkPopulateEvent.class, this::handleChunkEvent);
        if (Advanced.ENTITY_BLOCK_FORM_EVENT) registerEvent(EntityBlockFormEvent.class, this::handleBlockEvent);
        if (Advanced.LEAVES_DECAY_EVENT) registerEvent(LeavesDecayEvent.class, this::handleBlockEvent);
        if (Advanced.PLAYER_JOIN_EVENT) registerEvent(PlayerJoinEvent.class, this::handlePlayerEvent);
        if (Advanced.PLAYER_MOVE_EVENT) registerEvent(PlayerMoveEvent.class, this::handlePlayerEvent);
        if (Advanced.PLAYER_QUIT_EVENT) registerEvent(PlayerQuitEvent.class, this::handlePlayerEvent);

        if (Advanced.BLOCK_EXPLODE_EVENT) registerEvent(BlockExplodeEvent.class,
                e -> markChunk(e.getBlock().getWorld(), e.blockList()));
        if (Advanced.BLOCK_FROM_TO_EVENT) registerEvent(BlockFromToEvent.class,
                // this event gets spammed really hard, to the point
                // where checking the highest Y becomes quite expensive.
                // it's better to queue some unnecessary map updates
                // than to cause tps lag if this listener is enabled.
                e -> markChunk(e.getBlock().getLocation(), true));
        if (Advanced.BLOCK_PISTON_EXTEND_EVENT) registerEvent(BlockPistonExtendEvent.class,
                e -> markChunk(e.getBlock().getWorld(), e.getBlocks()));
        if (Advanced.BLOCK_PISTON_RETRACT_EVENT) registerEvent(BlockPistonRetractEvent.class,
                e -> markChunk(e.getBlock().getWorld(), e.getBlocks()));
        if (Advanced.ENTITY_CHANGE_BLOCK_EVENT) registerEvent(EntityChangeBlockEvent.class,
                e -> markChunk(e.getBlock().getLocation()));
        if (Advanced.ENTITY_EXPLODE_EVENT) registerEvent(EntityExplodeEvent.class,
                e -> markChunk(e.getEntity().getWorld(), e.blockList()));
        if (Advanced.FLUID_LEVEL_CHANGE_EVENT) registerEvent(FluidLevelChangeEvent.class,
                e -> {
                    if (e.getBlock().getBlockData().getMaterial() != e.getNewData().getMaterial()) {
                        markChunk(e.getBlock().getLocation());
                    }
                });
        if (Advanced.STRUCTURE_GROW_EVENT) registerEvent(StructureGrowEvent.class,
                e -> markChunk(e.getWorld(), e.getBlocks()));
    }

    public void unregisterEvents() {
        this.registeredListeners.forEach(HandlerList::unregisterAll);
        this.registeredListeners.clear();
    }

    private <E extends Event> void registerEvent(Class<E> clazz, Consumer<E> consumer) {
        Listener listener = new BlankListener();
        this.registeredListeners.add(listener);
        Bukkit.getPluginManager().registerEvent(clazz, listener, EventPriority.MONITOR, createExecutor(clazz, consumer), this.plugin, true);
    }

    private void handleBlockEvent(BlockEvent blockEvent) {
        this.markChunk(blockEvent.getBlock().getLocation());
    }

    private void handleChunkEvent(ChunkEvent event) {
        this.markChunk(toLoc(event.getChunk()), true);
    }

    private void handlePlayerEvent(PlayerEvent playerEvent) {
        this.markChunk(playerEvent.getPlayer().getLocation(), true);
    }

    private void markChunk(Location loc) {
        markChunk(loc, false);
    }

    private void markChunk(Location loc, boolean skipVisibilityCheck) {
        MapWorld mapWorld = WorldManager.INSTANCE.getMapWorld(loc.getWorld());
        if (mapWorld == null) {
            return;
        }
        markChunk(mapWorld, loc, skipVisibilityCheck);
    }

    private void markChunk(World world, List<Block> blocks) {
        MapWorld mapWorld = WorldManager.INSTANCE.getMapWorld(world);
        if (mapWorld == null) {
            return;
        }
        blocks.forEach(block -> markChunk(mapWorld, block.getLocation(), false));
    }

    private void markChunk(World world, Collection<BlockState> states) {
        MapWorld mapWorld = WorldManager.INSTANCE.getMapWorld(world);
        if (mapWorld == null) {
            return;
        }
        states.forEach(state -> markChunk(mapWorld, state.getLocation(), false));
    }

    private void markChunk(MapWorld mapWorld, Location loc, boolean skipVisibilityCheck) {
        if (skipVisibilityCheck || locationVisible(loc)) {
            mapWorld.addModifiedChunk(new ChunkCoordinate(
                    Coordinate.blockToChunk(loc.getBlockX()),
                    Coordinate.blockToChunk(loc.getBlockZ())
            ));
        }
    }

    private static boolean locationVisible(Location loc) {
        return loc.getWorld().hasCeiling() || loc.getY() >= loc.getWorld().getHighestBlockYAt(loc) - 10;
    }

    private Location toLoc(Chunk chunk) {
        return new Location(chunk.getWorld(), Coordinate.chunkToBlock(chunk.getX()), 0, Coordinate.chunkToBlock(chunk.getZ()));
    }

    private static <E extends Event> EventExecutor createExecutor(Class<E> clazz, Consumer<E> consumer) {
        return (listener, event) -> {
            if (!clazz.isAssignableFrom(event.getClass())) {
                return;
            }
            consumer.accept(clazz.cast(event));
        };
    }

    public static class BlankListener implements Listener {
    }
}
