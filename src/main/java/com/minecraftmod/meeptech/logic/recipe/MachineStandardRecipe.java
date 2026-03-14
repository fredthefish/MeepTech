package com.minecraftmod.meeptech.logic.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.emi.MachineEmiRecipe;
import com.minecraftmod.meeptech.emi.MachineEmiStandardRecipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class MachineStandardRecipe extends MachineRecipe {
    private String id;
    private MachineRecipeStandardType type;
    private Map<Ingredient, Integer> inputItems = new HashMap<>();
    private List<ItemStack> outputItems = new ArrayList<>();
    private int time;
    public MachineStandardRecipe(String id, MachineRecipeStandardType type, Map<Ingredient, Integer> inputs, List<ItemStack> outputs, int time) {
        super(id);
        this.type = type;
        this.inputItems.putAll(inputs);
        this.outputItems.addAll(outputs);
        this.time = time;
    }
    public String getId() {
        return id;
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
    public boolean validInput(ItemStack stack) {
        for (Ingredient ingredient : inputItems.keySet()) {
            if (ingredient.test(stack) && stack.getCount() >= inputItems.get(ingredient)) return true;
        }
        return false;
    }
    //Note: This is a greedy algorithm. 
    //In a scenario where the recipe wants (tag with item, item) and you input (item, item with tag), it will return false.
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
    public Map<Item, Integer> inputsForConsumption(List<ItemStack> inputs) {
        Map<Item, Integer> inputCount = new HashMap<>(); //Items left of each type.
        Map<Item, Integer> itemsConsumed = new HashMap<>();
        Map<Ingredient, Integer> ingredientsNeeded = new HashMap<>(inputItems);
        for (ItemStack input : inputs) {
            Item item = input.getItem();
            if (inputCount.containsKey(item)) inputCount.put(item, inputCount.get(item) + input.getCount());
            else inputCount.put(item, input.getCount());
            itemsConsumed.put(item, 0);
        }
        for (Ingredient ingredient : inputItems.keySet()) {
            for (Item item : inputCount.keySet()) {
                if (ingredient.test(new ItemStack(item))) {
                    if (inputItems.get(ingredient) > inputCount.get(item)) {
                        itemsConsumed.put(item, itemsConsumed.get(item) + inputCount.get(item));
                        inputCount.put(item, 0);
                        ingredientsNeeded.put(ingredient, ingredientsNeeded.get(ingredient) - inputItems.get(ingredient));
                    } else {
                        itemsConsumed.put(item, itemsConsumed.get(item) + inputItems.get(ingredient));
                        inputCount.put(item, inputCount.get(item) - inputItems.get(ingredient));
                        ingredientsNeeded.put(ingredient, 0);
                    }
                }
            }
        }
        return itemsConsumed;
    }
    @Override
    public List<EmiIngredient> getEmiInputs() {
        List<EmiIngredient> emiIngredients = new ArrayList<>();
        for (Ingredient ingredient : inputItems.keySet()) emiIngredients.add(EmiIngredient.of(ingredient));
        return emiIngredients;
    }
    @Override
    public List<EmiStack> getEmiOutputs() {
        List<EmiStack> emiStacks = new ArrayList<>();
        for (ItemStack stack : outputItems) emiStacks.add(EmiStack.of(stack));
        return emiStacks;
    }
    @Override
    public MachineEmiRecipe getEmiRecipe(ResourceLocation syntheticId, EmiRecipeCategory category) {
        return new MachineEmiStandardRecipe(syntheticId, category, this, type);
    }
}
