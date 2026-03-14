package com.minecraftmod.meeptech.logic.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredItem;

public class MachineRecipeHeatType extends MachineRecipeType implements IRecipeItemInput {
    private int inputSlots;
    private Map<String, MachineHeatRecipe> recipes = new HashMap<>();
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
        if (recipe instanceof MachineHeatRecipe heatRecipe) recipes.put(heatRecipe.getId(), heatRecipe);
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
        for (MachineHeatRecipe recipe : recipes.values()) {
            if (recipe.validInput(stack)) return true;
        }
        if (stack.getBurnTime(RecipeType.SMELTING) > 0 && stack.getItem() != Items.LAVA_BUCKET) return true;
        return false;
    }
    public MachineHeatRecipe getRecipe(ItemStack input) {
        for (MachineHeatRecipe recipe : recipes.values()) {
            if (recipe.validInput(input)) return recipe;
        }
        return null;
    }
}
