package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.minecraftmod.meeptech.ModMaterials;
import com.minecraftmod.meeptech.ModModuleTypes;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.module.ModuleType;
import com.minecraftmod.meeptech.logic.ui.SlotUIElement;
import com.minecraftmod.meeptech.logic.ui.UIModule;
import com.minecraftmod.meeptech.logic.ui.UIModuleType;

public class MachineData {
    private MachineBase base;
    private MachineType type;
    private HeatSource heatSource;
    private HashMap<MachineComponent, String> components = new HashMap<>();
    private HashMap<MachineStat, Object> stats = new HashMap<>();
    private HashMap<UIModuleType, UIModule> uiModules = new HashMap<>();
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
                    uiModules.put(UIModuleType.Input, machineType.getInputUI());
                    uiModules.put(UIModuleType.Output, machineType.getOutputUI());
                    uiModules.put(UIModuleType.Recipe, machineType.getRecipeUI());
                } else if (attribute instanceof HeatSource machineHeatSource) {
                    heatSource = machineHeatSource;
                    uiModules.put(UIModuleType.Energy, heatSource.getEnergyUI());
                } else if (attribute instanceof MachineComponent component) {
                    ArrayList<String> newPath = new ArrayList<>(path);
                    newPath.add(id);
                    String materialId = data.get(newPath);
                    components.put(component, materialId);
                }
            }
        }
        for (MachineComponent component : components.keySet()) {
            Material material = ModMaterials.getMaterial(components.get(component));
            Object statResult = component.performCalculations(material);
            stats.put(component.getMachineStat(), statResult);
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
        if (stats.containsKey(MachineStat.Speed)) return (double)stats.get(MachineStat.Speed);
        return Double.NaN;
    }
    public int getSlotCount() {
        int count = 0;
        for (UIModule uiModule : uiModules.values()) {
            count += uiModule.getSlotCount();
        }
        return count;
    }
    public ArrayList<SlotUIElement> getSlots() {
        ArrayList<SlotUIElement> slots = new ArrayList<>();
        for (UIModule uiModule : uiModules.values()) {
            slots.addAll(uiModule.getSlots());
        }
        return slots;
    }
}
