package net.pl3x.map.command;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public abstract class BaseCommand implements TabExecutor {
    private final Map<String, BaseCommand> subCommands = new TreeMap<>();

    private final Pl3xMap plugin;
    private final String name;
    private final String description;
    private final String permission;
    private final String usage;

    public BaseCommand(Pl3xMap plugin, String name, String description, String permission, String usage) {
        this.plugin = plugin;
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.usage = usage;
    }

    public Pl3xMap getPlugin() {
        return this.plugin;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getUsage() {
        return this.usage;
    }

    public void showUsage(CommandSender sender) {
        Lang.send(sender, Lang.COMMAND_BASE_USAGE,
                Placeholder.parsed("command", getName()),
                Placeholder.parsed("description", getDescription()),
                Placeholder.parsed("usage", getUsage()));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] strings) {
        return handleTabComplete(sender, command, new LinkedList<>(Arrays.asList(strings)));
    }

    protected List<String> handleTabComplete(CommandSender sender, Command command, LinkedList<String> args) {
        if (args.size() > 1) {
            BaseCommand subCmd = this.subCommands.get(args.pop().toLowerCase());
            if (subCmd != null && sender.hasPermission(subCmd.getPermission())) {
                return subCmd.handleTabComplete(sender, command, args);
            }
        } else if (args.size() == 1) {
            String arg = args.peek().toLowerCase();
            return this.subCommands.entrySet().stream()
                    .filter(cmdPair -> cmdPair.getKey().startsWith(arg))
                    .filter(cmdPair -> sender.hasPermission(cmdPair.getValue().getPermission()))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            return handleCommand(sender, command, new LinkedList<>(Arrays.asList(args)));
        } catch (CommandException e) {
            if (e.getMessage() == null || e.getMessage().isBlank()) {
                Lang.send(sender, Lang.ERROR_UNKNOWN_ERROR);
            } else {
                Lang.send(sender, e.getMessage());
            }
            return true;
        }
    }

    protected boolean handleCommand(CommandSender sender, Command command, LinkedList<String> args) throws CommandException {
        if (args != null && args.size() > 0) {
            String cmd = args.pop().toLowerCase();
            BaseCommand subCmd = this.subCommands.get(cmd);
            if (subCmd != null) {
                String arg = args.peek();
                if (arg != null && arg.equals("?")) {
                    subCmd.showUsage(sender);
                    return true;
                }
                if (!sender.hasPermission(subCmd.getPermission())) {
                    sender.sendMessage(Bukkit.getPermissionMessage());
                    return true;
                }
                return subCmd.handleCommand(sender, command, args);
            }
            if (!cmd.equals("?")) {
                Lang.send(sender, Lang.ERROR_UNKNOWN_SUBCOMMAND);
            }
        }
        showSubCommands(sender);
        return true;
    }

    protected void showSubCommands(CommandSender sender) {
        Lang.send(sender, Lang.COMMAND_BASE_SUBCOMMANDS_TITLE);
        Lang.send(sender, false, Lang.COMMAND_BASE_SUBCOMMANDS_FULL_COMMAND,
                Placeholder.parsed("command", this.getName()));
        boolean hasSubCmds = false;
        int i = 1;
        Collection<BaseCommand> subCmds = this.subCommands.values();
        for (BaseCommand subCmd : subCmds) {
            if (sender.hasPermission(subCmd.getPermission())) {
                Lang.send(sender, false, Lang.COMMAND_BASE_SUBCOMMANDS_ENTRY,
                        Placeholder.parsed("prefix", i == subCmds.size() ?
                                Lang.COMMAND_BASE_SUBCOMMANDS_ENTRY_PREFIX_LAST :
                                Lang.COMMAND_BASE_SUBCOMMANDS_ENTRY_PREFIX),
                        Placeholder.parsed("command", subCmd.getName()),
                        Placeholder.parsed("description", subCmd.getDescription()));
                hasSubCmds = true;
            }
            i++;
        }
        if (!hasSubCmds) {
            Lang.send(sender, Bukkit.getPermissionMessage());
        }
    }

    protected void registerSubcommand(BaseCommand subCmd) {
        this.subCommands.put(subCmd.getName().toLowerCase(), subCmd);
    }

    protected Player getPlayer(CommandSender sender, LinkedList<String> args, String targetPerm) {
        String target = args == null || args.size() < 1 ? null : args.pop();
        if (target == null) {
            if (sender instanceof Player player) {
                return player;
            }
            throw new CommandException(Lang.ERROR_MUST_SPECIFY_PLAYER);
        }
        if (targetPerm != null && !sender.hasPermission(targetPerm)) {
            throw new CommandException(Bukkit.getPermissionMessage());
        }
        Player player = Bukkit.getPlayer(target);
        if (player == null) {
            throw new CommandException(Lang.ERROR_NO_SUCH_PLAYER);
        }
        return player;
    }

    public MapWorld getMapWorld(CommandSender sender, LinkedList<String> args) {
        String target = args == null || args.size() < 1 ? null : args.pop();
        World world = null;
        if (target == null) {
            if (!(sender instanceof Player player)) {
                throw new CommandException(Lang.ERROR_MUST_SPECIFY_WORLD);
            }
            world = player.getWorld();
        }
        if (world == null) {
            world = Bukkit.getWorld(target);
            if (world == null) {
                throw new CommandException(Lang.ERROR_NO_SUCH_WORLD);
            }
        }
        MapWorld mapWorld = getPlugin().getWorldManager().getMapWorld(world);
        if (mapWorld == null) {
            throw new CommandException(Lang.ERROR_WORLD_DISABLED);
        }
        return mapWorld;
    }

    public List<String> tabPlayers(String target) {
        if (target == null || target.isBlank()) {
            return Collections.emptyList();
        }
        String lower = target.toUpperCase(Locale.ROOT);
        return Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(lower))
                .collect(Collectors.toList());
    }

    public List<String> tabMapWorlds(String target) {
        String lower = target.toLowerCase(Locale.ROOT);
        return getPlugin().getWorldManager().getMapWorlds().stream()
                .map(MapWorld::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(lower))
                .collect(Collectors.toList());
    }
}
