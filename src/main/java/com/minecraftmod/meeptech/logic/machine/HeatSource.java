package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModule;

public class HeatSource extends MachineAttribute {
    private UIModule energyModule;
    private ArrayList<TrackedStat> trackedStats = new ArrayList<>();
    public HeatSource(String id, UIModule energyModule, List<TrackedStat> trackedStats) {
        super(id);
        this.energyModule = energyModule;
        if (trackedStats != null) this.trackedStats.addAll(trackedStats);
    }
    public UIModule getEnergyUI() {
        return energyModule;
    }
    public List<TrackedStat> getTrackedStats() {
        return trackedStats;
    }
}