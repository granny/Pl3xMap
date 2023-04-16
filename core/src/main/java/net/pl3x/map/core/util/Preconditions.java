package net.pl3x.map.core.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Preconditions {
    public static void checkArgument(boolean condition, @Nullable Object error) {
        if (!condition) {
            throw new IllegalArgumentException(String.valueOf(error));
        }
    }

    @NonNull
    public static <T> T checkNotNull(@Nullable T value, @Nullable Object error) {
        if (value == null) {
            throw new NullPointerException(String.valueOf(error));
        }
        return value;
    }

    public static void checkState(boolean condition, @Nullable Object error) {
        if (!condition) {
            throw new IllegalStateException(String.valueOf(error));
        }
    }
}
