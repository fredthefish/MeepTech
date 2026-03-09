package com.minecraftmod.meeptech;

import java.util.ArrayList;
import java.util.HashMap;

import com.minecraftmod.meeptech.logic.ModuleSlotType;
import com.minecraftmod.meeptech.logic.ModuleType;

public class ModModuleTypes {
    public static final HashMap<ModuleSlotType, String> MODULE_SLOT_TRANSLATION_KEYS = new HashMap<>() {{
        put(ModuleSlotType.MachineCore, "meeptech.moduleSlotType.machine_core");
        put(ModuleSlotType.HeatingCore, "meeptech.moduleSlotType.heating_core");
        put(ModuleSlotType.FireboxSlot, "meeptech.moduleSlotType.firebox_core");
    }};
    public static final ArrayList<ModuleType> MODULE_TYPES = new ArrayList<>();

    public static final ModuleType BASE_MODULE = addModuleType(new ModuleType("hull", ModuleSlotType.Base));
    public static final ModuleType MACHINE_CORE_SMELTER = addModuleType(new ModuleType("machine_core_smelter", ModuleSlotType.MachineCore));
    public static final ModuleType HEATING_CORE_SOLID_FUEL = addModuleType(new ModuleType("heating_core_solid_fuel", ModuleSlotType.HeatingCore));

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
        BASE_MODULE.addSubSlot("machine_core", ModuleSlotType.MachineCore);
        MACHINE_CORE_SMELTER.addSubSlot("heating_core", ModuleSlotType.HeatingCore);
        HEATING_CORE_SOLID_FUEL.addSubSlot("firebox_slot", ModuleSlotType.FireboxSlot);
    }
}
