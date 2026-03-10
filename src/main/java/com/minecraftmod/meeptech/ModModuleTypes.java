package com.minecraftmod.meeptech;

import java.util.ArrayList;

import com.minecraftmod.meeptech.logic.MachineAttributes;
import com.minecraftmod.meeptech.logic.MaterialForm;
import com.minecraftmod.meeptech.logic.ModuleSlotType;
import com.minecraftmod.meeptech.logic.ModuleType;

public class ModModuleTypes {
    public static final ArrayList<ModuleSlotType> MODULE_SLOT_TYPES = new ArrayList<>();
    public static final ArrayList<ModuleType> MODULE_TYPES = new ArrayList<>();
    
    public static final ModuleSlotType SLOT_MACHINE_BASE = addModuleSlotType(new ModuleSlotType("machine_base"));
    public static final ModuleSlotType SLOT_MACHINE_CORE = addModuleSlotType(new ModuleSlotType("machine_core"));
    public static final ModuleSlotType SLOT_HEATING_CORE = addModuleSlotType(new ModuleSlotType("heating_core"));
    public static final ModuleSlotType SLOT_FIREBOX = addModuleSlotType(new ModuleSlotType("firebox_slot"));

    public static final ModuleType BRICK_HULL = addModuleType(
        new ModuleType("brick_hull", SLOT_MACHINE_BASE, MachineAttributes.BASE_BASIC));
    public static final ModuleType MACHINE_CORE_SMELTER = addModuleType(
        new ModuleType("machine_core_smelter", SLOT_MACHINE_CORE, MachineAttributes.TYPE_SMELTER));
    public static final ModuleType HEATING_CORE_SOLID_FUEL = addModuleType(
        new ModuleType("heating_core_solid_fuel", SLOT_HEATING_CORE, MachineAttributes.HEAT_SOURCE_SOLID_FUEL));
    public static final ModuleType FIREBOX_SLOT = addModuleType(
        new ModuleType("firebox_slot", SLOT_FIREBOX, MachineAttributes.COMPONENT_FIREBOX));

    private static ModuleType addModuleType(ModuleType type) {
        MODULE_TYPES.add(type);
        return type;
    }
    private static ModuleSlotType addModuleSlotType(ModuleSlotType type) {
        MODULE_SLOT_TYPES.add(type);
        return type;
    }
    public static ModuleType getModuleType(String moduleId) {
        for (ModuleType module : MODULE_TYPES) {
            if (module.getId().equals(moduleId)) return module;
        }
        return null;
    }
    public static ModuleSlotType getModuleSlotType(String slotTypeId) {
        for (ModuleSlotType slot : MODULE_SLOT_TYPES) {
            if (slot.getId().equals(slotTypeId)) return slot;
        }
        return null;
    }
    public static void InitializeModuleTypes() {
        SLOT_MACHINE_BASE.setMaterialForm(MaterialForm.Hull);
        SLOT_FIREBOX.setMaterialForm(MaterialForm.Box);

        BRICK_HULL.addSubSlot("machine_core", SLOT_MACHINE_CORE);
        BRICK_HULL.setAssociatedItem(ModBlocks.BRICK_HULL_ITEM);
        MACHINE_CORE_SMELTER.addSubSlot("heating_core", SLOT_HEATING_CORE);
        HEATING_CORE_SOLID_FUEL.addSubSlot("firebox_slot", SLOT_FIREBOX);
        FIREBOX_SLOT.setMaterialForm(MaterialForm.Box);
    }
}
