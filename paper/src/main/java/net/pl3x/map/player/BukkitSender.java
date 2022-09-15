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
        return sender instanceof org.bukkit.entity.Player player ? Pl3xMap.api().getPlayerRegistry().get(player.getUniqueId()) : Pl3xMap.api().getConsole();
    }

    public static CommandSender getSender(Sender sender) {
        return sender instanceof net.pl3x.map.player.Player player ? ((BukkitPlayer) player).getPlayer() : Bukkit.getConsoleSender();
    }
}
