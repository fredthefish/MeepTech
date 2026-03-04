package com.minecraftmod.meeptech.logic;

import java.util.ArrayList;

import net.minecraft.world.item.ItemStack;

public class MaterialWorkstationRecipes {
    private ArrayList<MaterialWorkstationRecipe> recipes;
    private Material material;

    public static final MaterialWorkstationRecipe[] RECIPES = new MaterialWorkstationRecipe[] {
        new MaterialWorkstationRecipe(MaterialForm.Base, MaterialForm.Plate, 1, 1),
        new MaterialWorkstationRecipe(MaterialForm.Plate, MaterialForm.LargePlate, 4, 1),
        new MaterialWorkstationRecipe(MaterialForm.Plate, MaterialForm.Box, 6, 1),
        new MaterialWorkstationRecipe(MaterialForm.Plate, MaterialForm.Hull, 8, 1)
    };
    
    public static final MaterialWorkstationRecipes getAvailableForms(ItemStack input) {
        ItemData itemData = new ItemData(input.getItem());
        if (itemData.getMaterial() == null) return null;

        MaterialWorkstationRecipes result = new MaterialWorkstationRecipes();
        result.recipes = new ArrayList<MaterialWorkstationRecipe>();
        result.material = itemData.getMaterial();
        for (MaterialWorkstationRecipe recipe : RECIPES) {
            if (recipe.getInputForm() == itemData.getForm()) {
                if (input.getCount() >= recipe.getInputAmount()) {
                    if (result.material.hasForm(recipe.getOutputForm())) {
                        result.recipes.add(recipe);
                    }
                }
            }
        }
        return result;
    }
    public ArrayList<MaterialWorkstationRecipe> getRecipes() {
        return recipes;
    }
    public Material getMaterial() {
        return material;
    }
}
