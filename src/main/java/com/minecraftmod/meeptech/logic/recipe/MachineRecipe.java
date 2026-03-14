package com.minecraftmod.meeptech.logic.recipe;

import java.util.List;

import com.minecraftmod.meeptech.emi.MachineEmiRecipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public abstract class MachineRecipe {
    private String id;
    public MachineRecipe(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public abstract List<EmiIngredient> getEmiInputs();
    public abstract List<EmiStack> getEmiOutputs();
    public abstract MachineEmiRecipe getEmiRecipe(ResourceLocation syntheticId, EmiRecipeCategory category);
}
