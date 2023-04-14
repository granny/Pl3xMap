package net.pl3x.map.core.world;

public class BlockState {
    private static final byte NEGATIVE_ONE = (byte) -1;

    private final Block block;
    //private final byte age;
    //private final byte level;
    //private final byte power;

    public BlockState(Block block) {
        //this(block, NEGATIVE_ONE, NEGATIVE_ONE, NEGATIVE_ONE);
        this.block = block;
    }

    /*public BlockState(Block block, byte age, byte level, byte power) {
        this.block = block;
        this.age = age;
        this.level = level;
        this.power = power;
    }*/

    public Block getBlock() {
        return this.block;
    }

    /*public byte getAge() {
        return this.age;
    }

    public byte getLevel() {
        return this.level;
    }

    public byte getPower() {
        return this.power;
    }*/
}
