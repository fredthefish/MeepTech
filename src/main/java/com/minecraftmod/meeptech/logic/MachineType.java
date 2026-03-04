package com.minecraftmod.meeptech.logic;

import java.util.ArrayList;

public class MachineType {
    private final String id;
    private final String displayName;
    private final ArrayList<MachineComponent> components;

    public MachineType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.components = new ArrayList<MachineComponent>();
    }
    public String getId() {
        return id;
    }
    public String getDisplayName() {
        return displayName;
    }
    public ArrayList<MachineComponent> getComponents() {
        return components;
    }
    public void addMachineComponent(MachineComponent component) {
        components.add(component);
    }
}
