package com.minecraftmod.meeptech.logic.recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class ModMachineRecipes {
    private static Map<String, MachineRecipeType> RECIPES = new HashMap<>();
    private static boolean isInitialized = false;

    public static MachineRecipeStandardType SMELTER = new MachineRecipeStandardType("smelter", 
        ModuleItems.SMELTER_CORE, 1, 1);
    public static MachineRecipeStandardType ALLOYER = new MachineRecipeStandardType("alloyer", 
        ModuleItems.ALLOYER_CORE, 2, 1);
    public static MachineRecipeHeatType SOLID_FUEL = new MachineRecipeHeatType("solid_fuel", 
        ModuleItems.SOLID_FUEL_CORE, 1);

    public static void registerRecipes() {
        RECIPES.put(SMELTER.getId(), SMELTER);
        RECIPES.put(SOLID_FUEL.getId(), SOLID_FUEL);
        RECIPES.put(ALLOYER.getId(), ALLOYER);

        SOLID_FUEL.addRecipe(new MachineHeatRecipe("burn_coal", SOLID_FUEL, Ingredient.of(ItemTags.COALS), 1600));
        SOLID_FUEL.addRecipe(new MachineHeatRecipe("burn_coal_block", SOLID_FUEL, Ingredient.of(Items.COAL_BLOCK), 16000));
        SOLID_FUEL.addRecipe(new MachineHeatRecipe("burn_dried_kelp_block", SOLID_FUEL, Ingredient.of(Items.DRIED_KELP_BLOCK), 4000));
        SOLID_FUEL.addRecipe(new MachineHeatRecipe("burn_blaze_rod", SOLID_FUEL, Ingredient.of(Items.BLAZE_ROD), 2400));
        SOLID_FUEL.addRecipe(new MachineHeatRecipe("burn_sugar_canae", SOLID_FUEL, Ingredient.of(Items.SUGAR_CANE), 300));

        ALLOYER.addRecipe(new MachineStandardRecipe("alloy_bronze", ALLOYER, 
            Map.of(Ingredient.of(Items.COPPER_INGOT), 3, Ingredient.of(ModMaterials.TIN.getForm(MaterialForm.BASE)), 1), 
            List.of(new ItemStack(ModMaterials.BRONZE.getForm(MaterialForm.BASE), 4)), 200));

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
