package net.pl3x.map.command;

import net.pl3x.map.Pl3xMap;

public abstract class Pl3xMapCommand {
    private final Pl3xMap plugin;
    private final CommandManager commandManager;

    protected Pl3xMapCommand(Pl3xMap plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    public Pl3xMap getPlugin() {
        return this.plugin;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public abstract void register();
}
