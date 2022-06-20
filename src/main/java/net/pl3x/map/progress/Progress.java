package net.pl3x.map.progress;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.render.task.ThreadManager;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
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
    private int totalRegions;
    private float percent;
    private double cps;

    private final Set<CommandSender> showProgressTo = new HashSet<>();
    private final ProgressBossbar bossbar;

    public Progress(AbstractRender render) {
        this.render = render;
        this.bossbar = new ProgressBossbar(render.getWorld());
    }

    public void show(CommandSender sender) {
        this.showProgressTo.add(sender);
    }

    public boolean hide(CommandSender sender) {
        return this.showProgressTo.remove(sender);
    }

    public ProgressBossbar getBossbar() {
        return this.bossbar;
    }

    public float getPercent() {
        return this.percent;
    }

    public double getCPS() {
        return this.cps;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public int getTotalRegions() {
        return this.totalRegions;
    }

    public void setTotalRegions(int totalRegions) {
        this.totalRegions = totalRegions;
        this.totalChunks = totalRegions * 32 * 32;
    }

    public final AtomicInteger getProcessedChunks() {
        return this.processedChunks;
    }

    public final AtomicInteger getProcessedRegions() {
        return this.processedRegions;
    }

    @Override
    public void run() {
        int processedChunks = getProcessedChunks().get();
        this.cpsTracker.add(processedChunks - this.prevProcessedChunks);
        this.prevProcessedChunks = processedChunks;
        this.percent = ((float) processedChunks / (float) getTotalChunks()) * 100.0F;
        this.cps = this.cpsTracker.average();

        // show progress to listeners
        Component component = MiniMessage.miniMessage().deserialize(
                String.format("Progress: %d/%d (%s) %s (%d,%d)",
                        processedChunks,
                        getTotalChunks(),
                        String.format("%.2f%%", getPercent()),
                        String.format("<gold>%.2f", getCPS()) + " cps</gold>",
                        ((ThreadPoolExecutor) ThreadManager.INSTANCE.getSaveExecutor()).getQueue().stream().filter(t -> !((FutureTask<?>) t).isDone()).count(),
                        ((ThreadPoolExecutor) ThreadManager.INSTANCE.getRenderExecutor()).getQueue().stream().filter(t -> !((FutureTask<?>) t).isDone()).count()
                )
        );
        for (CommandSender sender : this.showProgressTo) {
            Lang.send(sender, component);
        }

        // show to player bossbars
        this.bossbar.update(this.percent);

        // check if finished
        if (this.processedRegions.get() >= this.totalRegions) {
            cancel();
            if (this.render.getWorld().hasActiveRender()) {
                Logger.info("Finished rendering " + this.render.getWorld().getName());
                this.render.getWorld().finishRender();
                this.bossbar.finish();
            } else {
                this.bossbar.clear();
            }
        }
    }
}
