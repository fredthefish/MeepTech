package com.minecraftmod.meeptech;

import java.util.ArrayList;

import com.minecraftmod.meeptech.logic.MachineComponent;
import com.minecraftmod.meeptech.logic.MachineStat;
import com.minecraftmod.meeptech.logic.MaterialForm;
import com.minecraftmod.meeptech.logic.MaterialStat;

public class ModMachineComponents {
    public static final ArrayList<MachineComponent> MACHINE_COMPONENTS = new ArrayList<MachineComponent>();

    public static final MachineComponent TEMPERATURE_HULL_CASING = 
        addMachineComponent(new MachineComponent("temperature_hull_casing", "Thermal Hull Casing", MaterialForm.Hull, 1));
    public static final MachineComponent INSULATIVE_FIREBOX = 
        addMachineComponent(new MachineComponent("insulative_firebox", "Insulative Firebox", MaterialForm.Box, 1));
    public static final MachineComponent THERMAL_DIFFUSER = 
        addMachineComponent(new MachineComponent("thermal_diffuser_rack", "Thermal Diffuser Rack", MaterialForm.LargePlate, 1));

    public static MachineComponent addMachineComponent(MachineComponent component) {
        MACHINE_COMPONENTS.add(component);
        return component;
    }

    public static void InitializeMachineComponents() {
        TEMPERATURE_HULL_CASING.addRelevantStat(MaterialStat.MeltingPoint);
        TEMPERATURE_HULL_CASING.addOutputStat(MachineStat.Speed);
        TEMPERATURE_HULL_CASING.setCalculations((inputs) -> {
            if (inputs[0] instanceof Double meltingPoint) {
                double baseTemperature = 1000;
                double baseTime = 10;
                double rate = (meltingPoint * meltingPoint) / (baseTemperature * baseTemperature * baseTime);
                Object[] result = {rate};
                return result;
            }
            return null;
        });
        INSULATIVE_FIREBOX.addRelevantStat(MaterialStat.ThermalResistance);
        INSULATIVE_FIREBOX.addOutputStat(MachineStat.HeatDecay);
        INSULATIVE_FIREBOX.setCalculations((inputs) -> {
            if (inputs[0] instanceof Double thermalResistance) {
                double baseTime = 10;
                double baseEfficiency = 2;
                double rate = 1.0 / baseTime / baseEfficiency / thermalResistance;
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
