package com.minecraftmod.meeptech;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.recipe.MachineHeatRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeHeatType;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeStandardType;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;
import com.minecraftmod.meeptech.logic.recipe.MachineStandardRecipe;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class ModMachineRecipes {
    private static Map<String, MachineRecipeType> RECIPES = new HashMap<>();
    private static boolean isInitialized = false;

    public static MachineRecipeStandardType SMELTER = new MachineRecipeStandardType("smelter", ModuleItems.SMELTER_CORE, 1, 1);
    public static MachineRecipeHeatType SOLID_FUEL = new MachineRecipeHeatType("solid_fuel", ModuleItems.SOLID_FUEL_CORE, 1);

    public static void registerRecipes() {
        RECIPES.put(SMELTER.getId(), SMELTER);
        RECIPES.put(SOLID_FUEL.getId(), SOLID_FUEL);

        SMELTER.addRecipe(new MachineStandardRecipe(
            "smelt_raw_iron", SMELTER,
            Map.of(Ingredient.of(Items.RAW_IRON), 1), 
            List.of(new ItemStack(Items.IRON_INGOT)), 
            200));
        SOLID_FUEL.addRecipe(new MachineHeatRecipe("burn_coal", SOLID_FUEL, Ingredient.of(ItemTags.COALS), 1600));

        isInitialized = true;
    }
    public static Map<String, MachineRecipeType> getRecipeTypes() {
        if (!isInitialized) registerRecipes();
        return RECIPES;
    }
    public static MachineRecipeType getRecipeType(String id) {
        if (!isInitialized) registerRecipes();
        return RECIPES.get(id);
    }
}
