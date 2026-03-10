package com.minecraftmod.meeptech;

import java.util.ArrayList;
import java.util.HashMap;

import com.minecraftmod.meeptech.logic.MaterialForm;
import com.minecraftmod.meeptech.logic.ModuleSlotType;
import com.minecraftmod.meeptech.logic.ModuleType;

public class ModModuleTypes {
    public static final HashMap<ModuleSlotType, String> MODULE_SLOT_TRANSLATION_KEYS = new HashMap<>() {{
        put(ModuleSlotType.MachineCore, "meeptech.moduleSlotType.machine_core");
        put(ModuleSlotType.HeatingCore, "meeptech.moduleSlotType.heating_core");
        put(ModuleSlotType.FireboxSlot, "meeptech.moduleSlotType.firebox_slot");
    }};
    public static final ArrayList<ModuleType> MODULE_TYPES = new ArrayList<>();

    public static final ModuleType BRICK_HULL = addModuleType(new ModuleType("brick_hull", ModuleSlotType.MachineBase));
    public static final ModuleType MACHINE_CORE_SMELTER = addModuleType(new ModuleType("machine_core_smelter", ModuleSlotType.MachineCore));
    public static final ModuleType HEATING_CORE_SOLID_FUEL = addModuleType(new ModuleType("heating_core_solid_fuel", ModuleSlotType.HeatingCore));
    public static final ModuleType FIREBOX_SLOT = addModuleType(new ModuleType("firebox_slot", ModuleSlotType.FireboxSlot));

    public static ModuleType addModuleType(ModuleType type) {
        MODULE_TYPES.add(type);
        return type;
    }
    public static ModuleType getModuleType(String moduleId) {
        for (ModuleType module : MODULE_TYPES) {
            if (module.getId().equals(moduleId)) return module;
        }
        return null;
    }
    public static void InitializeModuleTypes() {
        BRICK_HULL.addSubSlot("machine_core", ModuleSlotType.MachineCore);
        BRICK_HULL.setAssociatedItem(ModBlocks.BLOCK_ITEMS.get(ModBlocks.BRICK_HULL));
        MACHINE_CORE_SMELTER.addSubSlot("heating_core", ModuleSlotType.HeatingCore);
        HEATING_CORE_SOLID_FUEL.addSubSlot("firebox_slot", ModuleSlotType.FireboxSlot);
        FIREBOX_SLOT.setMaterialForm(MaterialForm.Box);
    }
}
