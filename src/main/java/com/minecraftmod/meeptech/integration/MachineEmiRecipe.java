package com.minecraftmod.meeptech.integration;

import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public abstract class MachineEmiRecipe implements EmiRecipe {
    private final EmiRecipeCategory category;
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    public MachineEmiRecipe(ResourceLocation id, EmiRecipeCategory category, List<EmiIngredient> inputs, List<EmiStack> outputs) {
        this.category = category;
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
    }
    @Override
    public EmiRecipeCategory getCategory() {
        return this.category;
    }
    @Override
    public ResourceLocation getId() {
        return this.id;
    }
    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }
    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }
}
