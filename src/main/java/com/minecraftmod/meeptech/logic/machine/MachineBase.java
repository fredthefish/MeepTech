package com.minecraftmod.meeptech.logic.machine;

public class MachineBase extends MachineAttribute {
    private final int upgradeSlots;
    private final int moduleTier;
    public MachineBase(String id, int upgradeSlots, int moduleTier) {
        super(id);
        this.upgradeSlots = upgradeSlots;
        this.moduleTier = moduleTier;
    }
    public String getTranslationKey() {
        return "meeptech.moduleType." + getId();
    }
    public int getUpgradeSlots() {
        return upgradeSlots;
    }
    public int getModuleTier() {
        return moduleTier;
    }
}