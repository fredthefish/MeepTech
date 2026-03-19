package com.minecraftmod.meeptech.logic.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.integration.MachineEmiRecipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class MachineRecipe {
    private String id;
    private MachineRecipeType type;
    private Map<Ingredient, Integer> inputItems = new LinkedHashMap<>();
    private List<ItemStack> outputItems = new ArrayList<>();
    private int time;
    private Integer heat;
    public MachineRecipe(String id, MachineRecipeType type) {
        this.id = id;
        this.type = type;
    }
    public MachineRecipe setInputItems(Map<Ingredient, Integer> inputs) {
        this.inputItems.putAll(inputs);
        return this;
    }
    public MachineRecipe setOutputItems(List<ItemStack> outputs) {
        this.outputItems.addAll(outputs);
        return this;
    }
    public MachineRecipe setTime(int time) {
        this.time = time;
        return this;
    }
    public MachineRecipe setHeat(int heat) {
        this.heat = heat;
        return this;
    }
    public String getId() {
        return id;
    }
    public MachineRecipeType getType() {
        return type;
    }
    public Map<Ingredient, Integer> getInputItems() {
        return inputItems;
    }
    public List<ItemStack> getOutputItems() {
        List<ItemStack> output = new ArrayList<>();
        for (ItemStack stack : outputItems) {
            output.add(stack.copy());
        }
        return output;
    }
    public int getTime() {
        return time;
    }
    public Integer getHeat() {
        return heat;
    }
    public boolean fullInputs(List<ItemStack> inputs) {
        Map<Item, Integer> inputCount = new HashMap<>(); //Items left of each type.
        Map<Ingredient, Integer> ingredientsNeeded = new HashMap<>(inputItems);
        for (ItemStack input : inputs) {
            Item item = input.getItem();
            if (inputCount.containsKey(item)) inputCount.put(item, inputCount.get(item) + input.getCount());
            else inputCount.put(item, input.getCount());
        }
        for (Ingredient ingredient : inputItems.keySet()) {
            for (Item item : inputCount.keySet()) {
                if (ingredient.test(new ItemStack(item))) {
                    if (inputItems.get(ingredient) > inputCount.get(item)) {
                        inputCount.put(item, 0);
                        ingredientsNeeded.put(ingredient, ingredientsNeeded.get(ingredient) - inputItems.get(ingredient));
                    } else {
                        inputCount.put(item, inputCount.get(item) - inputItems.get(ingredient));
                        ingredientsNeeded.put(ingredient, 0);
                    }
                }
            }
        }
        for (Integer count : ingredientsNeeded.values()) {
            if (count > 0) return false;
        }
        return true;
    }
    public int[] inputsForConsumption(List<ItemStack> inputs) {
        int[] consumed = new int[inputs.size()];
        Map<Ingredient, Integer> ingredientsNeeded = new HashMap<>(inputItems);
        List<ItemStack> inputCopy = new ArrayList<>();
        for (ItemStack input : inputs) inputCopy.add(input.copy());
        for (Ingredient ingredient : inputItems.keySet()) {
            for (int i = 0; i < inputs.size(); i++) {
                if (ingredientsNeeded.get(ingredient) > 0 && ingredient.test(inputs.get(i))) {
                    int used = Math.min(inputs.get(i).getCount(), ingredientsNeeded.get(ingredient));
                    consumed[i] += used;
                    ingredientsNeeded.put(ingredient, ingredientsNeeded.get(ingredient) - used);
                    inputCopy.get(i).shrink(used);
                }
            }
        }
        return consumed;
    }
    public boolean canOutput(List<ItemStack> outputSlots) {
        List<ItemStack> simulated = new ArrayList<>();
        for (ItemStack outputSlot : outputSlots) simulated.add(outputSlot.copy());
        for (ItemStack output : outputItems) {
            int remaining = output.getCount();
            //Try to fill slots of the same item type.
            for (ItemStack slot : simulated) {
                if (remaining <= 0) break;
                if (slot.isEmpty()) continue;
                if (!ItemStack.isSameItemSameComponents(slot, output)) continue;
                int space = slot.getMaxStackSize() - slot.getCount();
                int add = Math.min(space, remaining);
                slot.grow(add);
                remaining -= add;
            }
            //Try to fill empty slots.
            for (ItemStack slot : simulated) {
                if (remaining <= 0) break;
                if (!slot.isEmpty()) continue;
                int add = Math.min(output.getMaxStackSize(), remaining);
                simulated.set(simulated.indexOf(slot), output.copyWithCount(add));
                remaining -= add;
            }
            if (remaining > 0) return false;
        }
        return true;
    }
    public List<EmiIngredient> getEmiInputs() {
        List<EmiIngredient> emiIngredients = new ArrayList<>();
        for (Ingredient ingredient : inputItems.keySet()) emiIngredients.add(EmiIngredient.of(ingredient, inputItems.get(ingredient)));
        return emiIngredients;
    }
    public List<EmiStack> getEmiOutputs() {
        List<EmiStack> emiStacks = new ArrayList<>();
        for (ItemStack stack : outputItems) emiStacks.add(EmiStack.of(stack));
        return emiStacks;
    }
    public MachineEmiRecipe getEmiRecipe(ResourceLocation syntheticId, EmiRecipeCategory category) {
        return new MachineEmiRecipe(syntheticId, category, this, type);
    }
}
