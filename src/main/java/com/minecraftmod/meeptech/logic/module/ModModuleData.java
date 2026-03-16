package com.minecraftmod.meeptech.logic.module;

import java.util.List;

import com.minecraftmod.meeptech.logic.machine.EnergySourceType;
import com.minecraftmod.meeptech.logic.machine.HeatSource;
import com.minecraftmod.meeptech.logic.machine.MachineBase;
import com.minecraftmod.meeptech.logic.machine.MachineComponent;
import com.minecraftmod.meeptech.logic.machine.MachineStat;
import com.minecraftmod.meeptech.logic.machine.MachineType;
import com.minecraftmod.meeptech.logic.material.MaterialStat;
import com.minecraftmod.meeptech.logic.recipe.ModMachineRecipes;
import com.minecraftmod.meeptech.logic.ui.EnergyUIModule;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;

public class ModModuleData {
    public static MachineBase BASE_BASIC = new MachineBase("basic", 1);
    
    public static MachineType TYPE_SMELTER = new MachineType("smelter", EnergySourceType.Heat, ModMachineRecipes.SMELTER);

    public static HeatSource HEAT_SOURCE_SOLID_FUEL = new HeatSource("solid_fuel", ModMachineRecipes.SOLID_FUEL,
        new EnergyUIModule("Heat", 1, true),
        List.of(TrackedStat.HeatLeft)
    );

    public static MachineComponent COMPONENT_FIREBOX = new MachineComponent("firebox", MachineStat.SPEED, MaterialStat.MeltingPoint, 
        (stat) -> {
            if (stat instanceof Double meltingPoint) {
                double baseTemperature = 1000;
                double rate = (meltingPoint * meltingPoint) / (baseTemperature * baseTemperature);
                return rate;
            }
            return null;
    });
}
