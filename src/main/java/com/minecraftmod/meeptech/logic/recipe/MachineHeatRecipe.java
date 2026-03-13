package com.minecraftmod.meeptech.logic.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class MachineHeatRecipe extends MachineRecipe {
    private Ingredient ingredient;
    private int heat;
    public MachineHeatRecipe(String id, Ingredient ingredient, int heat) {
        super(id);
        this.ingredient = ingredient;
        this.heat = heat;
    }
    public Ingredient getIngredient() {
        return ingredient;
    }
    public int getHeat() {
        return heat;
    }
    public boolean validInput(ItemStack stack) {
        return ingredient.test(stack);
    }
}
