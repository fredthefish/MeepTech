package com.minecraftmod.meeptech.logic.material;

import java.util.ArrayList;

import net.minecraft.world.item.ItemStack;

public class MaterialWorkstationRecipes {
    private ArrayList<MaterialWorkstationRecipe> recipes;
    private Material material;

    public static final MaterialWorkstationRecipe[] RECIPES = new MaterialWorkstationRecipe[] {
        new MaterialWorkstationRecipe(ModMaterials.BASE, ModMaterials.PLATE, 1, 1),
        new MaterialWorkstationRecipe(ModMaterials.PLATE, ModMaterials.BOX, 6, 1),
        new MaterialWorkstationRecipe(ModMaterials.PLATE, ModMaterials.HULL, 8, 1)
    };
    
    public static final MaterialWorkstationRecipes getAvailableForms(ItemStack input) {
        MaterialItemData itemData = new MaterialItemData(input.getItem());
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
