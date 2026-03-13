package com.minecraftmod.meeptech;

import com.minecraftmod.meeptech.logic.recipe.MachineRecipeHeatType;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeStandardType;

public class ModMachineRecipeTypes {
    public static MachineRecipeStandardType SMELTER = new MachineRecipeStandardType("smelter", 1, 1);
    public static MachineRecipeHeatType SOLID_FUEL = new MachineRecipeHeatType("solid_fuel", 1);
}
