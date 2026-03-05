package com.minecraftmod.meeptech;

import java.util.ArrayList;
import java.util.HashMap;

import com.minecraftmod.meeptech.logic.MachineComponent;
import com.minecraftmod.meeptech.logic.MachineStat;
import com.minecraftmod.meeptech.logic.MaterialForm;
import com.minecraftmod.meeptech.logic.MaterialStat;

public class ModMachineComponents {
    public static final ArrayList<MachineComponent> MACHINE_COMPONENTS = new ArrayList<MachineComponent>();

    public static final MachineComponent TEMPERATURE_HULL_CASING = 
        addMachineComponent(new MachineComponent("temperature_hull_casing", MaterialForm.Hull, 1));
    public static final MachineComponent INSULATIVE_FIREBOX = 
        addMachineComponent(new MachineComponent("insulative_firebox", MaterialForm.Box, 1));
    public static final MachineComponent THERMAL_DIFFUSER = 
        addMachineComponent(new MachineComponent("thermal_diffuser_rack", MaterialForm.LargePlate, 1));

    public static final HashMap<MachineStat, String> MACHINE_STAT_TRANSLATION_KEYS = new HashMap<>();

    public static MachineComponent addMachineComponent(MachineComponent component) {
        MACHINE_COMPONENTS.add(component);
        return component;
    }

    public static void InitializeMachineComponents() {
        MACHINE_STAT_TRANSLATION_KEYS.put(MachineStat.Speed, "meeptech.machineStat.speed");
        MACHINE_STAT_TRANSLATION_KEYS.put(MachineStat.Efficiency, "meeptech.machineStat.efficiency");
        MACHINE_STAT_TRANSLATION_KEYS.put(MachineStat.HeatDecay, "meeptech.machineStat.heat_decay");

        TEMPERATURE_HULL_CASING.addRelevantStat(MaterialStat.MeltingPoint);
        TEMPERATURE_HULL_CASING.addOutputStat(MachineStat.Speed);
        TEMPERATURE_HULL_CASING.setCalculations((inputs) -> {
            if (inputs[0] instanceof Double meltingPoint) {
                double baseTemperature = 1000;
                double rate = (meltingPoint * meltingPoint) / (baseTemperature * baseTemperature);
                Object[] result = {rate};
                return result;
            }
            return null;
        });
        INSULATIVE_FIREBOX.addRelevantStat(MaterialStat.ThermalResistance);
        INSULATIVE_FIREBOX.addOutputStat(MachineStat.HeatDecay);
        INSULATIVE_FIREBOX.setCalculations((inputs) -> {
            if (inputs[0] instanceof Double thermalResistance) {
                double baseEfficiency = 2;
                double rate = 1.0 / baseEfficiency / thermalResistance;
                Object[] result = {rate};
                return result;
            }
            return null;
        });
        THERMAL_DIFFUSER.addRelevantStat(MaterialStat.ThermalConductivity);
        THERMAL_DIFFUSER.addOutputStat(MachineStat.Efficiency);
        THERMAL_DIFFUSER.setCalculations((inputs) -> {
            if (inputs[0] instanceof Double thermalConductivity) {
                double baseRatio = 3.0 / 2.0 / Math.sqrt(2);
                double efficiency = 1.0 - baseRatio / Math.sqrt(thermalConductivity);
                Object[] result = {efficiency};
                return result;
            }
            return null;
        });
    }
}
