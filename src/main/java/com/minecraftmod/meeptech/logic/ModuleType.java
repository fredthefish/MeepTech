package com.minecraftmod.meeptech.logic;

import java.util.HashMap;
import java.util.Map;

public class ModuleType {
    private final String id;
    private final ModuleSlotType type;
    private final HashMap<String, ModuleSlotType> subSlots;
    public ModuleType(String id, ModuleSlotType type) {
        this.id = id;
        this.type = type;
        this.subSlots = new HashMap<>();
    }
    public String getId() {
        return id;
    }
    public String getModuleId() {
        return "module_" + id;
    }
    public ModuleSlotType getType() {
        return type;
    }
    public Map<String, ModuleSlotType> getSubSlots() {
        return subSlots;
    }
    public void addSubSlot(String subSlotId, ModuleSlotType subSlotType) {
        subSlots.put(subSlotId, subSlotType);
    }
}
