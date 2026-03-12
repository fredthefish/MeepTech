package com.minecraftmod.meeptech.logic.ui;

public class EnergyUIModule extends UIModule {
    private boolean hasHeatIcon;
    public EnergyUIModule(String title, int slotCount, boolean hasHeatIcon) {
        super(UIModuleType.Energy, title, slotCount);
        this.hasHeatIcon = hasHeatIcon;
    }
    public boolean hasHeatIcon() {
        return hasHeatIcon;
    }
}
