package net.pl3x.map.progress;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.render.task.AbstractRender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Progress extends BukkitRunnable {
    private final AbstractRender render;
    private final CPSTracker cpsTracker = new CPSTracker();

    private final AtomicLong processedChunks = new AtomicLong(0);
    private final AtomicLong processedRegions = new AtomicLong(0);

    private long prevProcessedChunks = 0;
    private long totalChunks;
    private long totalRegions;
    private float percent;
    private double cps;

    private final Set<Audience> audience = new HashSet<>();
    private final ProgressBossbar bossbar;

    public Progress(AbstractRender render) {
        this.render = render;
        this.bossbar = new ProgressBossbar(render.getWorld());
    }

    public void show(Audience audience) {
        this.audience.add(audience);
    }

    public boolean hide(Audience audience) {
        return this.audience.remove(audience);
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

    public long getTotalChunks() {
        return this.totalChunks;
    }

    public long getTotalRegions() {
        return this.totalRegions;
    }

    public void setTotalRegions(long totalRegions) {
        this.totalRegions = totalRegions;
        this.totalChunks = totalRegions * 32L * 32L;
    }

    public final AtomicLong getProcessedChunks() {
        return this.processedChunks;
    }

    public final AtomicLong getProcessedRegions() {
        return this.processedRegions;
    }

    public void finish() {
        cancel();
        if (this.render.getWorld().hasActiveRender()) {
            this.render.getWorld().finishRender();
            getBossbar().finish();
        } else {
            getBossbar().hideAll();
        }
    }

    @Override
    public void run() {
        long processedChunks = getProcessedChunks().get();
        this.cpsTracker.add(processedChunks - this.prevProcessedChunks);
        this.prevProcessedChunks = processedChunks;
        this.percent = ((float) processedChunks / (float) getTotalChunks()) * 100.0F;
        this.cps = this.cpsTracker.average();

        // show progress to listeners
        Component component = Lang.parse(
                "Progress: <processed_chunks>/<total_chunks> (<percent>) <gold><cps> cps</gold>",
                Placeholder.parsed("processed_chunks", Long.toString(processedChunks)),
                Placeholder.parsed("total_chunks", Long.toString(getTotalChunks())),
                Placeholder.parsed("percent", String.format("%.2f%%", getPercent())),
                Placeholder.parsed("cps", String.format("%.2f", getCPS()))
        );
        for (Audience audience : this.audience) {
            Lang.send(audience, component);
        }

        // show to player bossbars
        getBossbar().update(this.percent);

        // check if finished
        if (this.processedRegions.get() >= this.totalRegions) {
            finish();
        }
    }

    public static String formatMilliseconds(long time) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(time);
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(time) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(time) % 60;
        if (hrs > 0) {
            return String.format("%dh %dm %ds", hrs, min, sec);
        } else if (min > 0) {
            return String.format("%dm %ds", min, sec);
        } else {
            return String.format("%ds", sec);
        }
    }
}
