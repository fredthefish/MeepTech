package com.minecraftmod.meeptech.logic.machine;

import com.minecraftmod.meeptech.logic.ui.UIModule;

public class MachineType extends MachineAttribute {
    @SuppressWarnings("unused")
    private EnergySourceType energySource; //TODO: Improve it so that the MachineData reads things in order and thus this can be used.
    private UIModule inputModule;
    private UIModule outputModule;
    private UIModule recipeModule;
    public MachineType(String id, EnergySourceType energySource, UIModule inputModule, UIModule outputModule, UIModule recipeModule) {
        super(id);
        this.energySource = energySource;
        this.inputModule = inputModule;
        this.outputModule = outputModule;
        this.recipeModule = recipeModule;
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
}