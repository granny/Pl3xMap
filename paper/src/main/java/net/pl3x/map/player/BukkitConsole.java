package net.pl3x.map.player;

import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.pl3x.map.command.Console;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitConsole extends Console {
    private final ConsoleCommandSender console;

    public BukkitConsole() {
        this.console = Bukkit.getConsoleSender();
    }

    // Rest of this implements Audience methods

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        this.console.sendMessage(source, message, type);
    }
}
