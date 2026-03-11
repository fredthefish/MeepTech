package com.minecraftmod.meeptech.logic.machine;

public class MachineBase extends MachineAttribute {
    public MachineBase(String id) {
        super(id);
    }
    public String getTranslationKey() {
        return "meeptech.moduleType." + getId();
    }
}