package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModule;

public class MachineType extends MachineAttribute {
    private EnergySourceType energySource;
    private UIModule inputModule;
    private UIModule outputModule;
    private UIModule recipeModule;
    private ArrayList<TrackedStat> trackedStats = new ArrayList<>();
    public MachineType(String id, EnergySourceType energySource, UIModule inputModule, UIModule outputModule,
        UIModule recipeModule, List<TrackedStat> trackedStats) {
        super(id);
        this.energySource = energySource;
        this.inputModule = inputModule;
        this.outputModule = outputModule;
        this.recipeModule = recipeModule;
        if (trackedStats != null) this.trackedStats.addAll(trackedStats);
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
}