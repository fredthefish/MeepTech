package com.minecraftmod.meeptech.integration;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.MeepTech;
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
    private final List<EmiIngredient> inputItems = new ArrayList<>();
    private final List<EmiIngredient> catalystItems = new ArrayList<>();
    private final List<EmiStack> outputItems = new ArrayList<>();
    private final List<EmiIngredient> inputFluids = new ArrayList<>();
    private final List<EmiIngredient> catalystFluids = new ArrayList<>();
    private final List<EmiStack> outputFluids = new ArrayList<>();
    private final MachineRecipe recipe;
    private final MachineRecipeType type;
    public MachineEmiRecipe(ResourceLocation id, EmiRecipeCategory category, MachineRecipe recipe, MachineRecipeType type) {
        this.category = category;
        this.id = id;
        this.recipe = recipe;
        this.type = type;
        this.inputItems.addAll(recipe.getEmiInputs());
        this.outputItems.addAll(recipe.getEmiOutputs());
        this.inputFluids.addAll(recipe.getEmiFluidInputs());
        this.outputFluids.addAll(recipe.getEmiFluidOutputs());
        this.catalystItems.addAll(recipe.getEmiCatalystItems());
        this.catalystFluids.addAll(recipe.getEmiCatalystFluids());
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
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.addAll(inputItems);
        inputs.addAll(inputFluids);
        return inputs;
    }
    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> outputs = new ArrayList<>();
        outputs.addAll(outputItems);
        outputs.addAll(outputFluids);
        return outputs;
    }
    @Override
    public List<EmiIngredient> getCatalysts() {
        List<EmiIngredient> catalysts = new ArrayList<>();
        catalysts.addAll(catalystItems);
        catalysts.addAll(catalystFluids);
        return catalysts;
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
        ResourceLocation quadrants = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "textures/gui/quadrants_ui.png");
        widgets.addTexture(quadrants, 0, 0, 164, 102, 0, 0, 164, 102, 164, 102);
        if (!inputItems.isEmpty() || !inputFluids.isEmpty() || !catalystItems.isEmpty() || !catalystFluids.isEmpty()) {
            int x = 2;
            int y = 2;
            widgets.addText(Component.literal("Inputs"), x + 1, y + 1, 0x404040, false);
            int slots = 0;
            for (int i = 0; i < inputItems.size(); i++) {
                widgets.addSlot(inputItems.get(i), x + 3 + 17 * slots++, y + 13);
            }
            for (int i = 0; i < catalystItems.size(); i++) {
                widgets.addSlot(catalystItems.get(i), x + 3 + 17 * slots++, y + 13).catalyst(true);
            }
            for (int i = 0; i < inputFluids.size(); i++) {
                EmiIngredient tank = inputFluids.get(i);
                widgets.addTank(tank, x + 3 + 17 * slots++, y + 13, 18, 18, (int)tank.getAmount());
            }
            for (int i = 0; i < catalystFluids.size(); i++) {
                EmiIngredient tank = catalystFluids.get(i);
                widgets.addTank(tank, x + 3 + 17 * slots++, y + 13, 18, 18, (int)tank.getAmount()).catalyst(true);
            }
        }
        if (!outputItems.isEmpty() || !outputFluids.isEmpty()) {
            int x = 83;
            int y = 2;
            widgets.addText(Component.literal("Outputs"), x + 1, y + 1, 0x404040, false);
            int slots = 0;
            for (int i = 0; i < outputItems.size(); i++) {
                widgets.addSlot(outputItems.get(i), x + 3 + 17 * slots++, y + 13).recipeContext(this);
            }
            for (int i = 0; i < outputFluids.size(); i++) {
                EmiIngredient tank = outputFluids.get(i + type.getOutputSlots());
                widgets.addTank(tank, x + 3 + 17 * slots++, y + 13, 18, 18, (int)tank.getAmount()).recipeContext(this);
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
