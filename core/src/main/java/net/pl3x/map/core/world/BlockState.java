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
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BlockState {
    private final Block block;
    private final byte age;
    private final byte moisture;
    private final byte power;

    public BlockState(Block block) {
        this.block = block;
        this.age = this.moisture = this.power = -1;
    }

    public BlockState(Block block, Map<String, String> properties) {
        this.block = block;

        byte age = -1;
        byte moisture = -1;
        byte power = -1;

        if (!properties.isEmpty()) {
            String ageProperty = properties.get("age");
            if (ageProperty != null) {
                try {
                    age = Integer.valueOf(ageProperty).byteValue();
                } catch (NumberFormatException ignore) {}
            }

            String moistureProperty = properties.get("moisture");
            if (moistureProperty != null) {
                try {
                    moisture = Integer.valueOf(moistureProperty).byteValue();
                } catch (NumberFormatException ignore) {}
            }

            String powerProperty = properties.get("power");
            if (powerProperty != null) {
                try {
                    power = Integer.valueOf(powerProperty).byteValue();
                } catch (NumberFormatException ignore) {}
            }
        }

        this.age = age;
        this.moisture = moisture;
        this.power = power;
    }

    public Block getBlock() {
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
