package net.pl3x.map.core.util;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    @NonNull
    R apply(T t, U u, V v);
}
