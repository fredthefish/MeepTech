package com.minecraftmod.meeptech.logic.material;

import java.util.ArrayList;

import net.minecraft.world.item.ItemStack;

public class MaterialWorkstationRecipes {
    private ArrayList<MaterialWorkstationRecipe> recipes;
    private Material material;

    public static final MaterialWorkstationRecipe[] RECIPES = new MaterialWorkstationRecipe[] {
        new MaterialWorkstationRecipe(MaterialForm.BASE, MaterialForm.PLATE, 1, 1, 0),
        new MaterialWorkstationRecipe(MaterialForm.PLATE, MaterialForm.GEAR, 4, 1, 1),
        new MaterialWorkstationRecipe(MaterialForm.PLATE, MaterialForm.ROTOR, 4, 1, 1),
        new MaterialWorkstationRecipe(MaterialForm.PLATE, MaterialForm.BOX, 6, 1, 1),
        new MaterialWorkstationRecipe(MaterialForm.PLATE, MaterialForm.HULL, 8, 1, 1)
    };
    
    public static final MaterialWorkstationRecipes getAvailableForms(ItemStack input) {
        MaterialItemData itemData = new MaterialItemData(input.getItem());
        if (itemData.getMaterial() == null) return null;

        MaterialWorkstationRecipes result = new MaterialWorkstationRecipes();
        result.material = itemData.getMaterial();
        result.recipes = new ArrayList<MaterialWorkstationRecipe>();
        for (MaterialWorkstationRecipe recipe : RECIPES) {
            if (recipe.getInputForm() == itemData.getForm()) {
                if (input.getCount() >= recipe.getInputAmount()) {
                    if (result.material.hasForm(recipe.getOutputForm()) && result.material.getMaterialTier() <= recipe.getMaxTier()) {
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
