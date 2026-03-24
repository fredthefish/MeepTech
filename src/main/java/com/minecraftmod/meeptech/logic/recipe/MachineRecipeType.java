package com.minecraftmod.meeptech.logic.recipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.registries.DeferredItem;

public class MachineRecipeType {
    private int inputSlots = 0;
    private int outputSlots = 0;
    private int inputTanks = 0;
    private int outputTanks = 0;
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
    public int getInputTanks() {
        return inputTanks;
    }
    public int getOutputTanks() {
        return outputTanks;
    }
    public MachineRecipeType setItemIO(int inputSlots, int outputSlots) {
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        return this;
    }
    public MachineRecipeType setFluidIO(int inputTanks, int outputTanks) {
        this.inputTanks = inputTanks;
        this.outputTanks = outputTanks;
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
    public MachineRecipe getRecipe(List<ItemStack> inputs, List<ItemStack> outputs, List<FluidStack> inputFluids, List<FluidTank> outputTanks) {
        for (MachineRecipe recipe : recipes.values()) 
            if (recipe.fullInputs(inputs, inputFluids)) 
                if (recipe.canOutput(outputs, outputTanks)) return recipe;
        return null;
    }
}
