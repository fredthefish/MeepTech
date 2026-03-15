package com.minecraftmod.meeptech.integration;

import com.minecraftmod.meeptech.helpers.Formatting;
import com.minecraftmod.meeptech.logic.recipe.MachineHeatRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeHeatType;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MachineEmiHeatRecipe extends MachineEmiRecipe {
    private final MachineHeatRecipe recipe;
    private final MachineRecipeHeatType type;
    public MachineEmiHeatRecipe(ResourceLocation syntheticId, EmiRecipeCategory category, MachineHeatRecipe recipe, MachineRecipeHeatType type) {
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
        if (recipe.getHeat() > 0) {
            int x = 2;
            int y = 52;
            widgets.addText(Component.literal("Heat"), x + 1, y + 1, 0x404040, false);
            String heatString = Formatting.doubleFormatting((double)recipe.getHeat() / 20.0);
            widgets.addText(Component.literal(heatString + "s"), x + 1, y + 15, 0x404040, false);
        }
    }
}
