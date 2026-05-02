package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModule;

public class EnergySource extends MachineAttribute {
    private MachineRecipeType energyType;
    private EnergySourceType energySourceType;
    private UIModule energyModule;
    private ArrayList<TrackedStat> trackedStats = new ArrayList<>();
    public EnergySource(String id, MachineRecipeType energyType, EnergySourceType energySourceType, UIModule energyModule, List<TrackedStat> trackedStats) {
        super(id);
        this.energyType = energyType;
        this.energySourceType = energySourceType;
        this.energyModule = energyModule;
        if (trackedStats != null) this.trackedStats.addAll(trackedStats);
    }
    public UIModule getEnergyUI() {
        return energyModule;
    }
    public List<TrackedStat> getTrackedStats() {
        return trackedStats;
    }
    public MachineRecipeType getEnergyType() {
        return energyType;
    }
    public EnergySourceType getEnergySourceType() {
        return energySourceType;
    }
}