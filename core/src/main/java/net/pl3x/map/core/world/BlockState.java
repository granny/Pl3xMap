package net.pl3x.map.core.world;

import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BlockState {
    private final Block block;
    private final byte age;
    private final byte moisture;
    private final byte power;

    public BlockState(@NonNull Block block) {
        this.block = block;
        this.age = this.moisture = this.power = -1;
    }

    public BlockState(@NonNull Block block, @NonNull Map<@NonNull String, @NonNull String> properties) {
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

    public @NonNull Block getBlock() {
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
