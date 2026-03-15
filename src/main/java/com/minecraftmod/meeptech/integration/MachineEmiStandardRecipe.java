package com.minecraftmod.meeptech.integration;

import com.minecraftmod.meeptech.helpers.Formatting;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeStandardType;
import com.minecraftmod.meeptech.logic.recipe.MachineStandardRecipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MachineEmiStandardRecipe extends MachineEmiRecipe {
    private final MachineStandardRecipe recipe;
    private final MachineRecipeStandardType type;
    public MachineEmiStandardRecipe(ResourceLocation syntheticId, EmiRecipeCategory category, MachineStandardRecipe recipe, MachineRecipeStandardType type) {
        super(syntheticId, category, recipe.getEmiInputs(), recipe.getEmiOutputs());
        this.recipe = recipe;
        this.type = type;
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
        if (recipe.getTime() > 0) {
            int x = 83;
            int y = 52;
            widgets.addText(Component.literal("Recipe"), x + 1, y + 1, 0x404040, false);
            String timeString = Formatting.doubleFormatting((double)recipe.getTime() / 20.0);
            widgets.addText(Component.literal(timeString + "s"), x + 1, y + 15, 0x404040, false);
        }
    }
}
