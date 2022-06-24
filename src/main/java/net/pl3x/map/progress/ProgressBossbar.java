package net.pl3x.map.progress;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.world.MapWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ProgressBossbar {
    private static final String NAME = "<gold>Map Render of <grey><world></grey>: <red><percent></red>%";

    private final MapWorld mapWorld;
    private final BossBar bossbar;

    private final Set<Audience> players = new HashSet<>();

    public ProgressBossbar(MapWorld mapWorld) {
        this.mapWorld = mapWorld;
        this.bossbar = BossBar.bossBar(Component.empty(), 0F, BossBar.Color.RED, BossBar.Overlay.NOTCHED_20);

        update(0F);
    }

    // called for progress starter and any player that uses /status command
    public void show(Player player) {
        this.players.add(player);
        player.showBossBar(this.bossbar);
    }

    // called when a listening player quits the server
    public boolean hide(Player player) {
        boolean result = this.players.remove(player);
        if (result) {
            player.hideBossBar(this.bossbar);
        }
        return result;
    }

    // called shortly after the progress is finished
    public void hideAll() {
        Audience.audience(this.players).hideBossBar(this.bossbar);
    }

    // called every 20 ticks to update progress bar and name
    public void update(float percent) {
        this.bossbar.progress(Math.min(Math.max(Mathf.inverseLerp(0F, 100F, percent), 0), 1));
        this.bossbar.name(Lang.parse(NAME,
                Placeholder.unparsed("world", this.mapWorld.getName()),
                Placeholder.unparsed("percent", String.format("%.2f", percent))
        ));
    }

    // remove bossbar from audience after a few seconds
    public void finish() {
        // just for fun, lets flash the colors for a bit before removing
        AtomicInteger i = new AtomicInteger(0);
        AtomicInteger j = new AtomicInteger(0);
        new BukkitRunnable() {
            public void run() {
                try {
                    // cycle through colors
                    bossbar.color(BossBar.Color.values()[i.getAndIncrement()]);
                } catch (Throwable t) {
                    // 7 (colors) * 3 (repeats) * 5 (period) = 105 ticks (~5 seconds animation)
                    if (j.getAndIncrement() < 3) {
                        i.set(0);
                        return;
                    }
                    // out of colors, remove bossbar from players
                    hideAll();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Pl3xMap.getInstance(), 0, 5);
    }
}
