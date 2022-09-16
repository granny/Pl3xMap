package net.pl3x.map.command;

import java.util.Objects;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitConsole extends Console {
    private final ConsoleCommandSender console;

    public BukkitConsole() {
        this.console = Bukkit.getConsoleSender();
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        this.console.sendMessage(source, message, type);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        BukkitConsole other = (BukkitConsole) o;
        return getKey() == other.getKey()
                && this.console == other.console;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), this.console);
    }

    @Override
    public String toString() {
        return "BukkitConsole{"
                + "key=" + getKey()
                + ",console=" + this.console.getName()
                + "}";
    }
}
