package com.minecraftmod.meeptech.registries;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class ModServerConfig {
    public static final ModConfigSpec SPEC;
    public static final ModServerConfig INSTANCE;
    static {
        Builder builder = new Builder();
        INSTANCE = new ModServerConfig(builder);
        SPEC = builder.build();
    }
    public final IntValue veinRadius;
    public final IntValue veinsPerChunk;
    public final BooleanValue initializedFromCommon;
    private ModServerConfig(Builder builder) {
        builder.comment("MeepTech World Generation Settings").push("worldgen");
        initializedFromCommon = builder.comment("Internal flag that keeps track of if the world has been initialized.", 
            "Keep this at true, otherwise your world's settings will be overwritten by the common settings.")
            .define("initialized_from_common", false);
        veinRadius = builder.comment("Radius multiplier for ore veins.")
            .defineInRange("vein_radius", () -> 4, 1, 8);
        veinsPerChunk = builder.comment("Maximum ore veins per chunk.", "Set at zero to disable this feature.")
            .defineInRange("veins_per_chunk", () -> 2, 0, 4);
        builder.pop();
    }
}
