package com.minecraftmod.meeptech.logic.ui;

public class EnergyUIModule extends UIModule {
    private boolean hasHeatIcon;
    public EnergyUIModule(String title, int slotCount, int tankCount, boolean hasHeatIcon) {
        super(UIModuleType.Energy, title, slotCount, tankCount);
        this.hasHeatIcon = hasHeatIcon;
    }
    public boolean hasHeatIcon() {
        return hasHeatIcon;
    }
}
