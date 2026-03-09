package com.minecraftmod.meeptech.logic;

public class ModuleSlot {
    private final String id;
    private final ModuleSlotType type;
    public ModuleSlot(String id, ModuleSlotType type) {
        this.id = id;
        this.type = type;
    }
    public String getId() {
        return id;
    }
    public ModuleSlotType getType() {
        return type;
    }
}
