/*
 * MIT License
 *
 * Copyright (c) 2023 Cloud Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cloud.commandframework.forge;

import cloud.commandframework.CommandTree;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.permission.PredicatePermission;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ClientCommandSourceStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * A command manager for registering client-side commands.
 *
 * <p>All commands should be registered within mod initializers. Any registrations occurring after the first call to
 * {@link net.minecraftforge.client.event.RegisterClientCommandsEvent} will be considered <em>unsafe</em>, and will only be permitted when the unsafe
 * registration manager option is enabled.</p>
 *
 * @param <C> the command sender type
 */
public final class ForgeClientCommandManager<C> extends ForgeCommandManager<C> {

    /**
     * Create a command manager using native source types.
     *
     * @param execCoordinator Execution coordinator instance.
     * @return a new command manager
     * @see #ForgeClientCommandManager(Function, Function, Function) for a more thorough explanation
     */
    public static ForgeClientCommandManager<CommandSourceStack> createNative(
        final Function<CommandTree<CommandSourceStack>, CommandExecutionCoordinator<CommandSourceStack>> execCoordinator
    ) {
        return new ForgeClientCommandManager<>(execCoordinator, Function.identity(), Function.identity());
    }

    /**
     * Create a new command manager instance.
     *
     * @param commandExecutionCoordinator  Execution coordinator instance. The coordinator is in charge of executing incoming
     *                                     commands. Some considerations must be made when picking a suitable execution coordinator
     *                                     for your platform. For example, an entirely asynchronous coordinator is not suitable
     *                                     when the parsers used in that particular platform are not thread safe. If you have
     *                                     commands that perform blocking operations, however, it might not be a good idea to
     *                                     use a synchronous execution coordinator. In most cases you will want to pick between
     *                                     {@link CommandExecutionCoordinator#simpleCoordinator()} and
     *                                     {@link AsynchronousCommandExecutionCoordinator}
     * @param commandSourceMapper          Function that maps {@link CommandSourceStack} to the command sender type
     * @param backwardsCommandSourceMapper Function that maps the command sender type to {@link CommandSourceStack}
     */
    public ForgeClientCommandManager(
        final Function<CommandTree<C>, CommandExecutionCoordinator<C>> commandExecutionCoordinator,
        final Function<CommandSourceStack, C> commandSourceMapper,
        final Function<C, CommandSourceStack> backwardsCommandSourceMapper
    ) {
        super(
            commandExecutionCoordinator,
            commandSourceMapper,
            backwardsCommandSourceMapper,
            new ForgeCommandRegistrationHandler.Client<>(),
            () -> new ClientCommandSourceStack(
                CommandSource.NULL,
                Vec3.ZERO,
                Vec2.ZERO,
                4,
                "",
                Component.empty(),
                null
            )
        );

        this.registerParsers();
    }

    private void registerParsers() {
    }

    /**
     * Check if a sender has a certain permission.
     *
     * <p>The implementation for client commands always returns true.</p>
     *
     * @param sender     Command sender
     * @param permission Permission node
     * @return whether the sender has the specified permission
     */
    @Override
    public boolean hasPermission(final @NotNull C sender, final @NotNull String permission) {
        return true;
    }

    /**
     * Get a permission predicate which passes when the integrated server is running.
     *
     * @param <C> sender type
     * @return a predicate permission
     */
    public static <C> @NonNull PredicatePermission<C> integratedServerRunning() {
        return sender -> Minecraft.getInstance().hasSingleplayerServer();
    }

    /**
     * Get a permission predicate which passes when the integrated server is not running.
     *
     * @param <C> sender type
     * @return a predicate permission
     */
    public static <C> @NonNull PredicatePermission<C> integratedServerNotRunning() {
        return sender -> !Minecraft.getInstance().hasSingleplayerServer();
    }

    /**
     * Get a permission predicate which passes when cheats are enabled on the currently running integrated server.
     *
     * <p>This predicate will always pass if there is no integrated server running, i.e. when connected to a multiplayer server.</p>
     *
     * @param <C> sender type
     * @return a predicate permission
     */
    public static <C> @NonNull PredicatePermission<C> cheatsAllowed() {
        return cheatsAllowed(true);
    }

    /**
     * Get a permission predicate which passes when cheats are enabled on the currently running integrated server.
     *
     * <p>When there is no integrated server running, i.e. when connected to a multiplayer server, the predicate will
     * fall back to the provided boolean argument.</p>
     *
     * @param allowOnMultiplayer whether the predicate should pass on multiplayer servers
     * @param <C>                sender type
     * @return a predicate permission
     */
    public static <C> @NonNull PredicatePermission<C> cheatsAllowed(final boolean allowOnMultiplayer) {
        return sender -> {
            if (!Minecraft.getInstance().hasSingleplayerServer()) {
                return allowOnMultiplayer;
            }
            return Minecraft.getInstance().getSingleplayerServer().getPlayerList().isAllowCheatsForAllPlayers()
                || Minecraft.getInstance().getSingleplayerServer().getWorldData().getAllowCommands();
        };
    }

    /**
     * Get a permission predicate which passes when cheats are disabled on the currently running integrated server.
     *
     * <p>This predicate will always pass if there is no integrated server running, i.e. when connected to a multiplayer server.</p>
     *
     * @param <C> sender type
     * @return a predicate permission
     */
    public static <C> @NonNull PredicatePermission<C> cheatsDisallowed() {
        return cheatsDisallowed(true);
    }

    /**
     * Get a permission predicate which passes when cheats are disabled on the currently running integrated server.
     *
     * <p>When there is no integrated server running, i.e. when connected to a multiplayer server, the predicate will
     * fall back to the provided boolean argument.</p>
     *
     * @param allowOnMultiplayer whether the predicate should pass on multiplayer servers
     * @param <C>                sender type
     * @return a predicate permission
     */
    public static <C> @NonNull PredicatePermission<C> cheatsDisallowed(final boolean allowOnMultiplayer) {
        return sender -> {
            if (!Minecraft.getInstance().hasSingleplayerServer()) {
                return allowOnMultiplayer;
            }
            return !Minecraft.getInstance().getSingleplayerServer().getPlayerList().isAllowCheatsForAllPlayers()
                && !Minecraft.getInstance().getSingleplayerServer().getWorldData().getAllowCommands();
        };
    }
}
