package com.minecraftmod.meeptech.logic.machine;

public class MachineBase extends MachineAttribute {
    private int upgradeSlots;
    public MachineBase(String id, int upgradeSlots) {
        super(id);
        this.upgradeSlots = upgradeSlots;
    }
    public String getTranslationKey() {
        return "meeptech.moduleType." + getId();
    }
    public int getUpgradeSlots() {
        return upgradeSlots;
    }
}