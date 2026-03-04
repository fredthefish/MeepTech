package com.minecraftmod.meeptech.logic;

import java.util.ArrayList;

public class MachineType {
    private final String id;
    private final ArrayList<MachineComponent> components;

    public MachineType(String id) {
        this.id = id;
        this.components = new ArrayList<MachineComponent>();
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
}
