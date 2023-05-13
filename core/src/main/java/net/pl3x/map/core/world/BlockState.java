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
package net.pl3x.map.core.world;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class BlockState {
    private final Block block;
    private final byte age;
    private final byte moisture;
    private final byte power;

    public BlockState(@NotNull Block block) {
        this.block = block;
        this.age = this.moisture = this.power = -1;
    }

    public BlockState(@NotNull Block block, @NotNull Map<@NotNull String, @NotNull String> properties) {
        this.block = block;

        byte age = -1;
        try {
            age = Integer.valueOf(properties.get("age")).byteValue();
        } catch (NumberFormatException ignore) {
        }
        this.age = age;

        byte moisture = -1;
        try {
            moisture = Integer.valueOf(properties.get("moisture")).byteValue();
        } catch (NumberFormatException ignore) {
        }
        this.moisture = moisture;

        byte power = -1;
        try {
            power = Integer.valueOf(properties.get("power")).byteValue();
        } catch (NumberFormatException ignore) {
        }
        this.power = power;
    }

    public @NotNull Block getBlock() {
        return this.block;
    }

    public byte getAge() {
        return this.age;
    }

    public byte getMoisture() {
        return this.moisture;
    }

    public byte getPower() {
        return this.power;
    }
}
