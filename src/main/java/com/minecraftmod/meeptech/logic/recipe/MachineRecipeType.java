package com.minecraftmod.meeptech.logic.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;

public class MachineRecipeType {
    private String id;
    private int inputSlots;
    private int outputSlots;
    private List<MachineRecipe> recipes = new ArrayList<>();
    public MachineRecipeType(String id, int inputSlots, int outputSlots) {
        this.id = id;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
    }
    public String getId() {
        return id;
    }
    public int getInputSlots() {
        return inputSlots;
    }
    public int getOutputSlots() {
        return outputSlots;
    }
    public void addRecipe(MachineRecipe recipe) {
        recipes.add(recipe);
    }
    public boolean validInput(ItemStack stack) {
        for (MachineRecipe recipe : recipes) {
            if (recipe.validInput(stack)) return true;
        }
        return false;
    }
    public MachineRecipe getRecipe(List<ItemStack> inputs) {
        for (MachineRecipe recipe : recipes) {
            if (recipe.fullInputs(inputs)) return recipe;
        }
        return null;
    }
}
