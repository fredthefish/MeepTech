package com.minecraftmod.meeptech.logic.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

public class MachineRecipeStandardType extends MachineRecipeType implements IRecipeItemInput {
    private int inputSlots = 0;
    private int outputSlots = 0;
    private Map<String, MachineStandardRecipe> recipes = new HashMap<>();
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
        if (recipe instanceof MachineStandardRecipe standardRecipe) recipes.put(standardRecipe.getId(), standardRecipe);
    }
    @Override
    public List<MachineRecipe> getRecipes() {
        List<MachineRecipe> newRecipes = new ArrayList<>();
        for (MachineRecipe recipe : recipes.values()) newRecipes.add(recipe);
        return newRecipes;
    }
    @Override
    public MachineRecipe getRecipe(String recipe) {
        return recipes.get(recipe);
    }
    public boolean validInput(ItemStack stack) {
        for (MachineStandardRecipe recipe : recipes.values()) {
            if (recipe.validInput(stack)) return true;
        }
        return false;
    }
    public MachineStandardRecipe getRecipe(List<ItemStack> inputs) {
        for (MachineStandardRecipe recipe : recipes.values()) {
            if (recipe.fullInputs(inputs)) return recipe;
        }
        return null;
    }
}
