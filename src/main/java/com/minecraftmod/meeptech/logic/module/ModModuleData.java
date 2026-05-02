package com.minecraftmod.meeptech.logic.module;

import java.util.List;

import com.minecraftmod.meeptech.logic.machine.EnergySourceType;
import com.minecraftmod.meeptech.logic.machine.EnergySource;
import com.minecraftmod.meeptech.logic.machine.MachineBase;
import com.minecraftmod.meeptech.logic.machine.MachineType;
import com.minecraftmod.meeptech.logic.machine.MachineUpgrade;
import com.minecraftmod.meeptech.logic.recipe.ModMachineRecipes;
import com.minecraftmod.meeptech.logic.ui.EnergyUIModule;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;

public class ModModuleData {
    public static MachineBase BASE_BASIC = new MachineBase("basic_hull", 0, 0);
    public static MachineBase BASE_BRONZE = new MachineBase("bronze_hull", 1, 1);
    public static MachineBase BASE_IRON = new MachineBase("iron_hull", 2, 1);
    
    public static MachineType TYPE_SMELTER = new MachineType("smelter", EnergySourceType.Heat, ModMachineRecipes.SMELTER);
    public static MachineType TYPE_ALLOYER = new MachineType("alloyer", EnergySourceType.Heat, ModMachineRecipes.ALLOYER);
    public static MachineType TYPE_STEAM_BOILER = new MachineType("boiler", EnergySourceType.Heat, ModMachineRecipes.BOILER);
    
    public static EnergySource HEAT_SOURCE_SOLID_FUEL = new EnergySource("solid_fuel", ModMachineRecipes.SOLID_FUEL, EnergySourceType.Heat,
        new EnergyUIModule("Heat", 1, 0, true),
        List.of(TrackedStat.HeatLeft));
    public static EnergySource ENERGY_SOURCE_STEAM = new EnergySource("steam", ModMachineRecipes.STEAM_ENERGY, EnergySourceType.Steam,
        new EnergyUIModule("Steam", 0, 1, true),
        List.of(TrackedStat.HeatLeft));

    public static MachineUpgrade UPGRADE_BLASTING = new MachineUpgrade("blasting");
}
