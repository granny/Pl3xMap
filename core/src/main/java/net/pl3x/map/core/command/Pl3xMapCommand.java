package net.pl3x.map.core.command;

import cloud.commandframework.minecraft.extras.RichDescription;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a Pl3xMap command.
 */
public abstract class Pl3xMapCommand {
    private final CommandHandler handler;

    protected Pl3xMapCommand(@NonNull CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Get the command handler.
     *
     * @return command handler
     */
    public @NonNull CommandHandler getHandler() {
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
    protected static @NonNull RichDescription description(@NonNull String description, @NonNull TagResolver.@NonNull Single... placeholders) {
        return RichDescription.of(Lang.parse(description, placeholders));
    }
}
