package com.minecraftmod.meeptech.logic.recipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

public class MachineRecipeType {
    private int inputSlots = 0;
    private int outputSlots = 0;
    private String id;
    private DeferredItem<Item> iconItem;
    private final Map<String, MachineRecipe> recipes = new LinkedHashMap<>();
    private boolean hasHeat;
    public MachineRecipeType(String id, DeferredItem<Item> iconItem) {
        this.id = id;
        this.iconItem = iconItem;
    }
    public String getId() {
        return id;
    }
    public Item getIcon() {
        return iconItem.get();
    }
    public int getInputSlots() {
        return inputSlots;
    }
    public int getOutputSlots() {
        return outputSlots;
    }
    public MachineRecipeType setItemIO(int inputSlots, int outputSlots) {
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        return this;
    }
    public MachineRecipeType setHasHeat(boolean hasHeat) {
        this.hasHeat = hasHeat;
        return this;
    }
    public boolean hasHeat() {
        return hasHeat;
    }
    public void addRecipe(MachineRecipe recipe) {
        recipes.put(recipe.getId(), recipe);
    }
    public List<MachineRecipe> getRecipes() {
        return List.copyOf(recipes.values());
    }
    public MachineRecipe getRecipe(String recipe) {
        return recipes.get(recipe);
    }
    public MachineRecipe getRecipe(List<ItemStack> inputs, List<ItemStack> outputs) {
        for (MachineRecipe recipe : recipes.values()) if (recipe.fullInputs(inputs)) if (recipe.canOutput(outputs)) return recipe;
        return null;
    }
}
