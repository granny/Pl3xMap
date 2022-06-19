package net.pl3x.map.render;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.render.task.ThreadManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class Progress extends BukkitRunnable {
    private final AbstractRender render;
    private final CPSTracker cpsTracker = new CPSTracker();

    private final AtomicInteger processedChunks = new AtomicInteger(0);
    private final AtomicInteger processedRegions = new AtomicInteger(0);

    private int prevProcessedChunks = 0;
    private int totalChunks;
    private int processedRegionsCount;
    private int totalRegions;

    private final boolean logProgress;

    private final Set<UUID> playersToShow = new HashSet<>();
    private final Set<UUID> bossbarsToShow = new HashSet<>();

    public Progress(AbstractRender render) {
        this.render = render;
        this.logProgress = true;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public final AtomicInteger getProcessedChunks() {
        return this.processedChunks;
    }

    public int getTotalRegions() {
        return this.totalRegions;
    }

    public void setTotalRegions(int totalRegions) {
        this.totalRegions = totalRegions;
        this.totalChunks = totalRegions * 32 * 32;
    }

    public final AtomicInteger getProcessedRegions() {
        return this.processedRegions;
    }

    @Override
    public void run() {
        int processedChunks = this.processedChunks.get();
        this.processedRegionsCount = this.processedRegions.get();

        int diff = processedChunks - this.prevProcessedChunks;
        this.prevProcessedChunks = processedChunks;

        this.cpsTracker.add(diff);

        ThreadPoolExecutor saveExecutor = (ThreadPoolExecutor) ThreadManager.INSTANCE.getSaveExecutor();
        ThreadPoolExecutor renderExecutor = (ThreadPoolExecutor) ThreadManager.INSTANCE.getRenderExecutor();

        long saveExecutorCount = saveExecutor.getQueue().stream().filter(t -> !((FutureTask<?>) t).isDone()).count();
        long renderExecutorCount = renderExecutor.getQueue().stream().filter(t -> !((FutureTask<?>) t).isDone()).count();

        String percentComplete = String.format("%.2f%%", ((float) processedChunks / (float) this.totalChunks) * 100.0F);
        String cpsStr = String.format("<gold>%.2f", this.cpsTracker.average()) + " cps</gold>";

        String message = String.format("%d/%d (%s) %s (%d,%d)", processedChunks, this.totalChunks, percentComplete, cpsStr, saveExecutorCount, renderExecutorCount);

        showLogger(message);
        showPlayers(message);
    }

    public void showLogger(String message) {
        if (this.processedRegionsCount < this.totalRegions) {
            if (this.logProgress) {
                Logger.info("Progress: " + message);
            }
        } else {
            Logger.info("<dark_aqua>Finished: " + message);
            cancel();
        }
    }

    public void addPlayerToShow(Player player) {
        this.playersToShow.add(player.getUniqueId());
    }

    public void removePlayerToShow(Player player) {
        this.playersToShow.remove(player.getUniqueId());
    }

    public void showPlayers(String message) {
        if (this.processedRegionsCount < this.totalRegions) {
            message = "Progress: " + message;
        } else {
            message = "<dark_aqua>Finished: " + message;
        }
        Component component = MiniMessage.miniMessage().deserialize(message);
        for (UUID uuid : this.playersToShow) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            Lang.send(player, component);
        }
    }

    public void showBossbars() {
        // todo
    }

    public static class CPSTracker {
        final int[] avg = new int[15];
        int index = 0;

        public void add(int val) {
            this.index++;
            if (this.index == 15) {
                this.index = 0;
            }
            this.avg[this.index] = val;
        }

        public double average() {
            return Arrays.stream(this.avg).filter(i -> i != 0).average().orElse(0.00D);
        }
    }
}
