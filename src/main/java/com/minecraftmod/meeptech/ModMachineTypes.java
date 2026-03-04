package com.minecraftmod.meeptech;

import java.util.ArrayList;

import com.minecraftmod.meeptech.logic.MachineType;

public class ModMachineTypes {
    public static final ArrayList<MachineType> MACHINE_TYPES = new ArrayList<MachineType>();

    public static final MachineType PRIMITIVE_SMELTER = addMachineType(new MachineType("primitive_smelter"));

    public static MachineType addMachineType(MachineType type) {
        MACHINE_TYPES.add(type);
        return type;
    }
    
    public static void InitializeMachineTypes() {
        PRIMITIVE_SMELTER.addMachineComponent(ModMachineComponents.TEMPERATURE_HULL_CASING);
        PRIMITIVE_SMELTER.addMachineComponent(ModMachineComponents.INSULATIVE_FIREBOX);
        PRIMITIVE_SMELTER.addMachineComponent(ModMachineComponents.THERMAL_DIFFUSER);
    }
}
