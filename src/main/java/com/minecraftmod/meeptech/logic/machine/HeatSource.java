package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.recipe.MachineRecipeHeatType;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModule;

public class HeatSource extends MachineAttribute {
    private MachineRecipeHeatType heatType;
    private UIModule energyModule;
    private ArrayList<TrackedStat> trackedStats = new ArrayList<>();
    public HeatSource(String id, MachineRecipeHeatType heatType, UIModule energyModule, List<TrackedStat> trackedStats) {
        super(id);
        this.heatType = heatType;
        this.energyModule = energyModule;
        if (trackedStats != null) this.trackedStats.addAll(trackedStats);
    }
    public UIModule getEnergyUI() {
        return energyModule;
    }
    public List<TrackedStat> getTrackedStats() {
        return trackedStats;
    }
    public MachineRecipeHeatType getHeatType() {
        return heatType;
    }
}