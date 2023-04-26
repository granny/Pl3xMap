/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pl3x.map.core.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RegionModifiedState {
    private final Map<@NonNull Long, @NonNull Long> regionModifiedStates = new ConcurrentHashMap<>(); // <pos, modified>
    private final File file;

    public RegionModifiedState(@NonNull World world) {
        this.file = world.getTilesDirectory().resolve(".rms").toFile();

        if (this.file.exists()) {
            try (DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(this.file)))) {
                int size = in.readInt();
                for (int i = 0; i < size; i++) {
                    this.regionModifiedStates.put(in.readLong(), in.readLong());
                }
            } catch (Throwable ignore) {
            }
        }
    }

    public void set(long regionPos, long modified) {
        this.regionModifiedStates.put(regionPos, modified);
    }

    public long get(long regionPos) {
        Long modified = this.regionModifiedStates.get(regionPos);
        return modified == null ? -1 : modified;
    }

    public void save() {
        try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(this.file)))) {
            out.writeInt(this.regionModifiedStates.size());
            for (Map.Entry<Long, Long> entry : this.regionModifiedStates.entrySet()) {
                out.writeLong(entry.getKey());
                out.writeLong(entry.getValue());
            }
            out.flush();
        } catch (Throwable ignore) {
        }
    }
}
