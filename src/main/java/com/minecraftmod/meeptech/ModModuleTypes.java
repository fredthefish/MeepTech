package com.minecraftmod.meeptech;

import java.util.ArrayList;

import com.minecraftmod.meeptech.logic.ModuleSlotType;
import com.minecraftmod.meeptech.logic.ModuleType;

public class ModModuleTypes {
    public static final ArrayList<ModuleType> MODULE_TYPES = new ArrayList<>();

    public static final ModuleType MACHINE_CORE_SMELTER = addModuleType(new ModuleType("machine_core_smelter", ModuleSlotType.MachineCore));
    public static final ModuleType HEATING_CORE_SOLID_FUEL = addModuleType(new ModuleType("heating_core_solid_fuel", ModuleSlotType.HeatingCore));

    public static ModuleType addModuleType(ModuleType type) {
        MODULE_TYPES.add(type);
        return type;
    }
    
    public static void InitializeModuleTypes() {
        MACHINE_CORE_SMELTER.addSubSlot("heating_core", ModuleSlotType.HeatingCore);
        HEATING_CORE_SOLID_FUEL.addSubSlot("firebox_slot", ModuleSlotType.FireboxSlot);
    }
}
