package com.minecraftmod.meeptech.registries;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.features.VeinCountPlacement;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPlacementModifiers {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = 
        DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, MeepTech.MODID);
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<VeinCountPlacement>> VEIN_COUNT = 
        PLACEMENT_MODIFIERS.register("vein_count", () -> () -> VeinCountPlacement.CODEC);
}
