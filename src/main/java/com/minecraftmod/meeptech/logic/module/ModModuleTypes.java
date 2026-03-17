package com.minecraftmod.meeptech.logic.module;

import java.util.ArrayList;

import com.minecraftmod.meeptech.logic.material.ModMaterials;

public class ModModuleTypes {
    public static final ArrayList<ModuleSlotType> MODULE_SLOT_TYPES = new ArrayList<>();
    public static final ArrayList<ModuleType> MODULE_TYPES = new ArrayList<>();
    
    public static final ModuleSlotType SLOT_MACHINE_BASE = addModuleSlotType(new ModuleSlotType("machine_base"));
    public static final ModuleSlotType SLOT_MACHINE_CORE = addModuleSlotType(new ModuleSlotType("machine_core"));
    public static final ModuleSlotType SLOT_HEATING_CORE = addModuleSlotType(new ModuleSlotType("heating_core"));
    public static final ModuleSlotType SLOT_FIREBOX = addModuleSlotType(new ModuleSlotType("firebox_slot"));

    public static final ModuleSlotType UPGRADE_SLOT_MACHINE_CORE = addModuleSlotType(new ModuleSlotType("upgrade_machine_core"));
    public static final ModuleSlotType UPGRADE_SLOT_SMELTER = addModuleSlotType(new ModuleSlotType("upgrade_machine_core_smelter"));

    public static final ModuleType MACHINE_CORE_SMELTER = addModuleType(
        new ModuleType("machine_core_smelter", SLOT_MACHINE_CORE, ModModuleData.TYPE_SMELTER, UPGRADE_SLOT_SMELTER));
    public static final ModuleType HEATING_CORE_SOLID_FUEL = addModuleType(
        new ModuleType("heating_core_solid_fuel", SLOT_HEATING_CORE, ModModuleData.HEAT_SOURCE_SOLID_FUEL, null));
    public static final ModuleType FIREBOX_SLOT = addModuleType(
        new ModuleType("firebox_slot", SLOT_FIREBOX, ModModuleData.COMPONENT_FIREBOX, null));
    public static final ModuleType UPGRADE_SMELTER_BLASTING = 
        addModuleType(new ModuleType("upgrade_smelter_blasting", UPGRADE_SLOT_SMELTER, ModModuleData.UPGRADE_BLASTING, null));

    public static ModuleType addModuleType(ModuleType type) {
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
    public static void initializeModuleTypes() {
        SLOT_MACHINE_BASE.setMaterialForm(ModMaterials.HULL);
        SLOT_FIREBOX.setMaterialForm(ModMaterials.BOX);
        UPGRADE_SLOT_SMELTER.addSubType(UPGRADE_SLOT_MACHINE_CORE);

        MACHINE_CORE_SMELTER.addSubSlot("heating_core", SLOT_HEATING_CORE);
        HEATING_CORE_SOLID_FUEL.addSubSlot("firebox_slot", SLOT_FIREBOX);
        FIREBOX_SLOT.setMaterialForm(ModMaterials.BOX);
    }
}
