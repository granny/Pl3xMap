package net.pl3x.map.core.world;

import java.util.Map;

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
