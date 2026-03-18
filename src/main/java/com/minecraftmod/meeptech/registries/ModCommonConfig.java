package com.minecraftmod.meeptech.registries;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class ModCommonConfig {
    public static final ModConfigSpec SPEC;
    public static final ModCommonConfig INSTANCE;
    static {
        Builder builder = new Builder();
        INSTANCE = new ModCommonConfig(builder);
        SPEC = builder.build();
    }
    public final IntValue defaultVeinRadius;
    public final IntValue defaultVeinsPerChunk;
    private ModCommonConfig(Builder builder) {
        builder.comment("MeepTech default world generation settings.", "This can also be edited per world.")
            .push("worldgen");
        defaultVeinRadius = builder.comment("Default radius multiplier of ore veins for new worlds.")
            .defineInRange("default_vein_radius", 4, 1, 8);
        defaultVeinsPerChunk = builder.comment("Default maximum ore veins per chunk for new worlds.")
            .defineInRange("default_veins_per_chunk", 2, 0, 4);
        builder.pop();
    }
}
