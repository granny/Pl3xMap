package net.pl3x.map.command;


import cloud.commandframework.minecraft.extras.RichDescription;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.configuration.Lang;

/**
 * Represents a Pl3xMap command.
 */
public abstract class Pl3xMapCommand {
    private final CommandHandler handler;

    protected Pl3xMapCommand(CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Get the command handler.
     *
     * @return command handler
     */
    public CommandHandler getHandler() {
        return this.handler;
    }

    /**
     * Register subcommand.
     */
    public abstract void register();

    /**
     * Create a command description.
     *
     * @param description  description of command
     * @param placeholders placeholders
     * @return rich description
     */
    protected static RichDescription description(String description, TagResolver.Single... placeholders) {
        return RichDescription.of(Lang.parse(description, placeholders));
    }
}
