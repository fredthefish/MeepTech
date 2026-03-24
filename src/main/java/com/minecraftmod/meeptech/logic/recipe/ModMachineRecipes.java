package com.minecraftmod.meeptech.logic.recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class ModMachineRecipes {
    private static Map<String, MachineRecipeType> RECIPES = new HashMap<>();
    private static boolean isInitialized = false;

    public static MachineRecipeType SMELTER = new MachineRecipeType("smelter", ModuleItems.SMELTER_CORE).setItemIO(1, 1);
    public static MachineRecipeType ALLOYER = new MachineRecipeType("alloyer", ModuleItems.ALLOYER_CORE).setItemIO(2, 1);
    public static MachineRecipeType SOLID_FUEL = 
        new MachineRecipeType("solid_fuel", ModuleItems.SOLID_FUEL_CORE).setItemIO(1, 0).setHasHeat(true);
    public static MachineRecipeType BOILER = new MachineRecipeType("boiler", ModuleItems.STEAM_BOILER_CORE).setFluidIO(1, 1);

    public static void registerRecipes() {
        RECIPES.put(SMELTER.getId(), SMELTER);
        RECIPES.put(SOLID_FUEL.getId(), SOLID_FUEL);
        RECIPES.put(ALLOYER.getId(), ALLOYER);

        SOLID_FUEL.addRecipe(new MachineRecipe("burn_coal", SOLID_FUEL).setInputItems(Map.of(Ingredient.of(Items.SUGAR_CANE), 1)).setHeat(300));

        ALLOYER.addRecipe(new MachineRecipe("alloy_bronze", ALLOYER)
            .setInputItems(Map.of(Ingredient.of(Items.COPPER_INGOT), 3, Ingredient.of(ModMaterials.TIN.getForm(MaterialForm.BASE)), 1))
            .setOutputItems(List.of(new ItemStack(ModMaterials.BRONZE.getForm(MaterialForm.BASE), 4))).setTime(200));
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
