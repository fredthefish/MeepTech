package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.minecraftmod.meeptech.ModMaterials;
import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.module.ModuleType;
import com.minecraftmod.meeptech.logic.ui.SlotUIElement;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModule;
import com.minecraftmod.meeptech.logic.ui.UIModuleType;

public class MachineData {
    private static final List<UIModuleType> typeOrder = List.of(UIModuleType.Input, UIModuleType.Output, UIModuleType.Energy, UIModuleType.Recipe);

    private MachineBase base;
    private MachineType type;
    private HeatSource heatSource;
    private HashMap<MachineComponent, String> components = new HashMap<>();
    private HashMap<MachineStat, Object> stats = new HashMap<>();
    private HashMap<UIModuleType, UIModule> uiModules = new HashMap<>();
    private ArrayList<TrackedStat> trackedStats = new ArrayList<>();
    public MachineData(MachineConfigData data) {
        constructFromLayer(data);
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
    public List<SlotUIElement> getSlots() {
        ArrayList<SlotUIElement> slots = new ArrayList<>();
        for (UIModuleType type : typeOrder) {
            UIModule module = uiModules.get(type);
            if (module != null) slots.addAll(module.getSlots());
        }
        return slots;
    }
    public int getStartSlot(UIModuleType type) {
        int count = 0;
        for (UIModuleType eachType : typeOrder) {
            if (type == eachType) return count;
            UIModule module = uiModules.get(eachType);
            if (module != null) count += module.getSlotCount();
        }
        throw new IllegalStateException("Could not find UIModuleType type in MachineData typeOrder.");
    }
    public List<UIModule> getUiModules() {
        return new ArrayList<>(uiModules.values());
    }
    public List<TrackedStat> getTrackedStats() {
        return trackedStats;
    }
    public void constructFromLayer(MachineConfigData layer) {
        ModuleType module = layer.getModuleType();
        if (module != null) {
            MachineAttribute attribute = module.getAttribute();
            if (attribute instanceof MachineBase machineBase) {
                base = machineBase;
            } else if (attribute instanceof MachineType machineType) {
                type = machineType;
                uiModules.put(UIModuleType.Input, machineType.getInputUI());
                uiModules.put(UIModuleType.Output, machineType.getOutputUI());
                uiModules.put(UIModuleType.Recipe, machineType.getRecipeUI());
                trackedStats.addAll(machineType.getTrackedStats());
            } else if (attribute instanceof HeatSource machineHeatSource) {
                heatSource = machineHeatSource;
                uiModules.put(UIModuleType.Energy, heatSource.getEnergyUI());
                trackedStats.addAll(machineHeatSource.getTrackedStats());
            } else if (attribute instanceof MachineComponent component) {
                components.put(component, layer.getMaterial());
            }
            for (int i = 0; i < module.getSubSlotCount(); i++) {
                constructFromLayer(layer.getSubLayer(i));
            }
        }
    }
}
