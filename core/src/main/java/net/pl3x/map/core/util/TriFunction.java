package net.pl3x.map.core.util;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface TriFunction<@NonNull T, @NonNull U, @NonNull V, @NonNull R> {
    @NonNull R apply(@NonNull T t, @NonNull U u, @NonNull V v);
}
