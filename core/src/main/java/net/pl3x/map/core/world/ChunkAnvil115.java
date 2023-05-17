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

import java.util.Arrays;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class ChunkAnvil115 extends Chunk {
    protected ChunkAnvil115(@NotNull World world, @NotNull Region region, @NotNull CompoundTag chunkTag) {
        super(world, region, chunkTag, 36);

        CompoundTag levelData = chunkTag.getCompoundTag("Level");

        this.full = levelData.getString("Status").equals("full");
        if (!this.full) {
            return;
        }

        if (levelData.containsKey("Heightmaps")) {
            CompoundTag heightmaps = levelData.getCompoundTag("Heightmaps");
            this.worldSurfaceHeights = heightmaps.getLongArray("WORLD_SURFACE");
        }

        if (levelData.containsKey("Sections")) {
            this.sections = new Section[32]; //32 supports a max world-height of 512 which is the max that the heightmaps of Minecraft V1.13+ can store with 9 bits, I believe?
            for (CompoundTag sectionTag : levelData.getListTag("Sections").asCompoundTagList()) {
                Section section = new Section(sectionTag);
                if (section.sectionY >= 0 && section.sectionY < this.sections.length) {
                    this.sections[section.sectionY] = section;
                }
            }
        }

        Tag<?> tag = levelData.get("Biomes"); //tag can be byte-array or int-array
        if (tag instanceof ByteArrayTag) {
            byte[] bs = ((ByteArrayTag) tag).getValue();
            this.biomes = new int[bs.length];
            for (int i = 0; i < bs.length; i++) {
                this.biomes[i] = bs[i] & 0xFF;
            }
        } else if (tag instanceof IntArrayTag) {
            this.biomes = ((IntArrayTag) tag).getValue();
        }
        if (this.biomes == null || this.biomes.length == 0) {
            this.biomes = new int[1024];
        }
        if (this.biomes.length < 1024) {
            this.biomes = Arrays.copyOf(this.biomes, 1024);
        }
    }

    @Override
    public @NotNull Biome getBiome(int x, int y, int z) {
        int index = ((y >> 2) << 4) + (((z & 0xF) >> 2) << 2) + ((x & 0xF) >> 2);
        if (index < 0) {
            return Biome.DEFAULT;
        }
        if (index >= this.biomes.length) {
            return Biome.DEFAULT;
        }
        Biome biome = LegacyBiomes.get(this.biomes[index]);
        return biome == null ? Biome.DEFAULT : biome;
    }
}
