/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
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
package net.pl3x.map.core.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.function.BiFunction;
import net.pl3x.map.core.command.argument.parser.RendererParser;
import net.pl3x.map.core.renderer.Renderer;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Renderer} argument that belongs to a command.
 *
 * @param <C> command sender type
 */
@SuppressWarnings("unused")
public class RendererArgument<C> extends CommandArgument<@NotNull C, Renderer.@NotNull Builder> {
    protected RendererArgument(boolean required, @NotNull String name, @NotNull String defaultValue, @NotNull BiFunction<@NotNull CommandContext<@NotNull C>, @NotNull String, @NotNull List<@NotNull String>> suggestionsProvider, @NotNull ArgumentDescription defaultDescription) {
        super(required, name, new RendererParser<>(), defaultValue, Renderer.Builder.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link RendererArgument} builder.
     *
     * @param name argument name
     * @return new renderer argument builder
     */
    public static <C> CommandArgument.@NotNull Builder<@NotNull C, Renderer.@NotNull Builder> builder(@NotNull String name) {
        return new RendererArgument.Builder<>(name);
    }

    /**
     * Create a required {@link RendererArgument}.
     *
     * @param name argument name
     * @return constructed renderer argument
     */
    public static <C> @NotNull CommandArgument<@NotNull C, Renderer.@NotNull Builder> of(@NotNull String name) {
        return RendererArgument.<@NotNull C>builder(name).asRequired().build();
    }

    /**
     * Create an optional {@link RendererArgument}.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name argument name
     * @return constructed renderer argument
     */
    public static <C> @NotNull CommandArgument<@NotNull C, Renderer.@NotNull Builder> optional(@NotNull String name) {
        return RendererArgument.<@NotNull C>builder(name).asOptional().build();
    }

    /**
     * Create an optional {@link RendererArgument} with a default value.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name         argument name
     * @param defaultValue default value that will be used if none was supplied
     * @return constructed renderer argument
     */
    public static <C> @NotNull CommandArgument<@NotNull C, Renderer.@NotNull Builder> optional(@NotNull String name, @NotNull String defaultValue) {
        return RendererArgument.<@NotNull C>builder(name).asOptionalWithDefault(defaultValue).build();
    }

    /**
     * Mutable builder for {@link RendererArgument} instances.
     *
     * @param <C> command sender type
     */
    public static class Builder<C> extends CommandArgument.Builder<@NotNull C, Renderer.@NotNull Builder> {
        private Builder(@NotNull String name) {
            super(Renderer.Builder.class, name);
        }

        @Override
        public @NotNull CommandArgument<@NotNull C, Renderer.@NotNull Builder> build() {
            return new RendererArgument<>(isRequired(), getName(), getDefaultValue(), getSuggestionsProvider(), getDefaultDescription());
        }
    }
}
