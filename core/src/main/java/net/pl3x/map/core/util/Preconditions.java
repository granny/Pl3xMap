/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.pl3x.map.core.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Preconditions {
    public static void checkArgument(boolean condition, @Nullable Object error) {
        if (!condition) {
            throw new IllegalArgumentException(String.valueOf(error));
        }
    }

    public static <@NonNull T> @NonNull T checkNotNull(@Nullable T value, @Nullable Object error) {
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
