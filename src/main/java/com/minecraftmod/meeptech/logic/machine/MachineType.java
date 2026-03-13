package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.recipe.MachineRecipeStandardType;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;
import com.minecraftmod.meeptech.logic.ui.RecipeUIModule;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModule;
import com.minecraftmod.meeptech.logic.ui.UIModuleType;

public class MachineType extends MachineAttribute {
    private EnergySourceType energySource;
    private MachineRecipeType recipeType;
    private UIModule inputModule;
    private UIModule outputModule;
    private UIModule recipeModule;
    private ArrayList<TrackedStat> trackedStats = new ArrayList<>();
    public MachineType(String id, EnergySourceType energySource, MachineRecipeType recipeType) {
        super(id);
        this.energySource = energySource;
        this.recipeType = recipeType;
        if (recipeType != null) {
            if (recipeType instanceof MachineRecipeStandardType standardRecipeType) {
                this.inputModule = new UIModule(UIModuleType.Input, "Input", standardRecipeType.getInputSlots());
                this.outputModule = new UIModule(UIModuleType.Output, "Output", standardRecipeType.getOutputSlots());
            }
            this.recipeModule = new RecipeUIModule("Recipe", true);
            this.trackedStats.addAll(List.of(TrackedStat.RecipeProgress, TrackedStat.RecipeMaxProgress));
        }
    }
    public String getTranslationKey() {
        return "meeptech.moduleType." + getId();
    }
    public UIModule getInputUI() {
        return inputModule;
    }
    public UIModule getOutputUI() {
        return outputModule;
    }
    public UIModule getRecipeUI() {
        return recipeModule;
    }
    public List<TrackedStat> getTrackedStats() {
        return trackedStats;
    }
    public EnergySourceType getEnergySource() {
        return energySource;
    }
    public MachineRecipeType getRecipeType() {
        return recipeType;
    }
}