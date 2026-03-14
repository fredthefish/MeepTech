package com.minecraftmod.meeptech.logic.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

public class MachineRecipeHeatType extends MachineRecipeType implements IRecipeItemInput {
    private int inputSlots;
    private List<MachineHeatRecipe> recipes = new ArrayList<>();
    public MachineRecipeHeatType(String id, DeferredItem<Item> icon, int inputSlots) {
        super(id, icon);
        this.inputSlots = inputSlots;
    }
    @Override
    public int getInputSlots() {
        return inputSlots;
    }
    @Override
    public void addRecipe(MachineRecipe recipe) {
        if (recipe instanceof MachineHeatRecipe heatRecipe) recipes.add(heatRecipe);
    }
    @Override
    public List<MachineRecipe> getRecipes() {
        List<MachineRecipe> newRecipes = new ArrayList<>();
        for (MachineRecipe recipe : recipes) newRecipes.add(recipe);
        return newRecipes;
    }
    public boolean validInput(ItemStack stack) {
        for (MachineHeatRecipe recipe : recipes) {
            if (recipe.validInput(stack)) return true;
        }
        return false;
    }
    public MachineHeatRecipe getRecipe(ItemStack input) {
        for (MachineHeatRecipe recipe : recipes) {
            if (recipe.validInput(input)) return recipe;
        }
        return null;
    }
}
