package net.pl3x.map.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.pl3x.map.PaperPl3xMap;
import net.pl3x.map.configuration.AdvancedConfig;
import net.pl3x.map.coordinate.Coordinate;
import net.pl3x.map.coordinate.RegionCoordinate;
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

public class BukkitWorldListener implements Listener {
    private final PaperPl3xMap plugin;
    private final List<Listener> registeredListeners = new ArrayList<>();

    public BukkitWorldListener(PaperPl3xMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.plugin.getWorldRegistry().register(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        this.plugin.getWorldRegistry().unregister(event.getWorld());
    }

    public void registerEvents() {
        registerEvent(AdvancedConfig.BLOCK_BREAK_EVENT, BlockBreakEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.BLOCK_BURN_EVENT, BlockBurnEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.BLOCK_FADE_EVENT, BlockFadeEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.BLOCK_FORM_EVENT, BlockFormEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.BLOCK_GROW_EVENT, BlockGrowEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.BLOCK_PHYSICS_EVENT, BlockPhysicsEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.BLOCK_PLACE_EVENT, BlockPlaceEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.BLOCK_SPREAD_EVENT, BlockSpreadEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.ENTITY_BLOCK_FORM_EVENT, EntityBlockFormEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.LEAVES_DECAY_EVENT, LeavesDecayEvent.class, this::handleBlockEvent);
        registerEvent(AdvancedConfig.CHUNK_LOAD_EVENT, ChunkLoadEvent.class, this::handleChunkEvent);
        registerEvent(AdvancedConfig.CHUNK_POPULATE_EVENT, ChunkPopulateEvent.class, this::handleChunkEvent);
        registerEvent(AdvancedConfig.PLAYER_JOIN_EVENT, PlayerJoinEvent.class, this::handlePlayerEvent);
        registerEvent(AdvancedConfig.PLAYER_MOVE_EVENT, PlayerMoveEvent.class, this::handlePlayerEvent);
        registerEvent(AdvancedConfig.PLAYER_QUIT_EVENT, PlayerQuitEvent.class, this::handlePlayerEvent);

        registerEvent(AdvancedConfig.BLOCK_EXPLODE_EVENT, BlockExplodeEvent.class,
                e -> markChunk(e.getBlock().getWorld(), e.blockList()));
        registerEvent(AdvancedConfig.BLOCK_FROM_TO_EVENT, BlockFromToEvent.class,
                // this event gets spammed really hard, to the point
                // where checking the highest Y becomes quite expensive.
                // it's better to queue some unnecessary map updates
                // than to cause tps lag if this listener is enabled.
                e -> markChunk(e.getBlock().getLocation(), true));
        registerEvent(AdvancedConfig.BLOCK_PISTON_EXTEND_EVENT, BlockPistonExtendEvent.class,
                e -> markChunk(e.getBlock().getWorld(), e.getBlocks()));
        registerEvent(AdvancedConfig.BLOCK_PISTON_RETRACT_EVENT, BlockPistonRetractEvent.class,
                e -> markChunk(e.getBlock().getWorld(), e.getBlocks()));
        registerEvent(AdvancedConfig.ENTITY_CHANGE_BLOCK_EVENT, EntityChangeBlockEvent.class,
                e -> markChunk(e.getBlock().getLocation()));
        registerEvent(AdvancedConfig.ENTITY_EXPLODE_EVENT, EntityExplodeEvent.class,
                e -> markChunk(e.getEntity().getWorld(), e.blockList()));
        registerEvent(AdvancedConfig.FLUID_LEVEL_CHANGE_EVENT, FluidLevelChangeEvent.class,
                e -> {
                    if (e.getBlock().getBlockData().getMaterial() != e.getNewData().getMaterial()) {
                        markChunk(e.getBlock().getLocation());
                    }
                });
        registerEvent(AdvancedConfig.STRUCTURE_GROW_EVENT, StructureGrowEvent.class,
                e -> markChunk(e.getWorld(), e.getBlocks()));
    }

    public void unregisterEvents() {
        this.registeredListeners.forEach(HandlerList::unregisterAll);
        this.registeredListeners.clear();
    }

    private <E extends Event> void registerEvent(boolean enabled, Class<E> clazz, Consumer<E> consumer) {
        if (!enabled) {
            return;
        }
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
        MapWorld mapWorld = this.plugin.getWorldRegistry().get(loc.getWorld());
        if (mapWorld == null) {
            return;
        }
        markChunk(mapWorld, loc, skipVisibilityCheck);
    }

    private void markChunk(World world, List<Block> blocks) {
        MapWorld mapWorld = this.plugin.getWorldRegistry().get(world);
        if (mapWorld == null) {
            return;
        }
        blocks.forEach(block -> markChunk(mapWorld, block.getLocation(), false));
    }

    private void markChunk(World world, Collection<BlockState> states) {
        MapWorld mapWorld = this.plugin.getWorldRegistry().get(world);
        if (mapWorld == null) {
            return;
        }
        states.forEach(state -> markChunk(mapWorld, state.getLocation(), false));
    }

    private void markChunk(MapWorld mapWorld, Location loc, boolean skipVisibilityCheck) {
        if (skipVisibilityCheck || locationVisible(loc)) {
            mapWorld.addModifiedRegion(new RegionCoordinate(
                    Coordinate.blockToRegion(loc.getBlockX()),
                    Coordinate.blockToRegion(loc.getBlockZ())
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
