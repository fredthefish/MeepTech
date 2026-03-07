package com.minecraftmod.meeptech.logic;

import java.util.ArrayList;

public class MachineType {
    private final String id;
    private final ArrayList<MachineComponent> components;
    private final int inputSlots;
    private final int outputSlots;

    public MachineType(String id, int inputSlots, int outputSlots) {
        this.id = id;
        this.components = new ArrayList<MachineComponent>();
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
    }
    public String getId() {
        return id;
    }
    public String getTranslationKey() {
        return "meeptech.machineType." + id;
    }
    public ArrayList<MachineComponent> getComponents() {
        return components;
    }
    public void addMachineComponent(MachineComponent component) {
        components.add(component);
    }
    public int getInventorySlots() {
        return this.inputSlots + this.outputSlots;
    }
}
