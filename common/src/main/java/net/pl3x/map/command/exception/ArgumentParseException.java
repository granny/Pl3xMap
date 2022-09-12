package net.pl3x.map.command.exception;

import java.util.function.Supplier;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public ArgumentParseException(@Nullable String input, @NotNull String variable, @NotNull Reason reason) {
        this.input = input;
        this.variable = variable;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
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
        private final Supplier<String> supplier;

        public Reason(Supplier<String> supplier) {
            this.supplier = supplier;
        }

        @Override
        public String toString() {
            return this.supplier.get();
        }
    }
}
