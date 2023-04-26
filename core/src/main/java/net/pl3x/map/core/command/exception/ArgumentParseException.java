/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
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
package net.pl3x.map.core.command.exception;

import java.util.function.Supplier;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Thrown to indicate that a method has been passed an illegal or inappropriate command argument.
 */
public abstract class ArgumentParseException extends IllegalArgumentException {
    private final String input;
    private final String variable;
    private final Reason reason;

    /**
     * Construct a new ArgumentParseException.
     *
     * @param input  Input
     * @param reason Failure reason
     */
    public ArgumentParseException(@Nullable String input, @NonNull String variable, @NonNull Reason reason) {
        this.input = input;
        this.variable = variable;
        this.reason = reason;
    }

    @Override
    public @NonNull String getMessage() {
        String message = MiniMessage.miniMessage().stripTags(this.reason.toString());
        if (this.input != null) {
            message = message.replace(this.variable, this.input);
        }
        return message;
    }

    /**
     * Failure reason for throwing the exception.
     */
    public static class Reason {
        private final Supplier<@NonNull String> supplier;

        public Reason(@NonNull Supplier<@NonNull String> supplier) {
            this.supplier = supplier;
        }

        @Override
        public @NonNull String toString() {
            return this.supplier.get();
        }
    }
}
