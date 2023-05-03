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
package net.pl3x.map.core.registry;

import java.util.function.Supplier;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.event.world.WorldUnloadedEvent;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class WorldRegistry extends Registry<@NonNull World> {
    public @NonNull World getOrDefault(@NonNull String id, @NonNull Supplier<@NonNull World> supplier) {
        World world = get(id);
        if (world == null) {
            world = supplier.get();
            register(world.getName(), world);
        }
        return world;
    }

    @Override
    public @Nullable World unregister(@NonNull String id) {
        World world = this.entries.remove(id);
        if (world != null) {
            Pl3xMap.api().getEventRegistry().callEvent(new WorldUnloadedEvent(world));
            world.setPaused(true);
            world.getMarkerTask().cancel();
            world.getRegionFileWatcher().stop();
            world.cleanup();
        }
        return world;
    }
}
