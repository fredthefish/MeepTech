package com.minecraftmod.meeptech.integration;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialWorkstationRecipe;
import com.minecraftmod.meeptech.registries.ModTags;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class WorkstationEmiRecipe implements EmiRecipe {
    private final EmiRecipeCategory category;
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    public WorkstationEmiRecipe(ResourceLocation id, EmiRecipeCategory category, Material material, MaterialWorkstationRecipe recipe) {
        this.id = id;
        this.category = category;
        EmiIngredient hammerIngredient = EmiIngredient.of(Ingredient.of(ModTags.HAMMER_TAG));
        List<EmiIngredient> hammers = new ArrayList<>();
        for (EmiStack hammerStack : hammerIngredient.getEmiStacks()) {
            EmiStack remainderStack = hammerStack.copy();
            ItemStack damagedStack = remainderStack.getItemStack().copy();
            if (damagedStack.isDamageableItem()) {
                damagedStack.setDamageValue(damagedStack.getDamageValue() + 1);
                remainderStack.setRemainder(EmiStack.of(damagedStack));
            } else remainderStack.setRemainder(remainderStack);
            hammers.add(remainderStack);
        }
        this.inputs = List.of(
            EmiIngredient.of(Ingredient.of(material.getForm(recipe.getInputForm())), recipe.getInputAmount()), 
            EmiIngredient.of(hammers));
        this.outputs = List.of(EmiStack.of(material.getForm(recipe.getOutputForm())));
    }
    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }
    @Override
    public ResourceLocation getId() {
        return id;
    }
    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }
    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }
    @Override
    public int getDisplayWidth() {
        return 66;
    }
    @Override
    public int getDisplayHeight() {
        return 38;
    }
    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addSlot(inputs.get(1), 0, 20).catalyst(true);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 20, 10);
        widgets.addSlot(outputs.get(0), 45, 10).recipeContext(this);
    }
}
