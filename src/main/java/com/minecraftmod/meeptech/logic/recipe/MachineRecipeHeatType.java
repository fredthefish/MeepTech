package com.minecraftmod.meeptech.logic.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;

public class MachineRecipeHeatType extends MachineRecipeType implements IRecipeItemInput {
    private int inputSlots;
    private List<MachineHeatRecipe> recipes = new ArrayList<>();
    public MachineRecipeHeatType(String id, int inputSlots) {
        super(id);
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
