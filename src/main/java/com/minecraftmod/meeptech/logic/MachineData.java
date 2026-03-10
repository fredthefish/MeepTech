package com.minecraftmod.meeptech.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.minecraftmod.meeptech.ModMaterials;
import com.minecraftmod.meeptech.ModModuleTypes;
import com.minecraftmod.meeptech.logic.MachineAttributes.HeatSource;
import com.minecraftmod.meeptech.logic.MachineAttributes.MachineAttribute;
import com.minecraftmod.meeptech.logic.MachineAttributes.MachineBase;
import com.minecraftmod.meeptech.logic.MachineAttributes.MachineComponent;
import com.minecraftmod.meeptech.logic.MachineAttributes.MachineStat;
import com.minecraftmod.meeptech.logic.MachineAttributes.MachineType;

public class MachineData {
    private MachineBase base;
    private MachineType type;
    private MachineAttributes.HeatSource heatSource;
    private HashMap<MachineComponent, String> components = new HashMap<>();
    public MachineData(HashMap<ArrayList<String>, String> data) {
        for (Entry<ArrayList<String>, String> entry : data.entrySet()) {
            ArrayList<String> path = entry.getKey();
            String id = entry.getValue();
            ModuleType module = ModModuleTypes.getModuleType(id);
            if (module != null) {
                MachineAttribute attribute = module.getAttribute();
                if (attribute instanceof MachineBase machineBase) {
                    base = machineBase;
                } else if (attribute instanceof MachineType machineType) {
                    type = machineType;
                } else if (attribute instanceof HeatSource machineHeatSource) {
                    heatSource = machineHeatSource;
                } else if (attribute instanceof MachineComponent component) {
                    ArrayList<String> newPath = new ArrayList<>(path);
                    newPath.add(id);
                    String materialId = data.get(newPath);
                    components.put(component, materialId);
                }
            } else {
            }
        }
    }
    public MachineBase getBase() {
        return base;
    }
    public MachineType getType() {
        return type;
    }
    public MachineAttribute getEnergySource() {
        //Temporary until other energy sources are added.
        return heatSource;
    }
    public double getMachineSpeed() {
        for (MachineComponent component : components.keySet()) {
            if (component.getMachineStat() == MachineStat.Speed) {
                Material material = ModMaterials.getMaterial(components.get(component));
                Object result = component.performCalculations(material);
                return (double)result;
            }
        }
        return Double.NaN;
    }
    public HashMap<MachineComponent, String> getComponents() {
        return components;
    }
}
