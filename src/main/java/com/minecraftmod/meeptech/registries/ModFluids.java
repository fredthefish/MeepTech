package com.minecraftmod.meeptech.registries;

import com.minecraftmod.meeptech.MeepTech;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MeepTech.MODID);

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> STEAM = 
        FLUIDS.register("steam", () -> new BaseFlowingFluid.Source(steamProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> STEAM_FLOWING = 
        FLUIDS.register("steam_flowing", () -> new BaseFlowingFluid.Flowing(steamProperties()));
    private static BaseFlowingFluid.Properties steamProperties() {
        return new BaseFlowingFluid.Properties(ModFluidTypes.STEAM_TYPE, STEAM, STEAM_FLOWING);
    }
}
