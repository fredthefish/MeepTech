package com.minecraftmod.meeptech;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.logic.recipe.MachineRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class ModMachineRecipes {
    public static final ArrayList<MachineRecipe> RECIPES = new ArrayList<>();
    public static void initializeRecipes() {
        ModMachineRecipeTypes.SMELTER.addRecipe(new MachineRecipe(
            "smelt_raw_iron",
            Map.of(Ingredient.of(Items.RAW_IRON), 1), 
            List.of(new ItemStack(Items.IRON_INGOT)), 
            200));
    }
}
