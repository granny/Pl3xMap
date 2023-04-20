package net.pl3x.map.core.world;

import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LegacyBiomes {
    private static final Biome[] BIOME_IDS = new Biome[174];

    static {
        Arrays.fill(BIOME_IDS, Biome.DEFAULT);

        BIOME_IDS[0] = create("minecraft:ocean", 0.5F, 0.5F, 0x000070, 0x73A74E, 0x8EB971, 0x3F76E4);
        BIOME_IDS[1] = create("minecraft:plains", 0.8F, 0.4F, 0x8DB360, 0x73AB30, 0x91BD59, 0x3F76E4);
        BIOME_IDS[2] = create("minecraft:desert", 2.0F, 0.0F, 0xFA9418, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[3] = create("minecraft:mountains", 0.2F, 0.3F, 0x606060, 0x6CA36D, 0x8AB689, 0x3F76E4);
        BIOME_IDS[4] = create("minecraft:forest", 0.7F, 0.8F, 0x056621, 0x59AF30, 0x79C05A, 0x3F76E4);
        BIOME_IDS[5] = create("minecraft:taiga", 0.25F, 0.8F, 0x0B6659, 0x68A463, 0x86B783, 0x3F76E4);
        BIOME_IDS[6] = create("minecraft:swamp", 0.8F, 0.9F, 0x07F9B2, 0x44B522, 0x6AC44E, 0x617B64);
        BIOME_IDS[7] = create("minecraft:river", 0.5F, 0.5F, 0x0000FF, 0x73A74E, 0x8EB971, 0x3F76E4);
        BIOME_IDS[8] = create("minecraft:nether", 2.0F, 0.0F, 0xBF3B3B, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[9] = create("minecraft:the_end", 0.5F, 0.5F, 0x8080FF, 0x73A74E, 0x8EB971, 0x3F76E4);
        BIOME_IDS[10] = create("minecraft:frozen_ocean", 0.0F, 0.5F, 0x7070D6, 0x63A278, 0x80B497, 0x3938C9);
        BIOME_IDS[11] = create("minecraft:frozen_river", 0.0F, 0.5F, 0xA0A0FF, 0x63A278, 0x80B497, 0x3938C9);
        BIOME_IDS[12] = create("minecraft:snowy_tundra", 0.0F, 0.5F, 0xFFFFFF, 0x63A278, 0x80B497, 0x3F76E4);
        BIOME_IDS[13] = create("minecraft:snowy_mountains", 0.0F, 0.5F, 0xA0A0A0, 0x63A278, 0x80B497, 0x3F76E4);
        BIOME_IDS[14] = create("minecraft:mushroom_fields", 0.9F, 1.0F, 0xFF00FF, 0x2EBB0A, 0x55C93F, 0x3F76E4);
        BIOME_IDS[15] = create("minecraft:mushroom_field_shore", 0.9F, 1.0F, 0xA000FF, 0x2EBB0A, 0x55C93F, 0x3F76E4);
        BIOME_IDS[16] = create("minecraft:beach", 0.8F, 0.4F, 0xFADE55, 0x73AB30, 0x91BD59, 0x3F76E4);
        BIOME_IDS[17] = create("minecraft:desert_hills", 2.0F, 0.0F, 0xD25F12, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[18] = create("minecraft:wooded_hills", 0.7F, 0.8F, 0x22551C, 0x59AF30, 0x79C05A, 0x3F76E4);
        BIOME_IDS[19] = create("minecraft:taiga_hills", 0.25F, 0.8F, 0x163933, 0x68A463, 0x86B783, 0x3F76E4);
        BIOME_IDS[20] = create("minecraft:mountain_edge", 0.2F, 0.3F, 0x72789A, 0x6CA36D, 0x8AB689, 0x3F76E4);
        BIOME_IDS[21] = create("minecraft:jungle", 0.95F, 0.9F, 0x537B09, 0x2EBB0A, 0x59C93C, 0x3F76E4);
        BIOME_IDS[22] = create("minecraft:jungle_hills", 0.95F, 0.9F, 0x2C4205, 0x2EBB0A, 0x59C93C, 0x3F76E4);
        BIOME_IDS[23] = create("minecraft:jungle_edge", 0.95F, 0.8F, 0x628B17, 0x43B70F, 0x64C73F, 0x3F76E4);
        BIOME_IDS[24] = create("minecraft:deep_ocean", 0.5F, 0.5F, 0x000030, 0x73A74E, 0x8EB971, 0x3F76E4);
        BIOME_IDS[25] = create("minecraft:stone_shore", 0.2F, 0.3F, 0xA2A284, 0x6CA36D, 0x8AB689, 0x3F76E4);
        BIOME_IDS[26] = create("minecraft:snowy_beach", 0.05F, 0.3F, 0xFAF0C0, 0x63A278, 0x83B593, 0x3D57D6);
        BIOME_IDS[27] = create("minecraft:birch_forest", 0.6F, 0.6F, 0x307444, 0x6BA941, 0x88BB66, 0x3F76E4);
        BIOME_IDS[28] = create("minecraft:birch_forest_hills", 0.6F, 0.6F, 0x1F5F32, 0x6BA941, 0x88BB66, 0x3F76E4);
        BIOME_IDS[29] = create("minecraft:dark_forest", 0.7F, 0.8F, 0x40511A, 0x59AF30, 0x79C05A, 0x3F76E4);
        BIOME_IDS[30] = create("minecraft:snowy_taiga", -0.5F, 0.4F, 0x31554A, 0x63A278, 0x80B497, 0x3D57D6);
        BIOME_IDS[31] = create("minecraft:snowy_taiga_hills", -0.5F, 0.4F, 0x243F36, 0x63A278, 0x80B497, 0x3D57D6);
        BIOME_IDS[32] = create("minecraft:giant_tree_taiga", 0.3F, 0.8F, 0x596651, 0x67A55F, 0x86B87F, 0x3F76E4);
        BIOME_IDS[33] = create("minecraft:giant_tree_taiga_hills", 0.3F, 0.8F, 0x454F3E, 0x67A55F, 0x86B87F, 0x3F76E4);
        BIOME_IDS[34] = create("minecraft:wooded_mountains", 0.2F, 0.3F, 0x507050, 0x6CA36D, 0x8AB689, 0x3F76E4);
        BIOME_IDS[35] = create("minecraft:savanna", 2.0F, 0.0F, 0xBDB25F, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[36] = create("minecraft:savanna_plateau", 2.0F, 0.0F, 0xA79D64, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[37] = create("minecraft:badlands", 2.0F, 0.0F, 0xD94515, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[38] = create("minecraft:wooded_badlands_plateau", 2.0F, 0.0F, 0xB09765, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[39] = create("minecraft:badlands_plateau", 2.0F, 0.0F, 0xCA8C65, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[40] = create("minecraft:small_end_islands", 0.5F, 0.5F, 0x00002A, 0x73A74E, 0x8EB971, 0x3F76E4);
        BIOME_IDS[41] = create("minecraft:end_midlands", 0.5F, 0.5F, 0xEBF8B6, 0x73A74E, 0x8EB971, 0x3F76E4);
        BIOME_IDS[42] = create("minecraft:end_highlands", 0.5F, 0.5F, 0xC3BD89, 0x73A74E, 0x8EB971, 0x3F76E4);
        BIOME_IDS[43] = create("minecraft:end_barrens", 0.5F, 0.5F, 0x909072, 0x73A74E, 0x8EB971, 0x3F76E4);
        BIOME_IDS[44] = create("minecraft:warm_ocean", 0.5F, 0.5F, 0x0000AC, 0x73A74E, 0x8EB971, 0x43D5EE);
        BIOME_IDS[45] = create("minecraft:lukewarm_ocean", 0.5F, 0.5F, 0x000090, 0x73A74E, 0x8EB971, 0x45ADF2);
        BIOME_IDS[46] = create("minecraft:cold_ocean", 0.5F, 0.5F, 0x202070, 0x73A74E, 0x8EB971, 0x3D57D6);
        BIOME_IDS[47] = create("minecraft:deep_warm_ocean", 0.5F, 0.5F, 0x000050, 0x73A74E, 0x8EB971, 0x43D5EE);
        BIOME_IDS[48] = create("minecraft:deep_lukewarm_ocean", 0.5F, 0.5F, 0x000040, 0x73A74E, 0x8EB971, 0x45ADF2);
        BIOME_IDS[49] = create("minecraft:deep_cold_ocean", 0.5F, 0.5F, 0x202038, 0x73A74E, 0x8EB971, 0x3D57D6);
        BIOME_IDS[50] = create("minecraft:deep_frozen_ocean", 0.5F, 0.5F, 0x404090, 0x73A74E, 0x8EB971, 0x3938C9);
        BIOME_IDS[127] = create("minecraft:the_void", 0.5F, 0.5F, 0x000000, 0x73A74E, 0x8EB971, 0x3F76E4);
        BIOME_IDS[129] = create("minecraft:sunflower_plains", 0.8F, 0.4F, 0xB5DB88, 0x73AB30, 0x91BD59, 0x3F76E4);
        BIOME_IDS[130] = create("minecraft:desert_lakes", 2.0F, 0.0F, 0xFFBC40, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[131] = create("minecraft:gravelly_mountains", 0.2F, 0.3F, 0x888888, 0x6CA36D, 0x8AB689, 0x3F76E4);
        BIOME_IDS[132] = create("minecraft:flower_forest", 0.7F, 0.8F, 0x2D8E49, 0x59AF30, 0x79C05A, 0x3F76E4);
        BIOME_IDS[133] = create("minecraft:taiga_mountains", 0.25F, 0.8F, 0x338E81, 0x68A463, 0x86B783, 0x3F76E4);
        BIOME_IDS[134] = create("minecraft:swamp_hills", 0.8F, 0.9F, 0x2FFFDA, 0x44B522, 0x6AC44E, 0x617B64);
        BIOME_IDS[140] = create("minecraft:ice_spikes", 0.0F, 0.5F, 0xB4DCDC, 0x63A278, 0x80B497, 0x3F76E4);
        BIOME_IDS[149] = create("minecraft:modified_jungle", 0.95F, 0.9F, 0x7BA331, 0x2EBB0A, 0x59C93C, 0x3F76E4);
        BIOME_IDS[151] = create("minecraft:modified_jungle_edge", 0.95F, 0.8F, 0x8AB33F, 0x43B70F, 0x64C73F, 0x3F76E4);
        BIOME_IDS[155] = create("minecraft:tall_birch_forest", 0.6F, 0.6F, 0x589C6C, 0x6BA941, 0x88BB66, 0x3F76E4);
        BIOME_IDS[156] = create("minecraft:tall_birch_hills", 0.6F, 0.6F, 0x47875A, 0x6BA941, 0x88BB66, 0x3F76E4);
        BIOME_IDS[157] = create("minecraft:dark_forest_hills", 0.7F, 0.8F, 0x687942, 0x59AF30, 0x79C05A, 0x3F76E4);
        BIOME_IDS[158] = create("minecraft:snowy_taiga_mountains", -0.5F, 0.4F, 0x597D72, 0x63A278, 0x80B497, 0x3D57D6);
        BIOME_IDS[160] = create("minecraft:giant_spruce_taiga", 0.25F, 0.8F, 0x818E79, 0x68A463, 0x86B783, 0x3F76E4);
        BIOME_IDS[161] = create("minecraft:giant_spruce_taiga_hills", 0.25F, 0.8F, 0x6D7766, 0x68A463, 0x86B783, 0x3F76E4);
        BIOME_IDS[162] = create("minecraft:modified_gravelly_mountains", 0.2F, 0.3F, 0x789878, 0x6CA36D, 0x8AB689, 0x3F76E4);
        BIOME_IDS[163] = create("minecraft:shattered_savanna", 1.1F, 0.0F, 0xE5DA87, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[164] = create("minecraft:shattered_savanna_plateau", 1.0F, 0.0F, 0xCFC58C, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[165] = create("minecraft:eroded_badlands", 2.0F, 0.0F, 0xFF6D3D, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[166] = create("minecraft:modified_wooded_badlands_plateau", 2.0F, 0.0F, 0xD8BF8D, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[167] = create("minecraft:modified_badlands_plateau", 2.0F, 0.0F, 0xF2B48D, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[168] = create("minecraft:bamboo_jungle", 0.95F, 0.9F, 0x768E14, 0x1F8907, 0x59C93C, 0x3F76E4);
        BIOME_IDS[169] = create("minecraft:bamboo_jungle_hills", 0.95F, 0.9F, 0x3B470A, 0x1F8907, 0x59C93C, 0x3F76E4);
        BIOME_IDS[170] = create("minecraft:soul_sand_valley", 2.0F, 0.0F, 0x5E3830, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[171] = create("minecraft:crimson_forest", 2.0F, 0.0F, 0xDD0808, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[172] = create("minecraft:warped_forest", 2.0F, 0.0F, 0x49907B, 0xA9A52C, 0xBFB755, 0x3F76E4);
        BIOME_IDS[173] = create("minecraft:basalt_deltas", 2.0F, 0.0F, 0x403636, 0xA9A52C, 0xBFB755, 0x3F76E4);
    }

    @NonNull
    private static Biome create(@NonNull String id, float temperature, float humidity, int color, int foliage, int grass, int water) {
        return new Biome(0, id, color, foliage, grass, water, (x, z, def) -> def);
    }

    @Nullable
    public static Biome get(int legacyId) {
        if (legacyId < 0 || legacyId >= BIOME_IDS.length) legacyId = 0;
        return BIOME_IDS[legacyId];
    }
}
