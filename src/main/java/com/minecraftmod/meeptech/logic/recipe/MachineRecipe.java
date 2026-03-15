package com.minecraftmod.meeptech.logic.recipe;

import java.util.List;

import com.minecraftmod.meeptech.integration.MachineEmiRecipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

public abstract class MachineRecipe {
    private String id;
    private MachineRecipeType type;
    public MachineRecipe(String id, MachineRecipeType type) {
        this.id = id;
        this.type = type;
    }
    public String getId() {
        return id;
    }
    public MachineRecipeType getType() {
        return type;
    }
    public abstract List<EmiIngredient> getEmiInputs();
    public abstract List<EmiStack> getEmiOutputs();
    public abstract MachineEmiRecipe getEmiRecipe(ResourceLocation syntheticId, EmiRecipeCategory category);
}
