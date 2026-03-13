package com.minecraftmod.meeptech;

import java.util.HashMap;

import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;

public class ModMachineRecipeTypes {
    public static HashMap<String, MachineRecipeType> RECIPE_TYPES = new HashMap<>();

    public static MachineRecipeType SMELTER = addMachineRecipeType(new MachineRecipeType("smelter", 1, 1));
    
    public static MachineRecipeType addMachineRecipeType(MachineRecipeType recipeType) {
        RECIPE_TYPES.put(recipeType.getId(), recipeType);
        return recipeType;
    }
    public static MachineRecipeType getRecipeType(String id) {
        return RECIPE_TYPES.get(id);
    }
}
