package com.minecraftmod.meeptech.logic.machine;

import com.minecraftmod.meeptech.logic.ui.UIModule;

public class HeatSource extends MachineAttribute {
    private UIModule energyModule;
    public HeatSource(String id, UIModule energyModule) {
        super(id);
        this.energyModule = energyModule;
    }
    public UIModule getEnergyUI() {
        return energyModule;
    }
}