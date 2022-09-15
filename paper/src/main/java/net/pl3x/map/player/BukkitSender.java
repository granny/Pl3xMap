package net.pl3x.map.player;

import net.pl3x.map.Key;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.Sender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitSender extends Sender {
    public BukkitSender(@NotNull Key key) {
        super(key);
    }

    public static Sender getSender(CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player player) {
            return Pl3xMap.api().getPlayerRegistry().get(player.getUniqueId());
        }
        return Pl3xMap.api().getConsole();
    }

    public static CommandSender getSender(Sender sender) {
        if (sender instanceof net.pl3x.map.player.Player player) {
            return ((BukkitPlayer) player).getPlayer();
        }
        return Bukkit.getConsoleSender();
    }
}
