package net.pl3x.map.player;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.render.job.progress.Progress;
import org.jetbrains.annotations.Nullable;

public interface PlayerListener {
    default void onJoin(@Nullable Player player) {
        if (player == null) {
            return;
        }
        if (player.isHidden()) {
            player.setHidden(true, false);
        }
        new Thread(() -> new PlayerTexture(player)).start();
    }

    default void onQuit(@Nullable Player player) {
        if (player == null) {
            return;
        }
        Pl3xMap.api().getWorldRegistry().entries().forEach((key, world) -> {
            if (world.hasActiveRender()) {
                Progress progress = world.getActiveRender().getProgress();
                progress.hideChat(player);
                progress.getBossbar().hide(player);
            }
        });
    }
}
