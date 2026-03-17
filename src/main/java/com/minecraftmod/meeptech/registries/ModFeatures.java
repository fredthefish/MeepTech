package com.minecraftmod.meeptech.registries;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.features.OreVeinFeature;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MeepTech.MODID);
    public static final DeferredHolder<Feature<?>, OreVeinFeature> ORE_VEIN = FEATURES.register("ore_vein", OreVeinFeature::new);
}
