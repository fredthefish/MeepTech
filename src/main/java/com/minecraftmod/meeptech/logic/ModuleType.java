package com.minecraftmod.meeptech.logic;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.items.ModuleItems;

import net.minecraft.world.item.Item;

public class ModuleType {
    private final String id;
    private final ModuleSlotType type;
    private final List<ModuleSlot> subSlots;
    public ModuleType(String id, ModuleSlotType type) {
        this.id = id;
        this.type = type;
        this.subSlots = new ArrayList<>();
    }
    public String getId() {
        return id;
    }
    public String getModuleId() {
        return "module_" + id;
    }
    public Item getItem() {
        return ModuleItems.MODULES.get(id).get();
    }
    public ModuleSlotType getType() {
        return type;
    }
    public ModuleSlot getSubSlot(int index) {
        return subSlots.get(index);
    }
    public int getSubSlotCount() {
        return subSlots.size();
    }
    public void addSubSlot(String subSlotId, ModuleSlotType subSlotType) {
        subSlots.add(new ModuleSlot(subSlotId, subSlotType));
    }
    public MachineConfigData getEmptyMachineConfigData() {
        ArrayList<MachineConfigData> subModules = new ArrayList<>();
        for (int i = 0; i < getSubSlotCount(); i++) {
            subModules.add(null);
        }
        return new MachineConfigData(id, subModules);
    }
}
