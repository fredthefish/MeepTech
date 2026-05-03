package com.minecraftmod.meeptech.logic.module;

import java.util.ArrayList;

import com.minecraftmod.meeptech.logic.material.MaterialForm;

public class ModModuleTypes {
    public static final ArrayList<ModuleSlotType> MODULE_SLOT_TYPES = new ArrayList<>();
    public static final ArrayList<ModuleType> MODULE_TYPES = new ArrayList<>();
    
    public static final ModuleSlotType SLOT_MACHINE_BASE = addModuleSlotType(new ModuleSlotType("machine_base"));
    public static final ModuleSlotType SLOT_MACHINE_CORE = addModuleSlotType(new ModuleSlotType("machine_core"));
    public static final ModuleSlotType SLOT_ENERGY_CORE = addModuleSlotType(new ModuleSlotType("energy_core"));
    public static final ModuleSlotType SLOT_HEATING_CORE = addModuleSlotType(new ModuleSlotType("heating_core"));
    public static final ModuleSlotType SLOT_POWER_CORE = addModuleSlotType(new ModuleSlotType("power_core"));

    public static final ModuleSlotType UPGRADE_SLOT_MACHINE_CORE = addModuleSlotType(new ModuleSlotType("upgrade_machine_core"));
    public static final ModuleSlotType UPGRADE_SLOT_SMELTER = addModuleSlotType(new ModuleSlotType("upgrade_machine_core_smelter"));

    public static final ModuleType MACHINE_CORE_SMELTER = addModuleType(new ModuleType("machine_core_smelter", SLOT_MACHINE_CORE, ModModuleData.TYPE_SMELTER));
    public static final ModuleType MACHINE_CORE_ALLOYER = addModuleType(new ModuleType("machine_core_alloyer", SLOT_MACHINE_CORE, ModModuleData.TYPE_ALLOYER));
    public static final ModuleType MACHINE_CORE_COKER = addModuleType(new ModuleType("machine_core_coker", SLOT_MACHINE_CORE, ModModuleData.TYPE_COKER));
    public static final ModuleType MACHINE_CORE_STEAM_BOILER = 
        addModuleType(new ModuleType("machine_core_steam_boiler", SLOT_MACHINE_CORE, ModModuleData.TYPE_STEAM_BOILER));
    public static final ModuleType MACHINE_CORE_PRESSER = addModuleType(new ModuleType("machine_core_presser", SLOT_MACHINE_CORE, ModModuleData.TYPE_PRESSER));
    public static final ModuleType MACHINE_CORE_WATER_PUMPER = 
        addModuleType(new ModuleType("machine_core_water_pumper", SLOT_MACHINE_CORE, ModModuleData.TYPE_WATER_PUMPER));
    public static final ModuleType HEATING_CORE_SOLID_FUEL = 
        addModuleType(new ModuleType("heating_core_solid_fuel", SLOT_HEATING_CORE, ModModuleData.HEAT_SOURCE_SOLID_FUEL));
    public static final ModuleType ENERGY_CORE_STEAM = addModuleType(new ModuleType("energy_core_steam", SLOT_POWER_CORE, ModModuleData.ENERGY_SOURCE_STEAM));
    public static final ModuleType UPGRADE_SMELTER_BLASTING = 
        addModuleType(new ModuleType("upgrade_smelter_blasting", UPGRADE_SLOT_SMELTER, ModModuleData.UPGRADE_BLASTING));

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
        SLOT_MACHINE_BASE.setMaterialForm(MaterialForm.HULL);
        UPGRADE_SLOT_SMELTER.addSubType(UPGRADE_SLOT_MACHINE_CORE);
        SLOT_ENERGY_CORE.addSubType(SLOT_HEATING_CORE);
        SLOT_ENERGY_CORE.addSubType(SLOT_POWER_CORE);
        MACHINE_CORE_SMELTER.addSubSlot("energy_core", SLOT_ENERGY_CORE);
        MACHINE_CORE_SMELTER.setUpgradeType(UPGRADE_SLOT_SMELTER);
        MACHINE_CORE_ALLOYER.addSubSlot("energy_core", SLOT_ENERGY_CORE);
        MACHINE_CORE_STEAM_BOILER.addSubSlot("heating_core", SLOT_HEATING_CORE);
        MACHINE_CORE_STEAM_BOILER.setModuleTier(1);
        MACHINE_CORE_COKER.addSubSlot("energy_core", SLOT_POWER_CORE);
        MACHINE_CORE_COKER.setModuleTier(1);
        MACHINE_CORE_PRESSER.addSubSlot("power_core", SLOT_POWER_CORE);
        MACHINE_CORE_PRESSER.setModuleTier(1);
        MACHINE_CORE_WATER_PUMPER.setModuleTier(1);
    }
}
