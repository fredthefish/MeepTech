package com.minecraftmod.meeptech.logic.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

public class MachineRecipeStandardType extends MachineRecipeType implements IRecipeItemInput {
    private int inputSlots = 0;
    private int outputSlots = 0;
    private List<MachineStandardRecipe> recipes = new ArrayList<>();
    public MachineRecipeStandardType(String id, DeferredItem<Item> icon, int inputSlots, int outputSlots) {
        super(id, icon);
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
    }
    @Override
    public int getInputSlots() {
        return inputSlots;
    }
    public int getOutputSlots() {
        return outputSlots;
    }
    @Override
    public void addRecipe(MachineRecipe recipe) {
        if (recipe instanceof MachineStandardRecipe standardRecipe) recipes.add(standardRecipe);
    }
    @Override
    public List<MachineRecipe> getRecipes() {
        List<MachineRecipe> newRecipes = new ArrayList<>();
        for (MachineRecipe recipe : recipes) newRecipes.add(recipe);
        return newRecipes;
    }
    public boolean validInput(ItemStack stack) {
        for (MachineStandardRecipe recipe : recipes) {
            if (recipe.validInput(stack)) return true;
        }
        return false;
    }
    public MachineStandardRecipe getRecipe(List<ItemStack> inputs) {
        for (MachineStandardRecipe recipe : recipes) {
            if (recipe.fullInputs(inputs)) return recipe;
        }
        return null;
    }
}
