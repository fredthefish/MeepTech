package com.minecraftmod.meeptech.logic.recipe;

import java.util.List;

import com.minecraftmod.meeptech.integration.MachineEmiHeatRecipe;
import com.minecraftmod.meeptech.integration.MachineEmiRecipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class MachineHeatRecipe extends MachineRecipe {
    private MachineRecipeHeatType type;
    private Ingredient ingredient;
    private int heat;
    public MachineHeatRecipe(String id, MachineRecipeHeatType type, Ingredient ingredient, int heat) {
        super(id, type);
        this.type = type;
        this.ingredient = ingredient;
        this.heat = heat;
    }
    public Ingredient getIngredient() {
        return ingredient;
    }
    public int getHeat() {
        return heat;
    }
    public boolean validInput(ItemStack stack) {
        return ingredient.test(stack);
    }
    public List<EmiIngredient> getEmiInputs() {
        return List.of(EmiIngredient.of(ingredient));
    }
    public List<EmiStack> getEmiOutputs() {
        return List.of();
    }
    @Override
    public MachineEmiRecipe getEmiRecipe(ResourceLocation syntheticId, EmiRecipeCategory category) {
        return new MachineEmiHeatRecipe(syntheticId, category, this, type);
    }
}
