package com.minecraftmod.meeptech.integration;

import java.util.List;

import com.minecraftmod.meeptech.helpers.Formatting;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MachineEmiRecipe implements EmiRecipe {
    private final EmiRecipeCategory category;
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final MachineRecipe recipe;
    private final MachineRecipeType type;
    public MachineEmiRecipe(ResourceLocation id, EmiRecipeCategory category, MachineRecipe recipe, MachineRecipeType type) {
        this.category = category;
        this.id = id;
        this.recipe = recipe;
        this.type = type;
        this.inputs = recipe.getEmiInputs();
        this.outputs = recipe.getEmiOutputs();
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
    @Override
    public int getDisplayWidth() {
        return 164;
    }
    @Override
    public int getDisplayHeight() {
        return 102;
    }
    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation quadrants = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/quadrants_ui.png");
        widgets.addTexture(quadrants, 0, 0, 164, 102, 0, 0, 164, 102, 164, 102);
        if (type.getInputSlots() > 0) {
            int x = 2;
            int y = 2;
            widgets.addText(Component.literal("Inputs"), x + 1, y + 1, 0x404040, false);
            for (int i = 0; i < type.getInputSlots(); i++) {
                widgets.addSlot(getInputs().get(i), x + 3 + 17 * i, y + 13);
            }
        }
        if (type.getOutputSlots() > 0) {
            int x = 83;
            int y = 2;
            widgets.addText(Component.literal("Outputs"), x + 1, y + 1, 0x404040, false);
            for (int i = 0; i < type.getOutputSlots(); i++) {
                widgets.addSlot(getOutputs().get(i), x + 3 + 17 * i, y + 13).recipeContext(this);
            }
        }
        if (recipe.getHeat() > 0) {
            int x = 2;
            int y = 52;
            widgets.addText(Component.literal("Heat"), x + 1, y + 1, 0x404040, false);
            String heatString = Formatting.doubleFormatting((double)recipe.getHeat() / 20.0);
            widgets.addText(Component.literal(heatString + "s"), x + 1, y + 15, 0x404040, false);
        }
        if (recipe.getTime() > 0) {
            int x = 83;
            int y = 52;
            widgets.addText(Component.literal("Recipe"), x + 1, y + 1, 0x404040, false);
            String timeString = Formatting.doubleFormatting((double)recipe.getTime() / 20.0);
            widgets.addText(Component.literal(timeString + "s"), x + 1, y + 15, 0x404040, false);
        }
    }
}
