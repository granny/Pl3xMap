package net.pl3x.map.render.job.progress;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.util.Mathf;

public class ProgressBossbar {
    private final Progress progress;
    private final BossBar bossbar;

    private final Set<Audience> players = new HashSet<>();

    public ProgressBossbar(Progress progress) {
        this.progress = progress;
        this.bossbar = BossBar.bossBar(Component.empty(), 0F, BossBar.Color.RED, BossBar.Overlay.NOTCHED_20);

        update();
    }

    // called for progress starter and any player that uses /status command
    public void show(Audience audience) {
        this.players.add(audience);
        audience.showBossBar(this.bossbar);
    }

    // called when a listening player quits the server
    public boolean hide(Audience audience) {
        boolean result = this.players.remove(audience);
        if (result) {
            audience.hideBossBar(this.bossbar);
        }
        return result;
    }

    // called shortly after the progress is finished
    public void hideAll() {
        Collections.unmodifiableSet(this.players).forEach(this::hide);
    }

    // called every 20 ticks to update progress bar and name
    public void update() {
        this.bossbar.progress(Math.min(Math.max(Mathf.inverseLerp(0F, 100F, this.progress.getPercent()), 0), 1));
        this.bossbar.name(Lang.parse(Lang.PROGRESS_BOSSBAR,
                Placeholder.unparsed("world", this.progress.getRender().getMapWorld().getWorld().getName()),
                Placeholder.unparsed("percent", String.format("%.2f", this.progress.getPercent())),
                Placeholder.unparsed("eta", this.progress.getETA())
        ));
    }

    // remove bossbar from audience after a few seconds
    public void finish() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                hideAll();
            }
        }, 5000);
    }
}
