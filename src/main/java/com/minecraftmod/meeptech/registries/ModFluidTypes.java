package com.minecraftmod.meeptech.registries;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.MeepTech;

import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MeepTech.MODID);

    public static final Supplier<FluidType> STEAM_TYPE = FLUID_TYPES.register("steam", () -> new FluidType(FluidType.Properties.create()
        .descriptionId("fluid.meeptech.steam")
        .temperature(373)));
}
